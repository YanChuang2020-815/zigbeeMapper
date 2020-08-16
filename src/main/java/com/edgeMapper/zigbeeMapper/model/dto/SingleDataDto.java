package com.edgeMapper.zigbeeMapper.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/8/16 19:16
 */
@Data
public class SingleDataDto implements Serializable {
    private static final long serialVersionUID = 6760768341496836354L;

    private String name;

    private String value;
}
