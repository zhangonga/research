package tech.zg.research.netty.simple.packageandunpackage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PackageClientHandler extends SimpleChannelInboundHandler<ProtocolBean> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            String message = "你好啊服务器" + i;
            ProtocolBean protocolBean = new ProtocolBean();

            int len = message.getBytes(CharsetUtil.UTF_8).length;
            byte[] bytes = message.getBytes();
            protocolBean.setLen(len);
            protocolBean.setContent(bytes);

            ctx.channel().writeAndFlush(protocolBean);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ProtocolBean protocolBean) throws Exception {
        log.info(protocolBean.toString());
    }
}
