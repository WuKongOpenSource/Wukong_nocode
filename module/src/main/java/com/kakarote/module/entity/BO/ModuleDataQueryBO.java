package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : zjj
 * @since : 2022/12/5
 */
@Data
@ApiModel("模块数据查询 BO")
public class ModuleDataQueryBO {

    @ApiModelProperty(value = "模块 ID")
    private Long moduleId;

    @ApiModelProperty(value = "数据 ID")
    private List<Long> dataId;
}
