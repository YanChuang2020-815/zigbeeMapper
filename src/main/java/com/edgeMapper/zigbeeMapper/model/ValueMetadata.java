package com.edgeMapper.zigbeeMapper.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by rongshuai on 2020/7/15 16:41
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValueMetadata implements Serializable {
    private static final long serialVersionUID = 5398207514076976302L;

    private long timestamp;
}
