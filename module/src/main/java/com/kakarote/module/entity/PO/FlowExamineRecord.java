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
 * 审核记录表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_flow_examine_record")
@ApiModel(value="FlowExamineRecord对象", description="审核记录表")
public class FlowExamineRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "审核记录ID")
    @TableId(value = "record_id", type = IdType.ASSIGN_ID)
    private Long recordId;

	@ApiModelProperty(value = "模块ID")
	private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "流程元数据Id")
    private Long flowMetadataId;

    @ApiModelProperty(value = "流程ID")
    private Long flowId;

    @ApiModelProperty(value = "关联模块数据Id")
    private Long dataId;

    @ApiModelProperty(value = "审核状态 0 未审核 1 审核通过 2 审核拒绝 3 审核中 4 已撤回")
    private Integer examineStatus;

    @ApiModelProperty(value = "类型ID")
    private Long typeId;

    @ApiModelProperty(value = "流程类型 0 系统 1 自定义按钮")
    private Integer flowMetadataType;

    @ApiModelProperty(value = "批次ID")
    private String batchId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "修改人")
    private Long updateUserId;

}
