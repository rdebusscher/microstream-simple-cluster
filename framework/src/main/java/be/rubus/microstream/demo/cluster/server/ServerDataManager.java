package be.rubus.microstream.demo.cluster.server;

import be.rubus.microstream.demo.cluster.DataCallback;
import one.microstream.communication.binarydynamic.ComBinaryDynamic;
import one.microstream.communication.types.ComConnection;
import one.microstream.communication.types.ComHost;
import one.microstream.communication.types.ComHostChannel;
import one.microstream.communication.types.ComHostChannelAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ServerDataManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerDataManager.class);

    private final int startPortForClients;
    private final Object root;

    private final DataCallback dataCallback;

    private final Map<String, Integer> clientMappings = new HashMap<>();
    private final Map<String, ComHost<ComConnection>> clientHosts = new HashMap<>();
    private final Map<String, ComHostChannel<ComConnection>> clientChannels = new HashMap<>();

    public ServerDataManager(Object root, int startPortForClients, DataCallback dataCallback) {
        this.root = root;
        this.startPortForClients = startPortForClients;
        this.dataCallback = dataCallback;
    }

    public synchronized int startStartChannelFor(String clientId) {
        int portForClient = getPortForClient(clientId);
        ComHostChannelAcceptor<ComConnection> serverAcceptorThread = new ServerAcceptorThread(root, clientId);
        ComHost<ComConnection> host = ComBinaryDynamic.Foundation()
                .setPort(portForClient)
                .setHostChannelAcceptor(serverAcceptorThread)
                .createHost();

        // run the host, making it constantly listen for new connections and relaying them to the logic
        new Thread(host).start();
        LOGGER.debug("Started a MicroStream ComHost on port {} for client {}", portForClient, clientId);
        return portForClient;
    }

    private int getPortForClient(String clientId) {
        if (clientHosts.containsKey(clientId)) {
            clientHosts.remove(clientId).stop();
            clientChannels.remove(clientId).connection().close();
        }

        clientMappings.remove(clientId);
        int result = clientMappings.values().stream().mapToInt(v -> v).max().orElse(startPortForClients - 1) + 1;
        clientMappings.put(clientId, result);
        return result;

    }

    private synchronized void handleUpdate(String clientId, Object updated) {
        // First update to Root object
        dataCallback.accept(updated);
        for (Map.Entry<String, ComHostChannel<ComConnection>> channelEntry : clientChannels.entrySet()) {
            if (!clientId.equals(channelEntry.getKey())) {
                // Broadcast to other clients
                channelEntry.getValue().send(updated);
            }
        }
    }

    private class ServerAcceptorThread implements ComHostChannelAcceptor<ComConnection> {
        private final Object root;
        private final String clientId;

        public ServerAcceptorThread(Object root, String clientId) {

            this.root = root;
            this.clientId = clientId;
        }

        @Override
        public void acceptChannel(ComHostChannel<ComConnection> channel) {

            clientChannels.put(clientId, channel);
            channel.send(root);
            new Thread(() -> {
                Object received = channel.receive();

                handleUpdate(clientId, received);

            }).start();
        }
    }
}
