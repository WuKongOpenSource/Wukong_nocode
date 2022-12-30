package com.kakarote.module.entity.BO;

import com.kakarote.module.entity.PO.ModuleFieldData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zjj
 * @title: ModuleDataCheckRequestBO
 * @description: 数据校验请求
 * @date 2022/3/26 15:12
 */
@Data
@ApiModel("数据校验请求BO")
public class ModuleDataCheckRequestBO {

    @NotEmpty
    @ApiModelProperty("模块ID")
    private Long moduleId;

    @NotEmpty
    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "模块字段值列表")
    private List<ModuleFieldData> fieldDataList = new ArrayList<>();
}
