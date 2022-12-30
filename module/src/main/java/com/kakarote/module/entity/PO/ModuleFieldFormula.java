package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zjj
 * @title: ModuleFieldFormula
 * @description: 字段公式
 * @date 2022/3/4 15:56
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_module_field_formula")
@ApiModel(value="ModuleFieldFormula 对象", description="字段公式表")
public class ModuleFieldFormula implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "字段ID")
    private Long fieldId;

    @TableField(value = "`type`")
    @ApiModelProperty(value = "数值类型 1 数字 2 金额 3 百分比 4 日期 5 日期时间 6 文本 7 布尔值")
    private Integer type;

    @ApiModelProperty(value = "公式")
    private String formula;
}
