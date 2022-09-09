package be.rubus.microstream.demo.cluster.app.client.service;

import be.rubus.microstream.demo.cluster.app.client.microstream.AppClient;
import be.rubus.microstream.demo.cluster.app.model.Product;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductService {

    @Inject
    private AppClient client;


    public List<Product> getAllProducts() {
        return client.getAllProducts();
    }

    /**
     * @param product
     * @return False when product already exists (same id).
     */
    public boolean addProduct(Product product) {
        List<Product> products = client.getAllProducts();
        Optional<Product> existingProduct = products.stream().filter(p -> p.getId().equals(product.getId()))
                .findAny();
        if (existingProduct.isEmpty()) {
            client.addOrUpdateProduct(product);
        }
        return existingProduct.isEmpty();
    }

    /**
     * @param product
     * @return False when product not found (same id).
     */
    public boolean updateProduct(Product product) {
        List<Product> products = client.getAllProducts();
        Optional<Product> existingProduct = products.stream().filter(p -> p.getId().equals(product.getId()))
                .findAny();
        if (existingProduct.isPresent()) {
            client.addOrUpdateProduct(product);
        }
        return existingProduct.isPresent();
    }

    public void deleteProduct(String productId) {
        List<Product> products = client.getAllProducts();
        Optional<Product> existingProduct = products.stream().filter(p -> p.getId().equals(productId))
                .findAny();
        existingProduct.ifPresent(product -> client.deleteProduct(product));
    }
}
