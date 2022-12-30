package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 通用的ES nested BO
 *
 * @author zjj
 * @since  2022/2/2814:34
 */
@Data
@ApiModel("通用的ES nested BO")
public class CommonESNestedBO {

    @ApiModelProperty(value = "code")
    private String code;

    @ApiModelProperty(value = "key")
    private String key;

    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "value")
    private String value;

    @ApiModelProperty(value = "开始时间")
    private String fromDate;

    @ApiModelProperty(value = "结束时间")
    private String toDate;

    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "排序")
    private Integer sort;
}
