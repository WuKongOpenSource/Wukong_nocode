package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-31 16:26
 */
@Data
public class ExamineGeneralBO {

	@NotNull
	@ApiModelProperty("流程ID")
	private Long flowId;

	@ApiModelProperty("用户列表")
	private List<Long> userList;

	@ApiModelProperty("角色列表")
	private List<Long> roleList;
}
