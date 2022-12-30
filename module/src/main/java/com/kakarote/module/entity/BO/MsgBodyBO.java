package com.kakarote.module.entity.BO;


import com.kakarote.module.constant.MessageTagEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-20 18:13
 */
@Data
@ApiModel(value = "消息发送求送", description = "消息发送求送")
public class MsgBodyBO {
	@ApiModelProperty(value = "消息 KEY")
	private String msgKey;

	@ApiModelProperty(value = "模块 ID")
	private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

	@ApiModelProperty(value = "模块 ID列表")
	private List<Long> moduleIds;

	@ApiModelProperty(value = "数据 ID")
	private Long dataId;

	@ApiModelProperty(value = "数据 ID列表")
	private List<Long> dataIds;

	@ApiModelProperty(value = "字段 ID列表")
	private List<Long> fileIds;

	@ApiModelProperty(value = "流程ID")
	private Long flowId;

	@ApiModelProperty(value = "审核记录ID")
	private Long recordId;

	@ApiModelProperty(value = "消息 TAG")
	private MessageTagEnum msgTag;

    @ApiModelProperty(value = "旧数据")
    private List<ModuleFieldValueBO> oldData = new ArrayList<>();

	@ApiModelProperty(value = "当前数据")
	private List<ModuleFieldValueBO> currentData;

	@ApiModelProperty(value = "用户 ID")
	private Long userId;

	@ApiModelProperty(value = "服务端发送消息时间")
	private Long delayTime;
}
