package tech.zg.research.netty.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * fileChannel
 * ServerSocketChannel
 * SocketChannel
 */
@Slf4j
public class Channel {

    public static void main(String[] args) throws IOException {
        //fileChannelWrite();
        //fileChannelRead();
        //fileChannelCopyFileUseOneBuffer();
        fileChannelTransferFromOrTo();
    }

    private static void fileChannelTransferFromOrTo() throws IOException {
        File inputFile = new File("d:\\test.txt");
        File outputFile = new File("d:\\test2.txt");

        FileInputStream fileInputStream = new FileInputStream(inputFile);
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

        FileChannel inChannel = fileInputStream.getChannel();
        FileChannel outChannel = fileOutputStream.getChannel();

        //outChannel.transferFrom(inChannel, 0, inChannel.size());
        inChannel.transferTo(0, inChannel.size(), outChannel);

        fileInputStream.close();
        fileOutputStream.close();
    }

    private static void fileChannelCopyFileUseOneBuffer() throws IOException {
        File inputFile = new File("d:\\test.txt");
        File outputFile = new File("d:\\text1.txt");

        FileInputStream fileInputStream = new FileInputStream(inputFile);
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

        FileChannel inChannel = fileInputStream.getChannel();
        FileChannel outChannel = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while (true) {
            byteBuffer.clear();

            int read = inChannel.read(byteBuffer);
            if (read == -1) {
                break;
            }

            byteBuffer.flip();

            outChannel.write(byteBuffer);
        }

        fileInputStream.close();
        fileOutputStream.close();
    }

    private static void fileChannelRead() throws IOException {
        File file = new File("d:\\test.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel channel = fileInputStream.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());

        channel.read(byteBuffer);

        log.info(new String(byteBuffer.array()));
        fileInputStream.close();
    }

    private static void fileChannelWrite() throws IOException {
        String str = "hello";

        FileOutputStream fileOutputStream = new FileOutputStream("d:\\test.txt");
        FileChannel channel = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(str.getBytes());

        byteBuffer.flip();

        channel.write(byteBuffer);

        fileOutputStream.close();
    }
}
