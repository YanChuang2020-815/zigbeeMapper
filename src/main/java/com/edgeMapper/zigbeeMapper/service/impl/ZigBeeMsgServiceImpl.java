package com.edgeMapper.zigbeeMapper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.edgeMapper.zigbeeMapper.config.DeviceConfig;
import com.edgeMapper.zigbeeMapper.model.dto.DashBoardDto;
import com.edgeMapper.zigbeeMapper.model.dto.DeviceDataDto;
import com.edgeMapper.zigbeeMapper.model.dto.SingleDataDto;
import com.edgeMapper.zigbeeMapper.service.MqttService;
import com.edgeMapper.zigbeeMapper.service.ZigBeeMsgService;
import com.edgeMapper.zigbeeMapper.util.ByteUtil;
import com.edgeMapper.zigbeeMapper.util.GateWayUtil;
import com.edgeMapper.zigbeeMapper.util.RedisUtil;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by rongshuai on 2020/7/15 10:54
 */
@Slf4j
@Service
public class ZigBeeMsgServiceImpl implements ZigBeeMsgService {

    @Autowired
    private DeviceConfig deviceConfig;

    @Autowired
    private MqttService mqttService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DefaultMQProducer producer;

    @Override
    public void processMsg(byte[] bytes) {
        byte response = bytes[0];
        log.info("收到消息={}", ByteUtil.bytesToHexString(bytes));
        switch (response) {
            case 0x70:
                Double temperature;
                int humidity;
                int pm;
                int illumination;

                boolean isAlarm = false;

                JsonObject data = new JsonObject();

                int length = Integer.parseInt(String.valueOf(bytes[1]));
                String shortAddress = GateWayUtil.byte2HexStr(Arrays.copyOfRange(bytes, 2, 4));
                Integer endPoint = Integer.parseInt(String.valueOf(bytes[4]));
                String clusterId = GateWayUtil.byte2HexStr(Arrays.copyOfRange(bytes, 5, 7));
                log.info("clusterId is {}",clusterId);
                DeviceDataDto deviceDataDto = new DeviceDataDto();
                List<SingleDataDto> dataDtos = new ArrayList<>();
                switch (clusterId) {
                    case "0204":  // 温度传感器上报数据
                        for (int i = 0; i < Integer.parseInt(String.valueOf(bytes[7])); i++) {
                            if (GateWayUtil.byte2HexStr(Arrays.copyOfRange(bytes, 8 + i * 5, 10 + i * 5)).equals("0000")) {
                                if (bytes[10 + i * 5] == 0x29) {
                                    SingleDataDto dataDto = new SingleDataDto();
                                    BigDecimal b = new BigDecimal((double) GateWayUtil.dataBytesToInt(Arrays.copyOfRange(bytes, 11 + i * 5, 13 + i * 5)) / (double) 100);
                                    temperature = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    if (temperature >= 31.0) {
                                        isAlarm = true;
                                    }
                                    data.addProperty("temperature", String.valueOf(temperature));
                                    dataDto.setName("temperature");
                                    dataDto.setValue(String.valueOf(temperature));
                                    dataDtos.add(dataDto);
                                }
                            } else if (GateWayUtil.byte2HexStr(Arrays.copyOfRange(bytes, 8 + i * 5, 10 + i * 5)).equals("1100")) { // TODO 旧版本API文档表示 0204是温湿度
                                if (bytes[10 + i * 5] == 0x29) {
                                    SingleDataDto dataDto = new SingleDataDto();
                                    humidity = GateWayUtil.dataBytesToInt(Arrays.copyOfRange(bytes, 11 + i * 5, 13 + i * 5));
                                    data.addProperty("humidity", String.valueOf(humidity));
                                    dataDto.setName("humidity");
                                    dataDto.setValue(String.valueOf(humidity));
                                    dataDtos.add(dataDto);
                                }
                            }
                        }
                        if (deviceConfig.getZigbeeDevices().containsKey("0204")) {
                            String deviceName = deviceConfig.getZigbeeDevices().get("0204");
                            deviceDataDto.setDeviceName(deviceName);
                            deviceDataDto.setDataList(dataDtos);
                            redisUtil.set(deviceName,deviceDataDto);
                            log.info("设备数据为{}",deviceDataDto);
                            try {
                                DashBoardDto dashBoardDto = new DashBoardDto();
                                dashBoardDto.setTimestamp(new Date(System.currentTimeMillis()));
                                dashBoardDto.setDeviceDataDto(deviceDataDto);
                                Message msg = new Message("dashboard", JSONObject.toJSONString(dashBoardDto).getBytes());
                                log.info("推送给dashboard的数据为{}",dashBoardDto);
                                producer.send(msg);
                            } catch (Exception e) {
                                log.error("推送mq实时数据异常",e);
                            }
                            if (isAlarm) {
                                mqttService.updateDeviceTwin(deviceName, data);
                            }
                        } else {
                            log.error("云端不存在此设备，或是设备名不匹配");
                        }
                        break;

                    case "1504":  // PM2.5上报
                        String[] PM = {"PM1.0", "PM2.5", "PM10"};
                        for (int i = 0; i < Integer.parseInt(String.valueOf(bytes[7])); i++) {
                            if (GateWayUtil.byte2HexStr(Arrays.copyOfRange(bytes, 8 + i * 5, 10 + i * 5)).equals("0100")) {
                                if (bytes[10 + i * 5] == 0x21) {
                                    SingleDataDto dataDto = new SingleDataDto();
                                    pm = GateWayUtil.dataBytesToInt(Arrays.copyOfRange(bytes, 11 + i * 5, 13 + i * 5));
                                    data.addProperty("PM1.0", String.valueOf(pm));
                                    dataDto.setName("PM1.0");
                                    dataDto.setValue(String.valueOf(pm));
                                    dataDtos.add(dataDto);
                                }
                            } else if (GateWayUtil.byte2HexStr(Arrays.copyOfRange(bytes, 8 + i * 5, 10 + i * 5)).equals("0000")) {
                                if (bytes[10 + i * 5] == 0x21) {
                                    SingleDataDto dataDto = new SingleDataDto();
                                    pm = GateWayUtil.dataBytesToInt(Arrays.copyOfRange(bytes, 11 + i * 5, 13 + i * 5));
                                    if (pm >= 40) {
                                        isAlarm = true;
                                    }
                                    data.addProperty("PM2.5", String.valueOf(pm));
                                    dataDto.setName("PM2.5");
                                    dataDto.setValue(String.valueOf(pm));
                                    dataDtos.add(dataDto);
                                }
                            } else if (GateWayUtil.byte2HexStr(Arrays.copyOfRange(bytes, 8 + i * 5, 10 + i * 5)).equals("0200")) {
                                if (bytes[10 + i * 5] == 0x21) {
                                    SingleDataDto dataDto = new SingleDataDto();
                                    pm = GateWayUtil.dataBytesToInt(Arrays.copyOfRange(bytes, 11 + i * 5, 13 + i * 5));
                                    data.addProperty("PM10", String.valueOf(pm));
                                    dataDto.setName("PM10");
                                    dataDto.setValue(String.valueOf(pm));
                                    dataDtos.add(dataDto);
                                }
                            }
                        }
                        if (deviceConfig.getZigbeeDevices().containsKey("1504")) {
                            String deviceName = deviceConfig.getZigbeeDevices().get("1504");
                            deviceDataDto.setDeviceName(deviceName);
                            deviceDataDto.setDataList(dataDtos);
                            redisUtil.set(deviceName,deviceDataDto);
                            log.info("设备数据为{}",deviceDataDto);
                            try {
                                DashBoardDto dashBoardDto = new DashBoardDto();
                                dashBoardDto.setTimestamp(new Date(System.currentTimeMillis()));
                                dashBoardDto.setDeviceDataDto(deviceDataDto);
                                Message msg = new Message("dashboard", JSONObject.toJSONString(dashBoardDto).getBytes());
                                log.info("推送给dashboard的数据为{}",dashBoardDto);
                                producer.send(msg);
                            } catch (Exception e) {
                                log.error("推送mq实时数据异常",e);
                            }
                            if (isAlarm) {
                                mqttService.updateDeviceTwin(deviceName, data);
                            }
                        } else {
                            log.error("云端不存在此设备，或是设备名不匹配");
                        }
                        break;
                    case "0004":
                        for (int i = 0; i < Integer.parseInt(String.valueOf(bytes[7])); i++) {
                            if (GateWayUtil.byte2HexStr(Arrays.copyOfRange(bytes, 8 + i * 5, 10 + i * 5)).equals("0000")) {
                                if (bytes[10 + i * 5] == 0x21) {
                                    SingleDataDto dataDto = new SingleDataDto();
                                    illumination = GateWayUtil.dataBytesToInt(Arrays.copyOfRange(bytes, 11 + i * 5, 13 + i * 5));
                                    if (illumination >= 500) {
                                        isAlarm = true;
                                    }
                                    data.addProperty("illumination", String.valueOf(illumination));
                                    dataDto.setName("illumination");
                                    dataDto.setValue(String.valueOf(illumination));
                                    dataDtos.add(dataDto);
                                }
                            }
                        }
                        if (deviceConfig.getZigbeeDevices().containsKey("0004")) {
                            String deviceName = deviceConfig.getZigbeeDevices().get("0004");
                            deviceDataDto.setDeviceName(deviceName);
                            deviceDataDto.setDataList(dataDtos);
                            redisUtil.set(deviceName,deviceDataDto);
                            log.info("设备数据为{}",deviceDataDto);
                            try {
                                DashBoardDto dashBoardDto = new DashBoardDto();
                                dashBoardDto.setTimestamp(new Date(System.currentTimeMillis()));
                                dashBoardDto.setDeviceDataDto(deviceDataDto);
                                Message msg = new Message("dashboard", JSONObject.toJSONString(dashBoardDto).getBytes());
                                log.info("推送给dashboard的数据为{}",dashBoardDto);
                                producer.send(msg);
                            } catch (Exception e) {
                                log.error("推送mq实时数据异常",e);
                            }
                            if (isAlarm) {
                                mqttService.updateDeviceTwin(deviceName, data);
                            }
                        } else {
                            log.error("云端不存在此设备，或是设备名不匹配");
                        }
                        break;
                    default:
                        break;
                }
                break;
            case 0x01:
                log.info("全部设备信息={}",bytes);
                break;
            default:
                log.info("消息类型无匹配");
                break;
        }
    }

    /**
     * 获取网关下的全部设备
     * @param ip
     */
    @Override
    public void getAllDevice(String ip) {
        byte[] bytes = new byte[8];

        int index = 0;
        bytes[index++] = (byte) 0x08;
        bytes[index++] = (byte) 0x00;

        //snid
        bytes[index++] = (byte) 0xFF;
        bytes[index++] = (byte) 0xFF;
        bytes[index++] = (byte) 0xFF;
        bytes[index++] = (byte) 0xFF;

        bytes[index++] = (byte) 0xFE;
        bytes[index] = (byte) 0x9D;
        log.info("发送的字节码={}",ByteUtil.bytesToHexString(bytes));

        GateWayUtil.getChannelMap().get(ip).writeAndFlush(bytes);
        /**
         * S:08 00 FF FF FF FF FE 81
         * S:08 00 FF FF FF FF FE 8E
         */
    }

}
