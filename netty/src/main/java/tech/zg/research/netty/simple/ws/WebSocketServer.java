package tech.zg.research.netty.simple.ws;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketServer {

    private int port = 8888;

    public static WebSocketServer open() {
        WebSocketServer server = new WebSocketServer();
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
                            ChannelPipeline pipeline = channel.pipeline();
                            // ws 是基于http协议的
                            pipeline.addLast(new HttpServerCodec());
                            // 数据以块的方式写入
                            pipeline.addLast(new ChunkedWriteHandler());
                            // http 传输数据分段大小
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            // 将http协议，升级为 ws 协议，保持长连接，并制定ws 地址
                            pipeline.addLast(new WebSocketServerProtocolHandler("/wst"));
                            // 处理业务
                            pipeline.addLast(new WebSocketHandler());
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
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        WebSocketServer server = WebSocketServer.open();
        server.run();
    }
}
