package com.edgeMapper.zigbeeMapper.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/8/16 19:42
 */
@Data
public class DeviceDataRequestDto implements Serializable {
    private static final long serialVersionUID = -1536677137872528358L;

    private String deviceName;
}
