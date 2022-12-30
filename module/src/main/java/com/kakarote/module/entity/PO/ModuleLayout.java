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
 * @description:
 * @author: zjj
 * @date: 2021-07-13 15:45
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_module_layout")
@ApiModel(value = "ModuleLayout 对象", description = "模块页面布局")
public class ModuleLayout implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "主键ID")
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	@ApiModelProperty(value = "模块ID")
	private Long moduleId;

	@ApiModelProperty(value = "版本号")
	private Integer version;

	@ApiModelProperty(value = "布局数据")
	private String data;

	@ApiModelProperty(value = "创建时间")
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;

	@ApiModelProperty(value = "最后修改时间")
	@TableField(fill = FieldFill.UPDATE)
	private Date updateTime;

	@ApiModelProperty(value = "创建人ID")
	@TableField(fill = FieldFill.INSERT)
	private Long createUserId;

	@ApiModelProperty(value = "修改人ID")
	private Long updateUserId;
}
