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
@TableName("wk_module_field_data_common")
@ApiModel(value = "ModuleFieldDataCommon 对象", description = "通用模块字段值表")
public class ModuleFieldDataCommon implements Serializable {
	private static final long serialVersionUID=1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	@ApiModelProperty(value = "数据ID")
	private Long dataId;

	@ApiModelProperty(value = "创建人ID")
	@TableField(fill = FieldFill.INSERT)
	private Long createUserId;

	@TableField(exist = false)
	@ApiModelProperty(value = "创建人用户名")
	private Long createUserName;

	@ApiModelProperty(value = "创建时间")
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;

	@ApiModelProperty(value = "更新时间")
	@TableField(fill = FieldFill.INSERT)
	private Date updateTime;

	@ApiModelProperty(value = "负责人ID")
	private Long ownerUserId;

	@ApiModelProperty(value = "团队成员")
	private String teamMember;

	@TableField(exist = false)
	@ApiModelProperty(value = "负责人用户名")
	private Long ownerUserName;

	@ApiModelProperty(value = "类型 0 审批 1 其他")
	private Integer type;

	@ApiModelProperty(value = "当前节点ID")
	private Long currentFlowId;

	@ApiModelProperty(value = "节点类型 0 条件 1 审批节点 2 填写节点 3 抄送节点 4 添加数据 5 更新节点")
	private Integer flowType;

	@ApiModelProperty(value = "节点状态 0 待处理 1 已处理 2 拒绝 3 处理中 4 撤回 5 未提交 8 作废 9 忽略")
	private Integer flowStatus;

	@ApiModelProperty(value = "分类ID")
	private Long categoryId;

	@ApiModelProperty(value = "阶段ID")
	private Long stageId;

	@ApiModelProperty(value = "阶段名称")
	private String stageName;

	@ApiModelProperty(value = "阶段状态 0 未开始 1 完成 2 草稿 3 成功 4 失败")
	private Integer stageStatus;

	@ApiModelProperty(value = "模块ID")
	private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

	@ApiModelProperty(value = "批次号")
	private String batchId;

	public Long getCreateUserName() {
		return createUserId;
	}

	public Long getOwnerUserName() {
		return ownerUserId;
	}
}
