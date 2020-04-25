package tech.zg.research.netty.simple.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * ChannelGroup 事件组
     * GlobalEventExecutor 单例对象，全局事件执行器
     */
    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 连接到来后第一个调用方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // 将加入事件同步其他 Channel
        CHANNEL_GROUP.writeAndFlush("[客户端] " + channel.remoteAddress() + " 加入聊天");
        CHANNEL_GROUP.add(channel);
        log.info("在线人数： {}", CHANNEL_GROUP.size());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // 自动删除掉了, 不用 CHANNEL_GROUP.remove(channel)
        // 将退出事件同步其他 Channel
        CHANNEL_GROUP.writeAndFlush("[客户端] " + channel.remoteAddress() + " 退出聊天");
        log.info("在线人数： {}", CHANNEL_GROUP.size());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("{} 上线了", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("{} 下线了", ctx.channel().remoteAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("服务器日志-接收到消息: {}", msg);
        Channel channel = ctx.channel();
        CHANNEL_GROUP.forEach(channel1 -> {
            if (channel != channel1) {
                channel1.writeAndFlush("[客户端] " + channel.remoteAddress() + " 发送了消息 " + msg + "\n");
            } else {
                channel.writeAndFlush(msg);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
