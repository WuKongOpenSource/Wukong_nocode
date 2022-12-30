package com.kakarote.module.entity.BO;

import com.kakarote.module.constant.FieldSearchEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjj
 * @title: CommonConditionBO
 * @description: 通用条件 BO
 * @date 2022/3/24 18:00
 */
@Data
@ApiModel("通用条件 BO")
public class CommonConditionBO extends SearchEntityBO {

    @ApiModelProperty(value = "字段中文名")
    private String name;

    @ApiModelProperty(value = "连接条件")
    private FieldSearchEnum conditionType;

    public FieldSearchEnum getConditionType() {
        return FieldSearchEnum.parse(getType());
    }
}
