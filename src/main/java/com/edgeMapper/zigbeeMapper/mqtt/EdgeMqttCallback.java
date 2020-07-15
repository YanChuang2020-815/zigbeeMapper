package com.edgeMapper.zigbeeMapper.mqtt;

import com.edgeMapper.zigbeeMapper.config.EdgeMqttClient;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by rongshuai on 2020/7/14 17:36
 */
@Slf4j
public class EdgeMqttCallback implements MqttCallback {

    private EdgeMqttClient mqttClient;

    public EdgeMqttCallback(EdgeMqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.error("connectionLost");
        while (true) {
            try {
                Thread.sleep(1000);
                mqttClient.init();
                break;
            } catch (Exception e) {
                continue;
            }
        }
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        log.info("topic={},message={}",s,mqttMessage);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
