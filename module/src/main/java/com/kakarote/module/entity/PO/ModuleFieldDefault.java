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

/**
 * <p>
 * 字段默认值配置表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-03-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_module_field_default")
@ApiModel(value="ModuleFieldDefault对象", description="字段默认值配置表")
public class ModuleFieldDefault implements Serializable {

    private static final long serialVersionUID=1L;

	@TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

	@ApiModelProperty("模块ID")
	private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty("目标模块ID")
    private Long targetModuleId;

    @ApiModelProperty(value = "目标字段ID")
    private Long targetFieldId;

    @ApiModelProperty(value = "字段ID")
	private Long fieldId;

    @TableField(value = "`key`")
    @ApiModelProperty(value = "选项ID")
    private String key;

    @ApiModelProperty(value = "默认值")
    private String value;

    @ApiModelProperty(value = "默认值类型 1 固定值 2 自定义筛选 3 公式")
    private Integer type;

	@ApiModelProperty(value = "筛选条件")
	private String search;

    @ApiModelProperty(value = "公式")
    private String formula;

}
