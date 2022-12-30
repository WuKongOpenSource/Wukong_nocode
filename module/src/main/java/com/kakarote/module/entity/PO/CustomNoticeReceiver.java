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
 * @title: CustomNoticeReceiver
 * @description: 自定义提醒接收配置
 * @date 2022/3/23 9:57
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_custom_notice_receiver")
@ApiModel(value = "CustomNoticeReceiver 对象", description = "自定义提醒接收配置")
public class CustomNoticeReceiver implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("提醒ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long noticeId;

    @ApiModelProperty("提醒内容")
    private String content;

    @ApiModelProperty("通知创建人")
    private Boolean noticeCreator;

    @ApiModelProperty("通知负责人")
    private Boolean noticeOwner;

    @ApiModelProperty("指定成员")
    private String noticeUser;

    @ApiModelProperty("指定角色")
    private String noticeRole;

    @ApiModelProperty("负责人上级")
    private String parentLevel;

    @ApiModelProperty("人员字段")
    private String userField;

    @ApiModelProperty("部门字段")
    private String deptField;

    @ApiModelProperty("模块ID")
    @JsonSerialize(using = ToStringSerializer.class)
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
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

}
