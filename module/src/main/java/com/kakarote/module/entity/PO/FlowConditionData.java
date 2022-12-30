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
 * <p>
 * 审批条件扩展字段表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_flow_condition_data")
@ApiModel(value="FlowConditionData对象", description="审批条件扩展字段表")
public class FlowConditionData implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "规则类型 1 条件筛选 2 更新节点筛选 3 更新节点更新 4 更新节点添加 5 添加节点添加")
    private Integer ruleType;

    @ApiModelProperty(value = "对应类型ID")
    private Long typeId;

	@ApiModelProperty(value = "模式：0 简单, 1 高级")
	private Integer model;

	@ApiModelProperty(value = "类型：0 自定义,1 匹配字段")
	private Integer type;

	@ApiModelProperty(value = "目标模块ID")
	private Long targetModuleId;

	@ApiModelProperty(value = "筛选条件")
	private String search;

	@ApiModelProperty(value = "分组ID")
	private Integer groupId;

	@ApiModelProperty(value = "批次ID")
	private String batchId;

    @ApiModelProperty(value = "流程元数据ID")
    private Long flowMetadataId;

	@ApiModelProperty(value = "创建时间")
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;

	@ApiModelProperty(value = "创建人ID")
	@TableField(fill = FieldFill.INSERT)
	private Long createUserId;

	@ApiModelProperty(value = "当前模块ID")
	private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

}
