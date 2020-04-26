package tech.zg.research.netty.simple.packageandunpackage;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server {

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
                    // boss 线程option
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // work 线程 option
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // work 线程组 事件处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 这里每次连接都能获取到客户端的 channel， 这里就可以给用户一个标记和 channel 做一个标记，可以用来给用户推送消息
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            //socketChannel.pipeline().addLast(new ServerHandler());
                            socketChannel.pipeline().addLast(new ProtocolBeanEncoder());
                            socketChannel.pipeline().addLast(new ProtocolBeanDecoder());
                            socketChannel.pipeline().addLast(new PackageServerHandler());
                        }
                    });

            log.info("服务端初始化完成");
            // 启动服务器
            ChannelFuture channelFuture = serverBootstrap.bind(6666).sync();

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
