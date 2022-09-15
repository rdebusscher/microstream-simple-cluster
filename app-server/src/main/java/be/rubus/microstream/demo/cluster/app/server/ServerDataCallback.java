package be.rubus.microstream.demo.cluster.app.server;

import be.rubus.microstream.demo.cluster.DataCallback;
import be.rubus.microstream.demo.cluster.app.model.DataRoot;
import be.rubus.microstream.demo.cluster.app.model.Product;
import one.microstream.storage.types.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServerDataCallback implements DataCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerDataCallback.class);

    private final DataRoot root;
    private final StorageManager storageManager;

    public ServerDataCallback(DataRoot root, StorageManager storageManager) {
        this.root = root;
        this.storageManager = storageManager;
    }

    @Override
    public void accept(Object obj) {
        LOGGER.info("Processing object of type {} within DataCallback", obj.getClass().getName());

        if (obj instanceof DataRoot) {
            LOGGER.warn("Client should never send the Root !!");
        }
        if (obj instanceof Product) {
            addOrReplaceProduct((Product) obj);
        }
        if (obj instanceof List) {
            purgeRemovedProducts((List<String>) obj);
        }
    }

    private void purgeRemovedProducts(List<String> productIds) {
        List<Product> products = root.getProducts();

        List<Product> removedProducts = products.stream().filter(p -> !productIds.contains(p.getId()))
                .collect(Collectors.toList());
        removedProducts.forEach(root::removeProduct);

        storageManager.store(products);
    }

    private void addOrReplaceProduct(Product product) {
        List<Product> products = root.getProducts();
        Optional<Product> savedProduct = products.stream().filter(p -> product.getId().equals(p.getId()))
                .findAny();
        savedProduct.ifPresent(products::remove);
        products.add(product);

        storageManager.store(products);
        storageManager.store(product);  // TODO Check if this can be removed.
        // Even with update, the Ids are different and thus store of List will persist the updated Product.
    }
}
