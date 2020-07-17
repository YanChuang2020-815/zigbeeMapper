package com.edgeMapper.zigbeeMapper.service.impl;

import com.edgeMapper.zigbeeMapper.config.Constants;
import com.edgeMapper.zigbeeMapper.service.MqttService;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by rongshuai on 2020/7/15 15:36
 */
@Slf4j
@Service
public class MqttMsgServiceImpl implements MqttService {

    @Autowired
    private MqttClient mqttClient;

    @Override
    public void updateDeviceTwin(String deviceName, JsonObject data) {
        String topic = Constants.DeviceETPrefix + deviceName + Constants.TwinETUpdateSuffix;
        JsonObject rawMsg = new JsonObject();
        JsonObject twins = new JsonObject();
        Set<String> keySet = data.keySet();
        try{
            for (String key : keySet) {
                JsonObject twin = new JsonObject();
                JsonObject twinValue = new JsonObject();
                JsonObject typeMetadata = new JsonObject();
                twinValue.add("value", data.get(key));
                typeMetadata.addProperty("type", "Updated");
                twin.add("actual", twinValue);
                twin.add("metadata", typeMetadata);
                twins.add(key, twin);
            }
        } catch (Exception e) {
            log.error("json转换异常");
        }
        rawMsg.add("twin",twins);
        log.info("topic is {}, rawMsg is {}",topic,rawMsg);
        MqttMessage msg = new MqttMessage(rawMsg.toString().getBytes());
        try {
            mqttClient.publish(topic,msg);
        } catch (MqttException e) {
            log.error("mqtt消息发送失败");
        }

    }
}
