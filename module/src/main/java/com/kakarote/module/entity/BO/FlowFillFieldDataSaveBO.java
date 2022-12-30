package com.kakarote.module.entity.BO;

import com.kakarote.module.entity.PO.ModuleFieldData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 填写节点保存数据字段值BO
 * @author: zjj
 * @date: 2021-11-20 10:17
 */
@Data
@ToString
@ApiModel(value = "FlowFillFieldDataSaveBO 保存对象", description = "FlowFillFieldDataSaveBO")
public class FlowFillFieldDataSaveBO {

    @ApiModelProperty(value = "模块ID")
    @NotNull
    private Long moduleId;

    @ApiModelProperty(value = "数据ID")
    @NotNull
    private Long dataId;

    @ApiModelProperty(value = "流程ID")
    @NotNull
    private Long flowId;

    @ApiModelProperty("审核备注")
    private String remarks;

    @ApiModelProperty(value = "模块字段值列表")
    private List<ModuleFieldData> fieldDataList = new ArrayList<>();

}
