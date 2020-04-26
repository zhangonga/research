package tech.zg.research.netty.simple.packageandunpackage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * 2020-04-26 18:11:37,054 INFO  nioEventLoopGroup-3-3 (tech.zg.research.netty.simple.packageandunpackage.ServerHandler.channelRead0:19)  - hello 服务器1
 * 2020-04-26 18:11:37,055 INFO  nioEventLoopGroup-3-3 (tech.zg.research.netty.simple.packageandunpackage.ServerHandler.channelRead0:19)  - hello 服务器2hello 服务器3hello 服务器4hello 服务器5
 * 2020-04-26 18:11:37,055 INFO  nioEventLoopGroup-3-3 (tech.zg.research.netty.simple.packageandunpackage.ServerHandler.channelRead0:19)  - hello 服务器6hello 服务器7
 * 2020-04-26 18:11:37,056 INFO  nioEventLoopGroup-3-3 (tech.zg.research.netty.simple.packageandunpackage.ServerHandler.channelRead0:19)  - hello 服务器8hello 服务器9
 * 粘包现象
 */
@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] buffer = new byte[msg.readableBytes()];
        msg.readBytes(buffer);
        log.info(new String(buffer, CharsetUtil.UTF_8));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ByteBuf byteBuf = Unpooled.copiedBuffer(UUID.randomUUID().toString() + "\n", CharsetUtil.UTF_8);
        ctx.channel().writeAndFlush(byteBuf);
    }
}
