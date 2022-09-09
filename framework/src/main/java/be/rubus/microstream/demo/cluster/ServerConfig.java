package be.rubus.microstream.demo.cluster;

/**
 * Configuration of the server side in the replication.
 */
public class ServerConfig {

    // Client must connect to this port to 'register' themselves.
    private final int mainPort;

    // Start of the port range that are used by MicroStream ComChannels.
    private final int clientPorts;

    public ServerConfig() {
        this(9999, 30_000);
    }

    public ServerConfig(int mainPort, int clientPorts) {
        this.mainPort = mainPort;
        this.clientPorts = clientPorts;
    }

    public int getMainPort() {
        return mainPort;
    }

    public int getClientPorts() {
        return clientPorts;
    }
}
