package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 通用关联条件
 *
 * @author zjj
 * @since 2021/8/13 17:29
 */
@Data
@ApiModel("通用关联条件")
public class CommonUnionConditionBO {

    @ApiModelProperty(value = "模式：0 简单, 1 高级")
    private Integer model;

    @ApiModelProperty(value = "类型：0 自定义 1 匹配字段")
    private Integer type;

    @ApiModelProperty(value = "筛选条件")
    private String search;

    @ApiModelProperty(value = "分组ID")
    private Integer groupId;

    @ApiModelProperty(value = "目标模块ID")
    private Long targetModuleId;

    @ApiModelProperty(value = "当前模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;
}
