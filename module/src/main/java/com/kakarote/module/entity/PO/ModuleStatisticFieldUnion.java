package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_module_statistic_field_union")
@ApiModel(value = "ModuleStatisticFieldUnion 对象", description = "统计字段关联表")
public class ModuleStatisticFieldUnion {

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	@ApiModelProperty(value = "统计字段ID")
	private Long relateFieldId;

	@ApiModelProperty(value = "当前模块ID")
	private Long moduleId;

	@ApiModelProperty(value = "版本号")
	private Integer version;

	@ApiModelProperty(value = "目标字段ID")
	private Long targetFieldId;

	@ApiModelProperty(value = "目标模块ID")
	private Long targetModuleId;

	@ApiModelProperty(value = "统计类型")
	private Integer statisticType;

	@ApiModelProperty(value = "创建时间")
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;

	@ApiModelProperty(value = "创建人ID")
	@TableField(fill = FieldFill.INSERT)
	private Long createUserId;
}
