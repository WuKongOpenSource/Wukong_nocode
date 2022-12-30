package com.kakarote.module.entity.BO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("应用保存BO")
public class ModuleMetadataBO {

    @ApiModelProperty(value = "应用ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long applicationId;

    @ApiModelProperty(value = "应用名称")
    @NotNull
    private String name;

    @ApiModelProperty(value = "0 自定义 1 安装 2 导入 3 自用系统 4 其他")
    private Integer type;

    @ApiModelProperty(value = "关联的应用 ID")
    private Long relateApplicationId;

    @ApiModelProperty(value = "应用简介")
    private String description;

    @ApiModelProperty(value = "应用详情描述")
    private String detail;

    @ApiModelProperty(value = "是否精选")
    private Boolean isFeatured = false;

    @ApiModelProperty(value = "主图")
    private String mainPicture;

    @ApiModelProperty(value = "详情图")
    private String detailPicture;

    @ApiModelProperty(value = "应用状态")
    private Integer status;

    @ApiModelProperty(value = "模块图标")
    private String icon;

    @ApiModelProperty(value = "图标颜色")
    private String iconColor;
}
