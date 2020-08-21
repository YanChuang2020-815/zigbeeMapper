package com.edgeMapper.zigbeeMapper.watcher.zigbee;

import com.edgeMapper.zigbeeMapper.util.RedisUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by rongshuai on 2020/8/20 18:23
 */
@Slf4j
@Component
public class ConnectionCheck implements Runnable {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ZigbeeClient zigbeeClient;


    @Override
    public void run() {
        log.info("检查与网关的连接");
        if (!redisUtil.hasKey("deviceData")) {
            try {
                zigbeeClient.close();
            } catch (Exception e) {
                e.printStackTrace();
                log.info("重新连接异常");
            }
        }
    }
}
