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
 * @title: Message
 * @description: 系统消息表
 * @date 2021/11/269:38
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_message")
@ApiModel(value = "Message", description = "系统消息表")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "消息ID")
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long messageId;

    @ApiModelProperty(value = "内容")
    @TableField(value = "`value`")
    private String value;

    @ApiModelProperty(value = "数据ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dataId;

    @ApiModelProperty(value = "模块ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long moduleId;

    @ApiModelProperty(value = "模块名称")
    private String moduleName;

    @ApiModelProperty(value = "类型ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long typeId;

    @ApiModelProperty(value = "类型 0 系统 1 节点 2 自定义提醒 3 自定义按钮 4团队成员添加移除")
    private Integer type;

    @ApiModelProperty(value = "valid")
    @TableField(exist = false)
    private Integer valid;

    @ApiModelProperty(value = "创建人名称")
    @TableField(exist = false)
    private String createUserName;

    @ApiModelProperty(value = "流程名称")
    private String typeName;

    @ApiModelProperty(value = "审批状态")
    @TableField(value = "`status`")
    private Integer status;

    @ApiModelProperty(value = "批次ID")
    private String batchId;

    @ApiModelProperty(value = "限时处理的值 d代表天，m代表分钟 h代表小时 例 10d代表10天")
    private String timeValue;

    @ApiModelProperty(value = "接收人")
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long receiver;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "消息创建者 0为系统")
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @ApiModelProperty(value = "是否已读 0 未读 1 已读")
    private Integer isRead;

    @ApiModelProperty(value = "已读时间")
    private Date readTime;

    @ApiModelProperty(value = "扩展数据")
    private String extData;

}
