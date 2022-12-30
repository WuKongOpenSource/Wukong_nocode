package com.kakarote.module.entity.BO;

import cn.hutool.core.collection.CollUtil;
import com.kakarote.module.common.expression.ExpressionUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * @author zjj
 * @title: ModuleFormulaBO
 * @description: 公式 BO
 * @date 2022/3/4 16:07
 */
@Data
@ApiModel("函数BO")
public class ModuleFormulaBO {

    @ApiModelProperty(value = "数值类型 1 数字 2 金额 3 百分比 4 日期 5 日期时间 6 文本 7 布尔值")
    private Integer type;

    @ApiModelProperty(value = "表达式")
    private String expression;

    @ApiModelProperty(value = "参数")
    private Set<String> vars;

    @ApiModelProperty(value = "参数值")
    private Map<String, Object> env;

    @ApiModelProperty(value = "原始参数值")
    private Map<String, Object> sourceEnv;

    @ApiModelProperty(value = "字段值")
    private String value;

    private void setVars(Set<String> vars) {
        this.vars = vars;
    }

    public Set<String> getVars() {
        if (CollUtil.isEmpty(this.vars)) {
            this.vars = ExpressionUtil.parseArgs(this.getExpression(), ExpressionUtil.REGEX_ALL);
        }
        return vars;
    }
}
