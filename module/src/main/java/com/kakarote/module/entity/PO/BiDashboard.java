package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zjj
 * @title: BiDashboard
 * @description: BiDashboard 对象
 * @date 2022/7/6
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_bi_dashboard")
@ApiModel(value = "BiDashboard 对象", description = "BiDashboard")
public class BiDashboard implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "module_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long moduleId;

    @ApiModelProperty(value = "应用ID")
    private Long applicationId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "颜色")
    private String iconColor;

    @ApiModelProperty(value = "状态 0 草稿 1 对内发布 2 对外发布")
    private Integer status;

    @ApiModelProperty(value = "模类型 1为仪表盘")
    private Integer type;

    @ApiModelProperty(value = "对外分享密码")
    private String sharePass;

    @ApiModelProperty(value = "自动刷新时间，单位为分钟 0代表不自动刷新")
    private Integer refreshTime;

    @ApiModelProperty(value = "仪表盘样式")
    private String wsStyle;

    @ApiModelProperty(value = "排序字段")
    private Integer orderNum;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "修改人ID")
    @TableField(fill = FieldFill.UPDATE)
    private Long updateUserId;


}
