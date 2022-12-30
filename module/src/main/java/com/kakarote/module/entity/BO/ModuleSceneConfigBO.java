package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zjj
 * @description: 场景设置BO
 * @date 2021/8/9 9:45
 */
@Data
@ApiModel("场景设置BO")
public class ModuleSceneConfigBO {

    @NotNull
    @ApiModelProperty("模块ID")
    private Long moduleId;

    @ApiModelProperty("正常展示ids")
    private List<Long> noHideIds;

    @ApiModelProperty("隐藏ids")
    private List<Long> hideIds;
}
