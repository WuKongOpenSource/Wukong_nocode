package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-31 15:36
 */
@Data
@ApiModel("新增审核记录")
public class ExamineRecordSaveBO {

	@ApiModelProperty(value = "模块ID")
	private Long moduleId;

	@ApiModelProperty(value = "版本号")
	private Integer version;

	@ApiModelProperty("字段数据值")
	List<ModuleFieldValueBO> fieldValueBOS;

	@ApiModelProperty(value = "数据ID")
	private Long dataId;

	@ApiModelProperty(value = "自选成员列表")
	private List<ExamineGeneralBO> optionalList;

	@ApiModelProperty("消息标题")
	private String title;

	@ApiModelProperty("消息内容")
	private String content;

	@ApiModelProperty("创建人ID")
	private Long createUserId;

}
