package com.edgeMapper.zigbeeMapper.config;

import com.edgeMapper.zigbeeMapper.mqtt.EdgeMqttCallback;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Created by rongshuai on 2020/7/14 17:53
 */
@Slf4j
@Configuration
public class EdgeMqttClient {
    @Autowired
    private MqttConfig mqttConfig;

    @Autowired
    private MqttClient mqttClient;

    @Bean
    public MqttClient defaultMqttClient() throws MqttException {
        return new MqttClient(mqttConfig.getServer(), mqttConfig.getClientId(),new MemoryPersistence());
    }

    private MqttConnectOptions getOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(mqttConfig.isCleanSession());
        options.setConnectionTimeout(mqttConfig.getConnectionTimeout());
        options.setKeepAliveInterval(mqttConfig.getKeepAliveInterval());
        return options;
    }

    @PostConstruct
    public void init() throws MqttException {
        mqttClient.setCallback(new EdgeMqttCallback(this));
        mqttClient.connect(getOptions());
        mqttClient.subscribe(Constants.DeviceETPrefix + "+" + Constants.TwinETUpdateSuffix + "/+",1);
        if (mqttClient.isConnected()) {
            log.info("Mosquitto连接成功");
        }
    }
}
