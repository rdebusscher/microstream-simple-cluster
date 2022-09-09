package be.rubus.microstream.demo.cluster.app.client.microstream;

import be.rubus.microstream.demo.cluster.DataCallback;
import be.rubus.microstream.demo.cluster.app.model.DataRoot;
import be.rubus.microstream.demo.cluster.app.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ClientDataCallback implements DataCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDataCallback.class);

    private final DataRoot root;

    public ClientDataCallback(DataRoot root) {
        this.root = root;
    }

    @Override
    public void accept(Object obj) {
        LOGGER.info("Processing object of type {} within DataCallback", obj.getClass().getName());
        if (obj instanceof DataRoot) {
            DataRoot remoteRoot = (DataRoot) obj;
            root.resetDataRoot(remoteRoot.getProducts());
        }
        if (obj instanceof Product) {
            Product product = (Product) obj;
            root.addProduct(product);
        }

        if (obj instanceof List) {
            List<String> productIds = (List<String>) obj;

            List<Product> products = root.getProducts();

            List<Product> removedProducts = products.stream().filter(p -> !productIds.contains(p.getId()))
                    .collect(Collectors.toList());
            removedProducts.forEach(root::removeProduct);
        }
    }
}
