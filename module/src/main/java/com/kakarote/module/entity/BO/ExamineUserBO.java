package com.kakarote.module.entity.BO;

import com.kakarote.module.entity.PO.Flow;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-06-02 15:32
 */
@Data
@ApiModel("当前审批人BO")
public class ExamineUserBO {

	@ApiModelProperty("当前审批人列表")
	private List<Long> userIds;

	@ApiModelProperty("当前审批角色")
	private Long roleId;

	@ApiModelProperty("1 依次审批 2 会签 3 或签")
	private Integer type;

	@ApiModelProperty("审批节点")
	private Flow examineFlow;
}
