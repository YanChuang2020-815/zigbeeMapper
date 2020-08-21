package com.edgeMapper.zigbeeMapper.watcher.zigbee;

import com.edgeMapper.zigbeeMapper.service.ZigBeeMsgService;
import com.edgeMapper.zigbeeMapper.util.GateWayUtil;
import com.edgeMapper.zigbeeMapper.util.RedisUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by rongshuai on 2020/7/14 23:56
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ZigbeeHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Autowired
    private ZigBeeMsgService zigBeeMsgService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ZigbeeClient zigbeeClient;

    @Override
    public void channelActive (ChannelHandlerContext ctx) {
        log.info("连接成功！");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        int readerIndex = byteBuf.readerIndex();
        byteBuf.getBytes(readerIndex, bytes);
        zigBeeMsgService.processMsg(bytes);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Disconnected with the remote client.");
        zigbeeClient.reconnect();

        // do something
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught (ChannelHandlerContext ctx, Throwable cause) {
        log.info("连接断开");
        cause.printStackTrace();
        ctx.channel().close();
    }
}
