package com.kakarote.module.entity.BO;

import com.kakarote.module.entity.PO.Flow;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zjj
 * @date: 2021-06-02 17:13
 */
@Data
@ApiModel("当前审批人查询 BO")
public class ExamineUserQueryBO {

	@ApiModelProperty(value = "审核人ID")
	private Long createUserId;

	@ApiModelProperty(value = "当前审批节点")
	private Flow flow;

	@ApiModelProperty(value = "审核记录ID")
	private Long recordId;
}
