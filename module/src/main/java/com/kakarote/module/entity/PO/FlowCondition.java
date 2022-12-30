package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 流程条件表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_flow_condition")
@ApiModel(value="FlowCondition对象", description="流程条件表")
public class FlowCondition implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "条件ID")
    private Long conditionId;

    @ApiModelProperty(value = "条件名称")
    private String conditionName;

    @ApiModelProperty(value = "模块ID")
	private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "审批流程ID")
    private Long flowId;

    @ApiModelProperty(value = "优先级 数字越低优先级越高")
    private Integer priority;

    @ApiModelProperty(value = "批次ID")
    private String batchId;

    @ApiModelProperty(value = "流程元数据ID")
    private Long flowMetadataId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

}
