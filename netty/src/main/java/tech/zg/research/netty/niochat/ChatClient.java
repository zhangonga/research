package tech.zg.research.netty.niochat;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

@Slf4j
@Setter
@Getter
public class ChatClient {

    private Selector selector;
    private SocketChannel socketChannel;
    private String clientName;

    public static ChatClient open(String ip, int remotePort) {
        ChatClient chatClient = new ChatClient();
        try {
            Selector selector = Selector.open();
            chatClient.setSelector(selector);

            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(ip, remotePort));
            socketChannel.configureBlocking(Boolean.FALSE);
            socketChannel.register(selector, SelectionKey.OP_READ);
            chatClient.setSocketChannel(socketChannel);

            chatClient.setClientName(socketChannel.getLocalAddress().toString().substring(1));
            return chatClient;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendInfo(String message) {
        message = clientName + " " + message;
        try {
            this.socketChannel.write(ByteBuffer.wrap(message.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readInfo() {
        try {
            int select = this.selector.selectNow();
            if (select > 0) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(2014);
                Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
                Iterator<SelectionKey> selectionKeyIt = selectionKeys.iterator();
                while (selectionKeyIt.hasNext()) {
                    SelectionKey selectionKey = selectionKeyIt.next();
                    if (selectionKey.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        while (socketChannel.read(byteBuffer) > 0) {
                            continue;
                        }
                        log.info("接收到数据-{}", new String(byteBuffer.array()));
                    }
                    selectionKeyIt.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = ChatClient.open("127.0.0.1", 8888);
        if (chatClient == null) {
            log.info("启动失败");
        }

        // 启动一个线程读取信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    chatClient.readInfo();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();
            chatClient.sendInfo(nextLine);
        }
    }
}
