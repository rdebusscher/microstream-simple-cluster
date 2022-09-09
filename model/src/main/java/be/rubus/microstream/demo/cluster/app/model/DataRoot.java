package be.rubus.microstream.demo.cluster.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DataRoot {

    private static final Object SYNC_LOCK = new Object();

    private static final Object LOCK = new Object();

    private final List<Product> products = new ArrayList<>();

    private CountDownLatch resetRootLatch;

    private boolean initialized = false;

    /**
     * Needed n the client side to wait until the server has sent the root and
     * data is processed.
     */
    public void waitForInitialization() {
        if (!initialized) {
            synchronized (SYNC_LOCK) {
                resetRootLatch = new CountDownLatch(1);
            }
            if (!initialized) {
                // Additional if as initialization might have happened already.
                try {
                    resetRootLatch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public List<Product> getProducts() {
        synchronized (LOCK) {
            return new ArrayList<>(products);
        }
    }

    /**
     * Required on the client side to reset root content and inform initialization is done.
     * @param products
     */
    public void resetDataRoot(List<Product> products) {
        synchronized (SYNC_LOCK) {
            this.products.clear();
            this.products.addAll(products);
            initialized = true;
            if (resetRootLatch != null) {
                resetRootLatch.countDown();
            }
        }

    }

    public void addProduct(Product product) {
        synchronized (LOCK) {
            if (products.contains(product)) {
                products.removeIf(p -> p.getId().equals(product.getId()));
            }
            products.add(product);
        }
    }

    public void removeProduct(Product product) {
        synchronized (LOCK) {
            products.removeIf(p -> p.getId().equals(product.getId()));

        }
    }
}
