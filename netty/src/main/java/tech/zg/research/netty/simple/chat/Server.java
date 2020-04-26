package tech.zg.research.netty.simple.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class Server {

    private int port = 8888;

    public static Server open() {
        Server server = new Server();
        return server;
    }

    public void run() {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup work = new NioEventLoopGroup(8);
        ServerBootstrap bootstrap = new ServerBootstrap();

        try {
            bootstrap.group(boss, work)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            // inBound 顺序执行
                            // inBound 逆序执行
                            // 最后一个要是 inBound
                            // idleStateHandler 心跳机制，如果超过配置的时间没有读写，则发送IdleStateEvent 事件给下一个handler， 下一个handler 的 userEventTriggered 方法接收到并处理
                            channel.pipeline().addLast(new IdleStateHandler(10, 10, 20, TimeUnit.SECONDS));
                            channel.pipeline().addLast(new HeartHandler());
                            channel.pipeline().addLast(new StringDecoder());
                            channel.pipeline().addLast(new StringEncoder());
                            channel.pipeline().addLast(new ServerHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        log.info("服务器启动成功");
                    } else {
                        log.info("服务器启动失败");
                    }
                }
            });
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            log.info("启动服务器异常：{}", e.getMessage());
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        Server server = Server.open();
        server.run();
    }
}
