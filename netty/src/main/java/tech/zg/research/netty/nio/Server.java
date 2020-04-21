package tech.zg.research.netty.nio;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("Duplicates")
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {
        // 创建 server
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // server bind 端口
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        // server 注册非阻塞
        serverSocketChannel.configureBlocking(Boolean.FALSE);

        // 创建一个 selector
        Selector selector = Selector.open();
        // server 注册到selector 只关心 accept 事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            // 一秒内有事件，则处理
            if (selector.select(1000) > 0) {
                // 获取到有事件的selectionKey集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                if (CollectionUtils.isNotEmpty(selectionKeys)) {
                    Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
                    while (selectionKeyIterator.hasNext()) {
                        SelectionKey selectionKey = selectionKeyIterator.next();
                        // 遍历处理
                        try {
                            handle(serverSocketChannel, selector, selectionKey);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.info(e.getMessage());
                            // 把异常的 socketChannel 关闭掉
                            SelectableChannel selectableChannel = selectionKey.channel();
                            if (selectableChannel instanceof SocketChannel) {
                                SocketChannel socketChannel = (SocketChannel) selectableChannel;
                                socketChannel.close();
                            }
                        } finally {
                            selectionKeyIterator.remove();
                        }
                    }
                }
            }
            log.info("等待事件中......");
        }

    }

    private static void handle(ServerSocketChannel serverSocketChannel, Selector selector, SelectionKey selectionKey) throws IOException {
        if (selectionKey.isAcceptable()) {
            // accept 事件不能用 selectionKey.channel 来获取，因为 accept 是 serverSocketChannel 的事件，所以获取到的是 serverSocketChannel
            // 当然也可以通过 selectionKey.channel 获取 serverSocketChannel， 再通过 serverSocketChannel.accept() 来获取 SocketChannel
            // 但是我们能够直接拿到 serverSocketChannel 就不必多此一举了
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(Boolean.FALSE);
            socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
        } else if (selectionKey.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();
            while (socketChannel.read(byteBuffer) > 0) {
                log.info("读取中");
            }
            log.info("读取到数据:{}", new String(byteBuffer.array()));
            byteBuffer.clear();
        }
    }
}
