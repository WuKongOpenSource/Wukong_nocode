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
@TableName("wk_module_field_union")
@ApiModel(value = "ModuleFieldUnion 对象", description = "字段关联表")
public class ModuleFieldUnion implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	@ApiModelProperty(value = "0 字段关联 1 模块关联 2 表格字段默认值填充配置")
	private Integer type;

	@ApiModelProperty(value = "数据关联字段ID")
	private Long relateFieldId;

	@ApiModelProperty(value = "当前模块ID")
	private Long moduleId;

	@ApiModelProperty(value = "版本号")
	private Integer version;

	@ApiModelProperty(value = "当前字段ID")
	private Long fieldId;

	@ApiModelProperty(value = "目标字段ID")
	private Long targetFieldId;

	@ApiModelProperty(value = "目标模块ID")
	private Long targetModuleId;

	@ApiModelProperty(value = "目标分类ID")
	private String targetCategoryIds;

	@ApiModelProperty(value = "创建时间")
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;

	@ApiModelProperty(value = "创建人ID")
	@TableField(fill = FieldFill.INSERT)
	private Long createUserId;
}
