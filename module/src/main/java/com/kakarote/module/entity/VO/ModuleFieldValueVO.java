package com.kakarote.module.entity.VO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: zjj
 * @description: 模块字段值VO
 * @date: 2021/8/13 15:51
 */
@Data
@ApiModel("模块字段值VO")
public class ModuleFieldValueVO {

    @ApiModelProperty(value = "当前字段ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fieldId;

    @ApiModelProperty(value = "字段名")
    private String fieldName;

    @ApiModelProperty(value = "默认值")
    private Object value;
}
