package tech.zg.research.netty.simple.packageandunpackage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 2020-04-26 18:11:37,054 INFO  nioEventLoopGroup-3-3 (tech.zg.research.netty.simple.packageandunpackage.ServerHandler.channelRead0:19)  - hello 服务器1
 * 2020-04-26 18:11:37,055 INFO  nioEventLoopGroup-3-3 (tech.zg.research.netty.simple.packageandunpackage.ServerHandler.channelRead0:19)  - hello 服务器2hello 服务器3hello 服务器4hello 服务器5
 * 2020-04-26 18:11:37,055 INFO  nioEventLoopGroup-3-3 (tech.zg.research.netty.simple.packageandunpackage.ServerHandler.channelRead0:19)  - hello 服务器6hello 服务器7
 * 2020-04-26 18:11:37,056 INFO  nioEventLoopGroup-3-3 (tech.zg.research.netty.simple.packageandunpackage.ServerHandler.channelRead0:19)  - hello 服务器8hello 服务器9
 * 粘包现象
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * channel 就绪就会触发
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            ctx.writeAndFlush(Unpooled.copiedBuffer("hello 服务器" + i, CharsetUtil.UTF_8));
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] buffer = new byte[msg.readableBytes()];
        msg.readBytes(buffer);
        log.info(new String(buffer, CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
