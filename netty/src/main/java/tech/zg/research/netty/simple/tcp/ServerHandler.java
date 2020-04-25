package tech.zg.research.netty.simple.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读取客户端的消息
     *
     * @param ctx channelHandler 上下文，含有管道（pipe),通道（channel)，链接地址
     * @param msg 客户端发送的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        log.info("server: {}", ctx);
        ByteBuf byteBuf = (ByteBuf) msg;
        log.info("server read: {}", byteBuf.toString(CharsetUtil.UTF_8));
        log.info("client ip: {}", ctx.channel().remoteAddress());

        // 异步任务，放入 taskQueue 中
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端2", CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // 上一个任务执行完，才执行这个，因为是一个线程
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端3", CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // 异步任务，放入 scheduleTaskQueue 中
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端3", CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 数据读取完成
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        String backMessage = "hello 客户端";
        // 将数据写入缓存，并刷新
        // note: 发送的数据需要编码
        ctx.writeAndFlush(Unpooled.copiedBuffer(backMessage, CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("sever exception: {}", cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
