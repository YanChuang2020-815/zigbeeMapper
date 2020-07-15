package com.edgeMapper.zigbeeMapper.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/7/15 16:32
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MsgTwin implements Serializable {
    private static final long serialVersionUID = -7120751492295946527L;

    private TwinValue expected;

    private TwinValue actual;

    private boolean optional;

    private TypeMetadata metadata;

    private TwinVersion expected_version;

    private TwinVersion actual_version;
}
