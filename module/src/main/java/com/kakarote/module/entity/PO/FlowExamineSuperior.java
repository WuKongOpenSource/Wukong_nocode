package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 审批流程主管审批记录表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_flow_examine_superior")
@ApiModel(value="FlowExamineSuperior对象", description="审批流程主管审批记录表")
public class FlowExamineSuperior implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

	@ApiModelProperty(value = "模块ID")
	private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "审核流程ID")
    private Long flowId;

    @ApiModelProperty(value = "直属上级级别 1 代表直属上级 2 代表 直属上级的上级")
    private Integer parentLevel;

    @ApiModelProperty(value = "找不到上级时，是否由上一级上级代审批 0 否 1 是")
    private Integer type;

    @ApiModelProperty(value = "批次ID")
    private String batchId;

    @ApiModelProperty(value = "流程元数据ID")
    private Long flowMetadataId;

}
