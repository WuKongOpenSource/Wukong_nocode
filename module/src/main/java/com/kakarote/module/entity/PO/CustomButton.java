package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zjj
 * @title: CustomButton
 * @description: 自定义按钮
 * @date 2022/3/16 14:34
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_custom_button")
@ApiModel(value = "CustomButton 对象", description = "自定义按钮")
public class CustomButton implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

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

    @ApiModelProperty("模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

}
