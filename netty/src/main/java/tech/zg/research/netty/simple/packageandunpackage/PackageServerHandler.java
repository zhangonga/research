package tech.zg.research.netty.simple.packageandunpackage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class PackageServerHandler extends SimpleChannelInboundHandler<ProtocolBean> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ProtocolBean protocolBean) throws Exception {
        log.info(protocolBean.toString());

        String message = UUID.randomUUID().toString();
        ProtocolBean sendProtocolBean = new ProtocolBean();

        int len = message.length();
        byte[] bytes = message.getBytes();
        sendProtocolBean.setLen(len);
        sendProtocolBean.setContent(bytes);

        channelHandlerContext.channel().writeAndFlush(sendProtocolBean);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        String message = UUID.randomUUID().toString();
        ProtocolBean protocolBean = new ProtocolBean();

        int len = message.length();
        byte[] bytes = message.getBytes();
        protocolBean.setLen(len);
        protocolBean.setContent(bytes);

        ctx.channel().writeAndFlush(protocolBean);
    }
}
