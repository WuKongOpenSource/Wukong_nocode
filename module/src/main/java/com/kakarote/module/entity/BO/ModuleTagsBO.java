package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjj
 * @title: ModuleTagsBO
 * @description: TODO
 * @date 2022/3/314:55
 */
@Data
@ApiModel("标签字段选项BO")
public class ModuleTagsBO extends ModuleOptionsBO{

    @ApiModelProperty(value = "分组ID")
    private Integer groupId;

    @ApiModelProperty(value = "分组名字")
    private String groupName;

    @ApiModelProperty(value = "颜色")
    private String color;
}
