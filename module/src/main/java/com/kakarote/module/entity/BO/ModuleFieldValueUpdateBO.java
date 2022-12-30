package com.kakarote.module.entity.BO;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjj
 * @description: ModuleFieldValueUpdateBO
 * @date 2022/6/22
 */
@Data
@ApiModel("字段值变更BO")
public class ModuleFieldValueUpdateBO extends ModuleFieldValueBO {

    @ApiModelProperty(value = "字段值")
    private String oldValue;
}
