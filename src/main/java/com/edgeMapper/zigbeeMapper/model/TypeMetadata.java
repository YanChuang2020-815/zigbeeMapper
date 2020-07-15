package com.edgeMapper.zigbeeMapper.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/7/15 16:34
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TypeMetadata implements Serializable {
    private static final long serialVersionUID = -2625809893978846510L;

    private String type;
}
