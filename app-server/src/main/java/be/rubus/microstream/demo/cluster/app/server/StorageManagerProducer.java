package be.rubus.microstream.demo.cluster.app.server;

import be.rubus.microstream.demo.cluster.app.model.DataRoot;
import one.microstream.afs.nio.types.NioFileSystem;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.types.Storage;
import one.microstream.storage.types.StorageChannelCountProvider;
import one.microstream.storage.types.StorageConfiguration;
import one.microstream.storage.types.StorageManager;

public final class StorageManagerProducer {

    private StorageManagerProducer() {
    }

    public static StorageManager getAndStartStorageManager(DataRoot root, String storageTargetLocation, int channelCount) {
        NioFileSystem fileSystem = NioFileSystem.New();

        EmbeddedStorageFoundation<?> storageFoundation = EmbeddedStorageFoundation.New()
                .setConfiguration(
                        StorageConfiguration.Builder()
                                .setStorageFileProvider(
                                        Storage.FileProviderBuilder(fileSystem)
                                                .setDirectory(fileSystem.ensureDirectoryPath(storageTargetLocation))
                                                .createFileProvider()
                                )
                                .setChannelCountProvider(StorageChannelCountProvider.New(channelCount))
                                .createConfiguration()
                )
                .setRoot(root);

        return storageFoundation.start();
    }
}
