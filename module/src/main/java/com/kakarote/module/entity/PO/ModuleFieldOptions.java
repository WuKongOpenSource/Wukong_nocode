package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 字段选项表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-03-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_module_field_options")
@ApiModel(value="ModuleFieldOptions对象", description="字段选项表")
public class ModuleFieldOptions implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

	@ApiModelProperty("模块ID")
	private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "字段ID")
    private Long fieldId;

    @TableField(value = "`value`")
    @ApiModelProperty(value = "选项值")
    private String value;

    @TableField(value = "`key`")
    @ApiModelProperty(value = "选项ID")
    private String key;

	@ApiModelProperty(value = "选项类型：0 普通 1 其他")
	private Integer type;

    @ApiModelProperty(value = "排序")
    private Integer sorting;
}
