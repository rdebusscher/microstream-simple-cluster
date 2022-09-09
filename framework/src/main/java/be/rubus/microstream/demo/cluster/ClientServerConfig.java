package be.rubus.microstream.demo.cluster;

import one.microstream.com.XSockets;

/**
 * Defines the configuration of the Server, used by the client.
 */
public class ClientServerConfig {
    private final String hostname;
    private final int mainPort;

    public ClientServerConfig() {
        this(XSockets.localHostSocketAddress().getHostName(), 9999);
    }

    public ClientServerConfig(String hostname, int mainPort) {
        this.hostname = hostname;
        this.mainPort = mainPort;
    }

    public String getHostname() {
        return hostname;
    }

    public int getMainPort() {
        return mainPort;
    }
}
