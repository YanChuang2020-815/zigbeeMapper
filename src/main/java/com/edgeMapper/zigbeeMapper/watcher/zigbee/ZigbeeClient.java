package com.edgeMapper.zigbeeMapper.watcher.zigbee;

import com.edgeMapper.zigbeeMapper.config.ZigBeeConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * Created by rongshuai on 2020/7/15 9:56
 */
@Component
public class ZigbeeClient implements CommandLineRunner {

    @Autowired
    private ZigbeeHandler zigbeeHandler;

    @Autowired
    private ZigBeeConfig zigBeeConfig;

    public void run(String... var1) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(zigBeeConfig.getHost(), zigBeeConfig.getPort()))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel (SocketChannel ch)
                            throws Exception {
                                ch.pipeline().addLast(
                                        zigbeeHandler
                                );
                            }
                    });
            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
