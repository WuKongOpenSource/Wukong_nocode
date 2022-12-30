package com.kakarote.module.entity.VO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjj
 * @description: 字段默认值VO
 * @date 2021/8/12 10:24
 */
@Data
@ApiModel("字段默认值VO")
public class ModuleDefaultValueVO extends ModuleFieldValueVO {

    @ApiModelProperty(value = "选项ID")
    private Object key;

    @ApiModelProperty(value = "关联的表格id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long relateFieldId;
}
