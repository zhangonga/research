package tech.zg.research.netty.simple.packageandunpackage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ProtocolBeanEncoder extends MessageToByteEncoder<ProtocolBean> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ProtocolBean protocolBean, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(protocolBean.getLen());
        byteBuf.writeBytes(protocolBean.getContent());
    }
}
