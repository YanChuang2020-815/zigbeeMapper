package com.edgeMapper.zigbeeMapper.watcher.zigbee;

import com.edgeMapper.zigbeeMapper.config.ZigBeeConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by rongshuai on 2020/7/15 9:56
 */
@Slf4j
@Component
public class ZigbeeClient{

    @Autowired
    private ZigbeeHandler zigbeeHandler;

    @Autowired
    private ZigBeeConfig zigBeeConfig;

    private EventLoopGroup group;

    private Bootstrap b;

    private Channel channel;

    private ChannelFuture f;

    public void init() throws Exception {
        group = new NioEventLoopGroup();
        try {
            b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .remoteAddress(new InetSocketAddress(zigBeeConfig.getHost(),zigBeeConfig.getPort()))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel (SocketChannel ch)
                            throws Exception {
                                ch.pipeline().addLast(
                                        zigbeeHandler
                                );
                            }
                    });
            f = b.connect(zigBeeConfig.getHost(),zigBeeConfig.getPort()).sync();
            channel = f.channel();
            channel.closeFuture().sync();
//            f = b.connect().sync();
//            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public void close() throws Exception {
        if (!group.isShutdown()) {
            group.shutdownGracefully().sync();
        }
        init();
    }

    public void connect(){
        log.info("向网关发起连接");
        if (channel != null && channel.isActive()) return;
        try {
            channel.connect(new InetSocketAddress(zigBeeConfig.getHost(),zigBeeConfig.getPort())).sync();
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("连接失败");
        }
    }

//    public ChannelFuture getFuture() throws InterruptedException {
//        if (future==null || !future.channel().isActive()) {
//            future = b.connect(zigBeeConfig.getHost(),zigBeeConfig.getPort()).sync();
//        }
//        return future;
//    }

//    public void connect(){
//        try {
//            ChannelFuture future = b.connect(zigBeeConfig.getHost(),zigBeeConfig.getPort()).sync();
//            future.addListener(new ChannelFutureListener() {
//                @Override
//                public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                    if (channelFuture.isSuccess()) {
//                        channel = channelFuture.channel();
//                        System.out.println("Connect to server successfully!");
//                    } else {
//                        System.out.println("Failed to connect to server, try connect after 1s");
//
//                        channelFuture.channel().eventLoop().schedule(new Runnable() {
//                            @Override
//                            public void run() {
//                                connect();
//                            }
//                        }, 1, TimeUnit.SECONDS);
//                    }
//                }
//            });
//        } catch (Exception e) {
//
//        }
//    }
}
