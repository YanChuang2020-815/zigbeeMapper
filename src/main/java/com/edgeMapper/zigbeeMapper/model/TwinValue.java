package com.edgeMapper.zigbeeMapper.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/7/15 16:34
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TwinValue implements Serializable {
    private static final long serialVersionUID = 5954114611471343035L;

    private String value;

    private ValueMetadata metadata;
}
