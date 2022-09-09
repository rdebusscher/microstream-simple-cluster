package be.rubus.microstream.demo.cluster.app.client.microstream;

import be.rubus.microstream.demo.cluster.ClientServerConfig;
import be.rubus.microstream.demo.cluster.app.model.DataRoot;
import be.rubus.microstream.demo.cluster.app.model.Product;
import be.rubus.microstream.demo.cluster.client.MicroStreamClient;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AppClient {

    private MicroStreamClient microStreamClient;

    private DataRoot root;

    @PostConstruct
    private void init() {
        root = new DataRoot();

        String clientId = getClientId();
        ClientServerConfig config = new ClientServerConfig();
        microStreamClient = new MicroStreamClient(clientId, config, new ClientDataCallback(root));
    }

    private static String getClientId() {
        String clientId = System.getProperty("microstream.client.id");
        if (clientId == null) {
            clientId = "Client" + UUID.randomUUID();
        }
        return clientId;
    }

    public List<Product> getAllProducts() {
        root.waitForInitialization();
        return root.getProducts();
    }

    public void addOrUpdateProduct(Product product) {
        root.waitForInitialization();
        root.addProduct(product);
        microStreamClient.addChangedObject(product);
    }

    public void deleteProduct(Product product) {
        root.waitForInitialization();
        root.removeProduct(product);
        List<String> productIds = root.getProducts().stream().map(Product::getId).collect(Collectors.toList());
        microStreamClient.addChangedObject(productIds);
    }
}
