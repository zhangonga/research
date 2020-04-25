package tech.zg.research.netty.simple.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * channel 就绪就会触发
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("client: {}", ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello 服务器", CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("client: {}", ctx);
        ByteBuf byteBuf = (ByteBuf) msg;
        log.info("client read: {}", byteBuf.toString(CharsetUtil.UTF_8));
        log.info("server ip: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("client exception: {}", cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
