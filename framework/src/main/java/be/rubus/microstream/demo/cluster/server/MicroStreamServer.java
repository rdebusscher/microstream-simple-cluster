package be.rubus.microstream.demo.cluster.server;

import be.rubus.microstream.demo.cluster.DataCallback;
import be.rubus.microstream.demo.cluster.ServerConfig;
import be.rubus.microstream.demo.cluster.util.ChannelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

public class MicroStreamServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicroStreamServer.class);

    private final ServerConfig serverConfig;

    private final ServerDataManager serverDataManager;

    private AsynchronousServerSocketChannel serverSocket;

    public MicroStreamServer(ServerConfig serverConfig, Object root, DataCallback dataCallback) {
        this.serverConfig = serverConfig;
        serverDataManager = new ServerDataManager(root, serverConfig.getClientPorts(), dataCallback);
        start();
    }

    private void start() {
        try {
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(serverConfig.getMainPort()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        waitForConnection();
    }

    private void waitForConnection() {
        new Thread(() -> {
            try {
                LOGGER.info("Waiting for Client connections");
                AsynchronousSocketChannel channel = serverSocket.accept().get();

                String clientId = ChannelUtil.read(channel);
                LOGGER.info("Received connection from client {}", clientId);

                int portForClient = serverDataManager.startStartChannelFor(clientId);

                ChannelUtil.write(channel, String.valueOf(portForClient));

                channel.close();  // Channel no longer needed
            } catch (ExecutionException | InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
            waitForConnection();
        }).start();
    }


}
