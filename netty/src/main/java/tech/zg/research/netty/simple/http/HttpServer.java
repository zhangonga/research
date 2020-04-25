package tech.zg.research.netty.simple.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpServer {

    public static void main(String[] args) throws InterruptedException {

        // boss 线程组和 work 线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            // 配置服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup)
                    // 连接分配的channel 类型
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer());

            log.info("服务端初始化完成");
            // 启动服务器
            ChannelFuture channelFuture = serverBootstrap.bind(80).sync();

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

            // 监听关闭通道
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 优雅关闭
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
