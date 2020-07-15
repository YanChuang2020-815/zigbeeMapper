package com.edgeMapper.zigbeeMapper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by rongshuai on 2020/7/14 15:10
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfig {
    private int mode;

    private String server;

    private String internalServer;

    private String clientId;

    private boolean cleanSession;

    private int connectionTimeout;

    private int keepAliveInterval;
}
