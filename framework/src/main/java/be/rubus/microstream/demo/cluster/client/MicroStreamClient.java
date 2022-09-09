package be.rubus.microstream.demo.cluster.client;

import be.rubus.microstream.demo.cluster.ClientServerConfig;
import be.rubus.microstream.demo.cluster.DataCallback;
import be.rubus.microstream.demo.cluster.util.ChannelUtil;
import one.microstream.communication.binarydynamic.ComBinaryDynamic;
import one.microstream.communication.types.ComChannel;
import one.microstream.communication.types.ComClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

public class MicroStreamClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicroStreamClient.class);

    private final BlockingQueue<Object> changedObjects = new ArrayBlockingQueue<>(10);
    private final DataCallback callback;

    public MicroStreamClient(String clientId, ClientServerConfig clientServerConfig, DataCallback callback) {
        this.callback = callback;
        try {
            AsynchronousSocketChannel channel = createChannel(clientServerConfig);
            ChannelUtil.write(channel, clientId);
            String serverPort = ChannelUtil.read(channel);
            channel.close(); // Channel no longer required
            startMicroStreamProducer(clientServerConfig, Integer.parseInt(serverPort));

        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void startMicroStreamProducer(ClientServerConfig clientServerConfig, int port) {
        LOGGER.debug("Start connection to server on port {}", port);
        ComClient<?> client = ComBinaryDynamic.Client(new InetSocketAddress(clientServerConfig.getHostname(), port));
        // TODO Cleanup of this Channel and proper handling when other party drops the connection
        ComChannel channel = client.connect(10, Duration.ofMillis(500));
        new Thread(() -> {
            while (true) {
                try {
                    Object object = changedObjects.take();
                    LOGGER.debug("Send Object to the server : {}", object);
                    channel.send(object);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                Object obj = channel.receive();
                callback.accept(obj);
            }
        }).start();
    }

    private AsynchronousSocketChannel createChannel(ClientServerConfig clientServerConfig) throws IOException, ExecutionException, InterruptedException {
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(clientServerConfig.getHostname(), clientServerConfig.getMainPort());
        client.connect(address).get();
        return client;
    }

    public void addChangedObject(Object data) {
        changedObjects.add(data);
    }

}
