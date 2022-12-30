package com.kakarote.module.entity.PO;


import com.alibaba.fastjson.annotation.JSONField;
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

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_flow_data_deal_record")
@ApiModel(value = "FlowDataDealRecord 对象", description = "节点数据处理记录表")
public class FlowDataDealRecord implements Serializable {

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	@ApiModelProperty(value = "原处理记录ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long parentId;

	@ApiModelProperty(value = "主记录标识")
	private Boolean isMain;

	@ApiModelProperty(value = "负责人ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long userId;

	@ApiModelProperty(value = "创建人")
	@TableField(fill = FieldFill.INSERT)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long createUserId;

	@ApiModelProperty(value = "审批记录ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long recordId;

	@ApiModelProperty(value = "流程ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long flowId;

	@ApiModelProperty(value = "条件ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long conditionId;

	@ApiModelProperty(value = "数据ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long dataId;

	@ApiModelProperty(value = "流程类型  0 条件节点 1 审批节点 2 填写节点 3 抄送节点 4 添加数据 5 更新数据 6 发起人节点")
	private Integer flowType;

	@ApiModelProperty(value = "源数据")
	private byte[] sourceData;

	@ApiModelProperty(value = "当前节点填写后的数据")
	private byte[] currentData;

	@ApiModelProperty(value = "节点状态 0 待处理 1 已处理 2 拒绝 3 处理中 4 撤回 5 未提交 8 作废 9 忽略")
	private Integer flowStatus;

	@ApiModelProperty(value = "1 依次审批 2 会签 3 或签")
	private Integer type;

    @ApiModelProperty(value = "失效类型 1 转交失效 2 撤回失效")
    private Integer invalidType;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "角色ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long roleId;

	@ApiModelProperty(value = "批次ID")
	private String batchId;

	@ApiModelProperty(value = "创建时间")
	@TableField(fill = FieldFill.INSERT)
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	@ApiModelProperty(value = "修改时间")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;

	@ApiModelProperty(value = "限时处理的值 d代表天，m代表分钟 h代表小时 例 10d代表10天")
	private String timeValue;

	@ApiModelProperty(value = "超时类型 1 自动提醒 2 自动转交 3 自动同意")
	private Integer overtimeType;

    @ApiModelProperty(value = "模块ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

	@ApiModelProperty("备注")
	private String remark;

    @ApiModelProperty("扩展字段")
    private String extData;

}
