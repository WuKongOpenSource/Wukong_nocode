package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author zjj
 * @title: CustomButtonSaveBO
 * @description: 自定义按钮保存 BO
 * @date 2022/3/16 14:51
 */
@Data
@ApiModel(value = "自定义按钮保存 BO", description = "自定义按钮保存 BO")
public class CustomButtonSaveBO {

    @NotEmpty
    @ApiModelProperty("模块ID")
    private Long moduleId;

    @NotEmpty
    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty("按钮ID")
    private Long buttonId;

    @ApiModelProperty("按钮名称")
    private String buttonName;

    @ApiModelProperty(value = "模块图标")
    private String icon;

    @ApiModelProperty(value = "图标颜色")
    private String iconColor;

    @ApiModelProperty(value = "生效类型: 0  总是触发, 1 满足条件触发")
    private Integer effectType;

    @ApiModelProperty(value = "触发条件配置")
    private String effectConfig;

    @ApiModelProperty(value = "执行类型: 0  立即, 1 二次确认 2 填写内容")
    private Integer executeType;

    @ApiModelProperty(value = "二次确认配置")
    private String recheckConfig;

    @ApiModelProperty(value = "填写配置")
    private String fillConfig;

    @ApiModelProperty(value = "流程元数据ID")
    private Long flowMetaDataId;

}
