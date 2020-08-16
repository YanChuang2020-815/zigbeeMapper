package com.edgeMapper.zigbeeMapper.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rongshuai on 2020/8/16 19:15
 */
@Data
public class DeviceDataDto implements Serializable {
    private static final long serialVersionUID = -5780026741175759039L;

    private String deviceName;

    private List<SingleDataDto> dataList;
}
