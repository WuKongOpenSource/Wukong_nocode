package com.kakarote.module.entity.BO;

import com.kakarote.module.entity.PO.ModuleFieldSort;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zjj
 * @description: 字段调整对象
 * @date 2021/8/9 13:48
 */
@Data
@ApiModel(value = "字段调整对象", description = "字段调整对象")
public class ModuleFieldSortBO {

    @ApiModelProperty(value = "不隐藏的字段")
    private List<ModuleFieldSort> noHideFields;

    @ApiModelProperty(value = "不隐藏的字段")
    private List<ModuleFieldSort> hideFields;

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "分类ID")
    private Long categoryId;
}
