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
 * @title: StageFlow
 * @description: 阶段流程表
 * @date 2022/4/11 14:14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_stage_setting")
@ApiModel(value = "StageFlow 对象", description = "阶段流程表")
public class StageSetting implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "阶段流程ID")
    private Long stageSettingId;

    @ApiModelProperty(value = "阶段流程名称")
    private String stageSettingName;

    @ApiModelProperty(value = "成功阶段的名称")
    private String successName;

    @ApiModelProperty(value = "失败阶段的名称")
    private String failedName;

    @ApiModelProperty(value = "适用部门")
    private String deptIds;

    @ApiModelProperty(value = "适用员工")
    private String userIds;

    @ApiModelProperty(value = "状态 1 正常 0 停用")
    private Integer status;

    @ApiModelProperty("模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "创建人")
    private Long createUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}