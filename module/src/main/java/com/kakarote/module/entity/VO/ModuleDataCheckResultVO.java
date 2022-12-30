package com.kakarote.module.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjj
 * @title: ModuleDataCheckResultVO
 * @description: 数据校验结果VO
 * @date 2022/3/26 15:20
 */
@Data
@ApiModel("数据校验结果VO")
public class ModuleDataCheckResultVO {

    @ApiModelProperty(value = "0 未通过, 1 通过, 2 规则错误")
    private Integer result;

    @ApiModelProperty(value = "提示")
    private String tip;
}
