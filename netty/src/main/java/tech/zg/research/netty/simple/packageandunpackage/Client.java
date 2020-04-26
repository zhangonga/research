package tech.zg.research.netty.simple.packageandunpackage;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client {

    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            // 配置线程组
            bootstrap.group(eventLoopGroup)
                    // 配置客户端通道channel 实现类
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 配置处理器
                            socketChannel.pipeline().addLast(new ProtocolBeanEncoder());
                            socketChannel.pipeline().addLast(new ProtocolBeanDecoder());
                            socketChannel.pipeline().addLast(new PackageClientHandler());
                        }
                    });
            log.info("客户端初始化完成");

            // ChannelFuture netty 的异步模型
            // 连接
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6666).sync();
            // 监听关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
