package com.edgeMapper.zigbeeMapper.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/7/15 16:34
 */
@Data
public class TwinVersion implements Serializable {
    private static final long serialVersionUID = -8530842509519650155L;

    private long cloud;

    private long edge;
}
