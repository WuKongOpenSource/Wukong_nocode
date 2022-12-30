package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collection;

/**
 * @author zjj
 * @title: MessageBO
 * @description: 系统消息BO
 * @date 2021/11/269:51
 */
@Data
@ApiModel("系统消息BO")
public class MessageBO {

    @ApiModelProperty(value = "内容")
    private String value;

    @ApiModelProperty(value = "数据ID")
    private Long dataId;

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "模块名称")
    private String moduleName;

    @ApiModelProperty(value = "类型ID")
    private Long typeId;

    @ApiModelProperty(value = "类型 0 系统 1 节点 2 自定义提醒 3 自定义按钮 4团队成员添加移除 ")
    private Integer type;

    @ApiModelProperty(value = "批次ID")
    private String batchId;

    @ApiModelProperty(value = "对应类型名称")
    private String typeName;

    @ApiModelProperty(value = "扩展数据")
    private String extData;

    @ApiModelProperty(value = "节点状态 0 待处理 1 已处理 2 拒绝 3 处理中 4 撤回 5 未提交 8 作废 9 忽略")
    private Integer status;

    @ApiModelProperty(value = "限时处理的值 d代表天，m代表分钟 h代表小时 例 10d代表10天")
    private String timeValue;

    @ApiModelProperty(value = "接收人ID")
    private Collection<Long> receivers;

    @ApiModelProperty(value = "消息创建者 0为系统")
    private Long createUserId;

}
