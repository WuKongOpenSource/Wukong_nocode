package com.kakarote.module.entity.BO;

import com.kakarote.common.result.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 *  条件搜索请求
 * @author zjj
 * @date 2021/8/12 18:13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("条件搜索请求")
public class ConditionSearchRequest extends PageEntity {

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "目标模块ID")
    private Long targetModuleId;

    @ApiModelProperty(value = "字段Id")
    private Long fieldId;

    @ApiModelProperty(value = "搜索条件中字段值列表")
    private List<ModuleFieldValueBO> fieldValues;

    @ApiModelProperty(value = "高级筛选列表")
    private List<SearchEntityBO> searchList = new ArrayList<>();

}
