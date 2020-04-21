package tech.zg.research.netty.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

@Slf4j
public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(Boolean.FALSE);
        socketChannel.bind(new InetSocketAddress(7777));
        InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 6666);

        if (!socketChannel.connect(serverAddress)) {
            while (!socketChannel.finishConnect()) {

                log.info("客户端连接中......");
            }
        }

        String message = "hello netty";
        ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes());
        socketChannel.write(byteBuffer);
    }
}
