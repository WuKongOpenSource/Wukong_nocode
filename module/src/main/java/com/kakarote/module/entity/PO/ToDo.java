package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_to_do")
@ApiModel(value = "ToDO 对象", description = "待办")
public class ToDo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty("应用 ID")
    private Long applicationId;

    @ApiModelProperty("模块ID")
    private Long moduleId;

    @ApiModelProperty("模块名称")
    private String moduleName;

    @ApiModelProperty("对象类型 0 自定义流程 1 自定义按钮")
    private Integer objectType;

    @ApiModelProperty("对象 ID")
    private Long objectId;

    @ApiModelProperty("记录 ID")
    private Long recordId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty("数据 ID")
    private Long dataId;

    @ApiModelProperty(value = "字段授权")
    private String fieldAuth;

    @ApiModelProperty(value = "字段值")
    private String fieldValue;

    @ApiModelProperty(value = "待办类型 0 节点通知")
    private Integer type;

    @ApiModelProperty(value = "类型 ID")
    private Long typeId;

    @ApiModelProperty("类型名称")
    private String typeName;

    @ApiModelProperty("节点类型 0 条件节点 1 审批节点 2 填写节点 3 抄送节点 4 添加数据 5 更新数据 6 发起人节点")
    private Integer flowType;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "负责人")
    @TableField(fill = FieldFill.INSERT)
    private Long ownerUserId;

    @ApiModelProperty(value = "已读")
    private Boolean viewed;

    @ApiModelProperty(value = "查看时间")
    private Date viewTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty("待办状态 0 待处理 1 已处理")
    private Integer status;
}
