package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : zjj
 * @since : 2022/12/1
 */
@Data
@ApiModel("树字段展示字段BO")
public class ModuleTreeBO {

    @ApiModelProperty(value = "展示字段")
    private Long showField;

    @ApiModelProperty(value = "本地临时展示字段 ID")
    private String tempShowField;
}
