package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjj
 * @title: ModuleFieldFormula
 * @description: 计算公式字段BO
 * @date 2022/3/4 16:08
 */
@Data
@ApiModel("计算公式字段BO")
public class ModuleFieldFormulaBO {

    @ApiModelProperty(value = "数值类型 1 数字 2 金额 3 百分比 4 日期 5 日期时间 6 文本 7 布尔值")
    private Integer type;

    @ApiModelProperty(value = "公式")
    private String formula;
}
