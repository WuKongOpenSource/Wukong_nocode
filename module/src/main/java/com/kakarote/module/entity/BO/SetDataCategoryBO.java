package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zjj
 * @title: SetDataCategoryBO
 * @description: 设置数据分组BO
 * @date 2022/4/1 14:38
 */
@Data
@ApiModel(value = "设置数据分组BO", description = "设置数据分组BO")
public class SetDataCategoryBO {

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @NotNull
    @ApiModelProperty(value = "数据ID")
    private List<Long> dataIds;

    @ApiModelProperty(value = "分类ID")
    private Long categoryId;
}
