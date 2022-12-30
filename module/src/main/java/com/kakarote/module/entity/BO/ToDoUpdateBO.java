package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "待办更新BO", description = "待办更新BO")
public class ToDoUpdateBO {

    @ApiModelProperty("待办 ID")
    private Long id;

    @ApiModelProperty("待办状态 0 待处理 1 已处理")
    private Integer status;
}
