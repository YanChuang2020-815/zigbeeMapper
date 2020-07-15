package com.edgeMapper.zigbeeMapper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created by rongshuai on 2020/7/15 14:44
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "device")
public class DeviceConfig {
    private Map<String,String> zigbeeDevices;
}
