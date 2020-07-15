package com.edgeMapper.zigbeeMapper.service;

import com.google.gson.JsonObject;

/**
 * Created by rongshuai on 2020/7/15 15:36
 */
public interface MqttService {
    void updateDeviceTwin(String deviceName, JsonObject data);
}
