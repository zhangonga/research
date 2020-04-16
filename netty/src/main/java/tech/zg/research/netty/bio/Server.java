package tech.zg.research.netty.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Server {

    private static final ThreadPoolExecutor socketServerThreadPool = new ThreadPoolExecutor(
            10,
            50,
            60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            runnable -> {
                Thread thread = new Thread(runnable);
                thread.setName("socketServerThreadPool" + ThreadLocalRandom.current().nextInt(1000));
                return thread;
            }, (runnable, threadPoolExecutor) -> log.error("socketServerThreadPool reject-{}" + runnable.toString()));

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(6666);
        while (true) {
            log.info("线程-{}-等待连接", Thread.currentThread().getName());
            final Socket acceptSocket = serverSocket.accept();

            socketServerThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    handle(acceptSocket);
                }
            });
        }
    }

    private static void handle(Socket acceptSocket) {

        log.info("线程-{}-处理连接", Thread.currentThread().getName());
        try (InputStream inputStream = acceptSocket.getInputStream();) {
            byte[] bytes = new byte[1024];
            while (true) {
                log.info("线程-{}-接收数据", Thread.currentThread().getName());
                int read = inputStream.read(bytes);
                if (read != -1) {
                    log.info("线程-{}-打印数据-{}", Thread.currentThread().getName(), new String(bytes, 0, read));
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
