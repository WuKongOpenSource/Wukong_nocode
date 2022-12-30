package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-07-20 09:41
 */
@Data
@ApiModel("待办转交BO")
public class TransferFlowBO extends FlowDealDetailQueryBO{

	@ApiModelProperty("当前处理人")
	@NotNull
	private Long fromUserId;

	@ApiModelProperty("转交人")
	@NotEmpty
	private List<Long> toUserIds;

    @ApiModelProperty("备注")
    private String remark;
}
