package com.kakarote.module.entity.VO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-31 18:05
 */
@Data
@ApiModel("新增审核记录返回VO")
@NoArgsConstructor
public class ExamineRecordReturnVO implements Serializable {

	@ApiModelProperty("审核记录ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long recordId;

	@ApiModelProperty("审核状态")
	private Integer examineStatus;

	@ApiModelProperty("审核历史Id")
	private List<Long> examineLogIds;

	@ApiModelProperty("审核用户Id")
	private List<Long> examineUserIds;

	public ExamineRecordReturnVO(Long recordId, Integer examineStatus) {
		this.recordId = recordId;
		this.examineStatus = examineStatus;
	}
}
