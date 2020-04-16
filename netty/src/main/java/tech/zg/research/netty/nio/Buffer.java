package tech.zg.research.netty.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class Buffer {

    public static void main(String[] args) throws IOException {
        //intBuffer();
        //bufferPutGet();
        //readOnlyBuffer();
        //mappedBuffer();
        ScatteringAndGathering();
    }

    /**
     * 数据写入buffer时，可以采用buffer数组，依次写入   =   分散
     * 可以从buffer数组中依次读出   =   聚合
     */
    private static void ScatteringAndGathering() throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(6666);
        serverSocketChannel.bind(inetSocketAddress);
        SocketChannel socketChannel = serverSocketChannel.accept();

        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        while (true) {
            long read = 0;
            while (read < 8) {

                Arrays.asList(byteBuffers).forEach(buffer -> buffer.clear());

                long read1 = socketChannel.read(byteBuffers);
                read += read1;

                log.info("read length -{} ", read);

                Arrays.asList(byteBuffers).stream().map(
                        buffer -> "position=" + buffer.position() + ", limit=" + buffer.limit()
                ).collect(Collectors.toList()).forEach(msg -> log.info("{}", msg));
            }

            // 将所有的buffer翻转
            Arrays.asList(byteBuffers).forEach(buffer -> buffer.flip());

            // 将所有的数据读出
            long write = 0;
            while (write < 8) {
                long write1 = socketChannel.write(byteBuffers);
                write += write1;
            }
        }
    }

    /**
     * 读取到文件后直接在内存中修改，不需要再进行拷贝
     * 堆外内存，比较快
     */
    private static void mappedBuffer() throws IOException {

        RandomAccessFile randomAccessFile = new RandomAccessFile("d:\\test.txt", "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();

        /**
         * param1, 读写模式
         * param2, 映射起始位置
         * param3, 映射的内存大小
         * mappedByteBuffer 实际类型是 DirectByteBuffer
         */
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

        mappedByteBuffer.put(0, (byte) 'n');
        mappedByteBuffer.put(1, (byte) 'h');

        randomAccessFile.close();
    }

    private static void readOnlyBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        ByteBuffer readOnlyBuffer = byteBuffer.asReadOnlyBuffer();
        log.info("{}", readOnlyBuffer.isReadOnly());
        log.info("{}", readOnlyBuffer.getClass());
        // 报错
        readOnlyBuffer.put((byte) 1);
    }

    private static void bufferPutGet() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.putInt(1);
        byteBuffer.putLong(2L);

        byteBuffer.flip();

        // 这样取异常
        /*log.info(byteBuffer.getInt(0) + "");
        log.info(byteBuffer.getLong(1) + "");*/

        log.info(byteBuffer.getInt() + "");
        log.info(byteBuffer.getLong() + "");
    }

    private static void intBuffer() {
        IntBuffer intBuffer = IntBuffer.allocate(5);
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i * 2);
        }

        // 读取数据要flip
        intBuffer.flip();

        while (intBuffer.hasRemaining()) {
            log.info(intBuffer.get() + "");
        }

        for (int i = 0; i < intBuffer.capacity(); i++) {
            log.info(intBuffer.get(i) + "");
        }
    }
}
