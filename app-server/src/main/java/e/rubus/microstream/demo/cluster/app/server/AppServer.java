package e.rubus.microstream.demo.cluster.app.server;

import be.rubus.microstream.demo.cluster.ServerConfig;
import be.rubus.microstream.demo.cluster.app.model.DataRoot;
import be.rubus.microstream.demo.cluster.app.model.Product;
import be.rubus.microstream.demo.cluster.server.MicroStreamServer;
import one.microstream.storage.types.StorageManager;

public class AppServer {
    public static void main(String[] args) {

        DataRoot dataRoot = new DataRoot();
        dataRoot.addProduct(new Product("Ba", "Banana", "A curvy, yellow fruit", 3));
        StorageManager storageManager = StorageManagerProducer.getAndStartStorageManager(dataRoot, "data", 1);

        ServerConfig serverConfig = new ServerConfig();
        new MicroStreamServer(serverConfig, dataRoot, new ServerDataCallback(dataRoot, storageManager));
    }
}
