package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjj
 * @title: FieldQueryBO
 * @description: TODO
 * @date 2022/4/6 17:16
 */
@Data
@ApiModel("字段查询 BO")
public class FieldQueryBO {

    @ApiModelProperty(value = "当前模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "分类ID")
    private Long categoryId;
}
