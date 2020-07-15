package com.edgeMapper.zigbeeMapper.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/7/15 11:49
 */
@Data
public class Device implements Serializable {
    private static final long serialVersionUID = 7318700992704906084L;
    private String shortAddress;
    private byte Endpoint;
    private String profileId;
    private String deviceId;
    private Boolean state;
    private String name;
    private byte onlineState;
    private String IEEE;
    private String snid;
    private String zoneType;
    private double electric;
    private byte[] recentState;
}
