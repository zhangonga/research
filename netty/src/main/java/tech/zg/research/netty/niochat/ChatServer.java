package tech.zg.research.netty.niochat;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("Duplicates")
@Slf4j
@Getter
@Setter
public class ChatServer {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private static final int SELECT_TIME_OUT = 2000;

    public static ChatServer open(int port) {
        try {
            ChatServer chatServer = new ChatServer();

            Selector selector = Selector.open();
            chatServer.setSelector(selector);

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(Boolean.FALSE);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            chatServer.setServerSocketChannel(serverSocketChannel);

            return chatServer;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void start() {
        while (true) {

            // 需要处理的事件数
            int selectCount = 0;
            try {
                selectCount = selector.select(SELECT_TIME_OUT);
            } catch (Exception e) {
                e.printStackTrace();
                log.info(e.getMessage());
            }

            // 如果有事件需要处理
            if (selectCount > 0) {
                // 获取到有事件的selectionKey集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> selectionKeyIt = selectionKeys.iterator();
                while (selectionKeyIt.hasNext()) {
                    SelectionKey selectionKey = selectionKeyIt.next();
                    // 遍历处理
                    try {
                        handle(selectionKey);
                    } catch (IOException e) {
                        e.printStackTrace();
                        log.info(e.getMessage());
                        // 把异常的 socketChannel 关闭掉
                        SelectableChannel selectableChannel = selectionKey.channel();
                        if (selectableChannel instanceof SocketChannel) {
                            SocketChannel socketChannel = (SocketChannel) selectableChannel;
                            try {
                                socketChannel.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                                log.info(e1.getMessage());
                            }
                        }
                    }
                    selectionKeyIt.remove();
                }
            }
            //log.info("服务器日志-等待事件中......");
        }
    }


    private void handle(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isAcceptable()) {
            SocketChannel socketChannel = this.serverSocketChannel.accept();
            socketChannel.configureBlocking(Boolean.FALSE);
            socketChannel.register(this.selector, SelectionKey.OP_READ);
            log.info("服务器日志-{}-{}", socketChannel.getRemoteAddress().toString().substring(1), "上线了");
        } else if (selectionKey.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            while (socketChannel.read(byteBuffer) > 0) {
                log.info("服务器日志-读取消息中......");
            }
            String msg = new String(byteBuffer.array());
            log.info("服务器日志-读取到数据-{}", msg);
            // 进行广播
            broadcast(socketChannel, msg);
        }
    }

    private void broadcast(SocketChannel socketChannelSelf, String msg) throws IOException {
        Set<SelectionKey> keys = this.selector.keys();
        ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
        for (SelectionKey selectionKey : keys) {
            SelectableChannel selectableChannel = selectionKey.channel();
            if (selectableChannel instanceof SocketChannel && selectableChannel != socketChannelSelf) {
                SocketChannel socketChannel = (SocketChannel) selectableChannel;
                log.info("发送给-{}", socketChannel.getRemoteAddress().toString().substring(1));
                byteBuffer.position(0);
                socketChannel.write(byteBuffer);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ChatServer chatServer = ChatServer.open(6666);
        if (chatServer != null) {
            chatServer.start();
        }
    }
}
