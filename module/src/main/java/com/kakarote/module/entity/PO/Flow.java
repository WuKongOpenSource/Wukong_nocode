package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 模块流程表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_flow")
@ApiModel(value="Flow对象", description="模块流程表")
public class Flow implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "流程ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long flowId;

    @ApiModelProperty(value = "流程元数据ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long flowMetadataId;

	@ApiModelProperty("模块ID")
	private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "条件ID")
    private Long conditionId;

    @ApiModelProperty(value = "流程名称")
    private String flowName;

    @ApiModelProperty(value = "流程类型  0 条件节点 1 审批节点 2 填写节点 3 抄送节点 4 添加数据 5 更新数据 6 发起人节点")
    private Integer flowType;

    @ApiModelProperty(value = "流程下属类型 如审批节点类型")
    private Integer type;

	@ApiModelProperty(value = "审批找不到用户或者条件均不满足时怎么处理 1 自动通过 2 管理员审批")
	private Integer examineErrorHandling;

    @ApiModelProperty(value = "字段列表")
    private String fieldId;

    @ApiModelProperty(value = "描述文本")
    private String content;

    @ApiModelProperty(value = "优先级 数字越低优先级越高")
    private Integer priority;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "批次ID")
    private String batchId;


}
