package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjj
 * @title: FieldFormQueryBO
 * @description: 字段表单查询 BO
 * @date 2022/2/2116:42
 */
@Data
@ApiModel("字段表单查询 BO")
public class FieldFormQueryBO {

    @ApiModelProperty(value = "当前模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "分类ID")
    private Long categoryId;

    @ApiModelProperty(value = "是否过滤隐藏字段")
    private Boolean filterHidden;

}
