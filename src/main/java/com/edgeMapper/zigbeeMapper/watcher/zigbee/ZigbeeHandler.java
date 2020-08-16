package com.edgeMapper.zigbeeMapper.watcher.zigbee;

import com.edgeMapper.zigbeeMapper.service.ZigBeeMsgService;
import com.edgeMapper.zigbeeMapper.util.GateWayUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by rongshuai on 2020/7/14 23:56
 */
@Slf4j
@Component
public class ZigbeeHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Autowired
    private ZigBeeMsgService zigBeeMsgService;

    @Override
    public void channelActive (ChannelHandlerContext ctx) {
        log.info("连接成功！");
        GateWayUtil.getChannelMap().put(GateWayUtil.getIPString(ctx), ctx.channel());
        zigBeeMsgService.getAllDevice(GateWayUtil.getIPString(ctx));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        int readerIndex = byteBuf.readerIndex();
        byteBuf.getBytes(readerIndex, bytes);
        zigBeeMsgService.processMsg(bytes);
    }

    @Override
    public void exceptionCaught (ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
