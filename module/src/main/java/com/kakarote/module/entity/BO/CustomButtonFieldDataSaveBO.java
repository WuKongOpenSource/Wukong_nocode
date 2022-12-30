package com.kakarote.module.entity.BO;

import com.kakarote.module.entity.PO.ModuleFieldData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zjj
 * @title: CustomButtonFieldDataSaveBO
 * @description: 自定义按钮字段值保存
 * @date 2022/3/21 15:34
 */
@Data
@ToString
@ApiModel(value = "CustomButtonFieldDataSaveBO 保存对象", description = "CustomButtonFieldDataSaveBO")
public class CustomButtonFieldDataSaveBO {

    @NotEmpty
    @ApiModelProperty("模块ID")
    private Long moduleId;

    @NotEmpty
    @ApiModelProperty(value = "版本号")
    private Integer version;

    @NotEmpty
    @ApiModelProperty(value = "数据ID")
    private Long dataId;

    @NotEmpty
    @ApiModelProperty(value = "按钮ID")
    private Long buttonId;

    @ApiModelProperty(value = "模块字段值列表")
    private List<ModuleFieldData> fieldDataList = new ArrayList<>();
}
