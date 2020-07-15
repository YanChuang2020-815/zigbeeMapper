package com.edgeMapper.zigbeeMapper.service.impl;

import com.edgeMapper.zigbeeMapper.service.ZigBeeMsgService;
import com.edgeMapper.zigbeeMapper.util.ByteUtil;
import com.edgeMapper.zigbeeMapper.util.GateWayUtil;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by rongshuai on 2020/7/15 10:54
 */
@Slf4j
@Service
public class ZigBeeMsgServiceImpl implements ZigBeeMsgService {

    @Override
    public void processMsg(byte[] bytes) {
        byte response = bytes[0];
        log.info("收到消息={}", ByteUtil.bytesToHexString(bytes));
        switch (response) {
            case 0x70:
                Double temperature;
                int humidity;
                int pm;

                JsonObject data = new JsonObject();

                int length = Integer.parseInt(String.valueOf(bytes[1]));
                String shortAddress = GateWayUtil.byte2HexStr(Arrays.copyOfRange(bytes, 2, 4));
                Integer endPoint = Integer.parseInt(String.valueOf(bytes[4]));
                String clusterId = GateWayUtil.byte2HexStr(Arrays.copyOfRange(bytes, 5, 7));
                log.info("clusterId is {}",clusterId);
                switch (clusterId) {
                    case "0204":  // 温度传感器上报数据
                        for (int i = 0; i < Integer.parseInt(String.valueOf(bytes[7])); i++) {
                            if (GateWayUtil.byte2HexStr(Arrays.copyOfRange(bytes, 8 + i * 5, 10 + i * 5)).equals("0000")) {
                                if (bytes[10 + i * 5] == 0x29) {
                                    BigDecimal b = new BigDecimal((double) GateWayUtil.dataBytesToInt(Arrays.copyOfRange(bytes, 11 + i * 5, 13 + i * 5)) / (double) 100);
                                    temperature = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    data.addProperty("temperature", temperature);
                                }
                            } else if (GateWayUtil.byte2HexStr(Arrays.copyOfRange(bytes, 8 + i * 5, 10 + i * 5)).equals("1100")) { // TODO 旧版本API文档表示 0204是温湿度
                                if (bytes[10 + i * 5] == 0x29) {
                                    humidity = GateWayUtil.dataBytesToInt(Arrays.copyOfRange(bytes, 11 + i * 5, 13 + i * 5));
                                    data.addProperty("humidity", (double) humidity);
                                }
                            }
                        }
                        break;

                    case "1504":  // PM2.5上报
                        String[] PM = {"PM1.0", "PM2.5", "PM10"};
                        for (int i = 0; i < Integer.parseInt(String.valueOf(bytes[7])); i++) {
                            if (GateWayUtil.byte2HexStr(Arrays.copyOfRange(bytes, 8 + i * 5, 10 + i * 5)).equals("0100")) {
                                if (bytes[10 + i * 5] == 0x21) {
                                    pm = GateWayUtil.dataBytesToInt(Arrays.copyOfRange(bytes, 11 + i * 5, 13 + i * 5));
                                    data.addProperty("PM1.0", (double) pm);
                                }
                            } else if (GateWayUtil.byte2HexStr(Arrays.copyOfRange(bytes, 8 + i * 5, 10 + i * 5)).equals("0000")) {
                                if (bytes[10 + i * 5] == 0x21) {
                                    pm = GateWayUtil.dataBytesToInt(Arrays.copyOfRange(bytes, 11 + i * 5, 13 + i * 5));
                                    data.addProperty("PM2.5", (double) pm);
                                }
                            } else if (GateWayUtil.byte2HexStr(Arrays.copyOfRange(bytes, 8 + i * 5, 10 + i * 5)).equals("0200")) {
                                if (bytes[10 + i * 5] == 0x21) {
                                    pm = GateWayUtil.dataBytesToInt(Arrays.copyOfRange(bytes, 11 + i * 5, 13 + i * 5));
                                    data.addProperty("PM10", (double) pm);
                                }
                            }
                        }
                        break;

                    default:
                        break;
                }
                log.info("device data is {}",data);
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