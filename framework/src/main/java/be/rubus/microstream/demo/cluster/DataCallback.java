package be.rubus.microstream.demo.cluster;

/**
 * Callback that needs to be implemented for each application when data is sent between client and server and vice versa.
 */
@FunctionalInterface
public interface DataCallback {

    void accept(Object obj);
}
