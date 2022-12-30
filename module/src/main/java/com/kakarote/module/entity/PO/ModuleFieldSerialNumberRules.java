package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wwl
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_module_field_serial_number_rules")
@ApiModel(value = "ModuleFieldSerialNumberRules对象", description = "自定义编号规则表")
public class ModuleFieldSerialNumberRules implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "字段ID")
    private Long fieldId;

    @TableField(value = "`type`")
    @ApiModelProperty(value = "类型")
    private Integer type;

    @TableField(exist = false)
    @ApiModelProperty(value = "type为3时，是表单内字段，根据startNumber找到字段名")
    private String name;

    @ApiModelProperty(value = "时间格式")
    private String textFormat;

    @ApiModelProperty(value = "起始编号")
    private String startNumber;

    @ApiModelProperty(value = "递增数")
    private Integer stepNumber;

    @ApiModelProperty(value = "重新编号规则")
    private Integer resetType;

    @TableField(value = "`sorting`")
    @ApiModelProperty(value = "排序")
    private int sorting;
}
