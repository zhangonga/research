package tech.zg.research.netty.simple.packageandunpackage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class ProtocolBeanDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int len = byteBuf.readInt();
        byte[] bytes = new byte[len];

        byteBuf.readBytes(bytes);

        ProtocolBean protocolBean = new ProtocolBean();
        protocolBean.setLen(len);
        protocolBean.setContent(bytes);

        list.add(protocolBean);
    }
}
