package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zjj
 * @description: 转移负责人BO
 * @date 2021/8/14 10:16
 */
@Data
@ApiModel(value = "转移负责人BO", description = "转移负责人BO")
public class TransferOwnerBO {

    @NotNull
    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "数据ID列表")
    private List<Long> dataIds;

    @NotNull
    @ApiModelProperty(value = "负责人ID")
    private Long ownerUserId;
}
