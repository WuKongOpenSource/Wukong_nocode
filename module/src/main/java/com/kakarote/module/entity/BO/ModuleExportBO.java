package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @author wwl
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@ApiModel(value="导出传参对象", description="导出传参对象")
public class ModuleExportBO extends FieldQueryBO {

    @ApiModelProperty(value = "选中导出的dataIds")
    private List<Long> ids;

    @ApiModelProperty(value = "fieldId列表")
    private List<Long> sortIds;

    @ApiModelProperty(value = "搜索条件")
    private SearchBO search;

    @ApiModelProperty(value = " 1.xls 2.csv")
    private  Integer isXls = 1;
}
