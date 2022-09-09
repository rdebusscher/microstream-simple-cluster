package be.rubus.microstream.demo.cluster.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Utility class for reading and writing from {@link AsynchronousSocketChannel}.
 */
public final class ChannelUtil {

    private ChannelUtil() {
    }

    public static void write(AsynchronousSocketChannel channel, String data) {
        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8));
        channel.write(buffer);
    }

    /**
     * Read from Async channels and only returns when some data is available and processed.
     *
     * @param channel
     * @return
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static String read(AsynchronousSocketChannel channel) throws ExecutionException, InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Future<Integer> readval = channel.read(buffer);
        readval.get();
        return new String(buffer.array()).trim();
    }
}
