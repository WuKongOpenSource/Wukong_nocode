package com.kakarote.module.entity.VO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value="场景VO")
public class ModuleSceneVO {

    @ApiModelProperty("场景ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sceneId;

    @ApiModelProperty(value = "场景名称")
    private String name;

    @ApiModelProperty(value = "排序ID")
    private Integer sort;

    @ApiModelProperty(value = "data")
    private String data;

    @ApiModelProperty(value = "1全部 2 我负责的 3 我下属负责的 0 自定义")
    private Integer isSystem;

    @ApiModelProperty(value = "是否默认 0 否 1是")
    private Integer isDefault;

    @ApiModelProperty(value = "1隐藏")
    private Integer isHide;

    @NotNull
    @ApiModelProperty(value = "模块ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long moduleId;
}
