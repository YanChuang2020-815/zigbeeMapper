package com.edgeMapper.zigbeeMapper.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rongshuai on 2020/8/22 11:22
 */
@Data
public class DashBoardDto implements Serializable {
    private static final long serialVersionUID = -3570057924233029099L;

    private Date timestamp;

    private DeviceDataDto deviceDataDto;
}
