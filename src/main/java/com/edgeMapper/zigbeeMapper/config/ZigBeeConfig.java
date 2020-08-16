package com.edgeMapper.zigbeeMapper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by rongshuai on 2020/8/16 17:57
 */
@Configuration
@ConfigurationProperties(prefix = "zigbee")
@Data
public class ZigBeeConfig {
    private String host;
    private int port;
}
