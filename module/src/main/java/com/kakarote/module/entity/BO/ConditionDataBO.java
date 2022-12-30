package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zjj
 * @description: 条件数据
 * @date 2021/8/13 14:45
 */
@Data
@ApiModel("条件数据")
public class ConditionDataBO {

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "目标字段ID（自定义字段英文标识）")
    private List<String> targetFieldNames;

    @ApiModelProperty(value = "数据ID")
    private Long dataId;

    @ApiModelProperty(value = "搜索条件中字段值列表")
    private List<ModuleFieldValueBO> fieldValues;

    @ApiModelProperty(value = "高级筛选列表")
    private List<SearchEntityBO> searchList = new ArrayList<>();

    @ApiModelProperty(value = "被关联的模块ID")
    private Long relatedModuleId;

    @ApiModelProperty(value = "被关联的模块版本")
    private Integer relatedVersion;
}
