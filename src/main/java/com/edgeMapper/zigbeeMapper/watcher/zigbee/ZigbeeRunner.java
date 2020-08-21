package com.edgeMapper.zigbeeMapper.watcher.zigbee;

import com.edgeMapper.zigbeeMapper.util.RedisUtil;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by rongshuai on 2020/8/20 18:03
 */
@Component
public class ZigbeeRunner implements CommandLineRunner {
    @Autowired
    private ZigbeeClient zigbeeClient;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ConnectionCheck connectionCheck;

    @Override
    public void run(String... args) throws Exception {
        redisUtil.set("deviceData","deviceData");
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(connectionCheck,5,10, TimeUnit.SECONDS);
        zigbeeClient.init();
    }
}
