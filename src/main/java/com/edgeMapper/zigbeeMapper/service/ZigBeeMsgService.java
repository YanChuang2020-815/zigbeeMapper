package com.edgeMapper.zigbeeMapper.service;

/**
 * Created by rongshuai on 2020/7/15 15:21
 */
public interface ZigBeeMsgService {
    void processMsg(byte[] bytes);

    void getAllDevice(String ip);
}
