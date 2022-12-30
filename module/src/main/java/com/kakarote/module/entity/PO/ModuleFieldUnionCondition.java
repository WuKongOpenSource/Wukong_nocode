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
@TableName("wk_module_field_union_condition")
@ApiModel(value = "ModuleFieldUnionCondition 对象", description = "数据关联筛选条件表")
public class ModuleFieldUnionCondition implements Serializable {
	private static final long serialVersionUID=1L;

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	@ApiModelProperty(value = "模式：0 简单, 1 高级")
	private Integer model;

	@ApiModelProperty(value = "类型：0 自定义 1 匹配字段")
	private Integer type;

	@ApiModelProperty(value = "筛选条件")
	private String search;

	@ApiModelProperty(value = "分组ID")
	private Integer groupId;

	@ApiModelProperty(value = "目标模块ID")
	private Long targetModuleId;

	@ApiModelProperty(value = "数据关联字段ID")
	private Long relateFieldId;

	@ApiModelProperty(value = "当前模块ID")
	private Long moduleId;

	@ApiModelProperty(value = "版本号")
	private Integer version;

	@ApiModelProperty(value = "创建时间")
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;

	@ApiModelProperty(value = "创建人ID")
	@TableField(fill = FieldFill.INSERT)
	private Long createUserId;
}
