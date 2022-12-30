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

/**
 * <p>
 * 审批流程指定成员记录表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_flow_examine_member")
@ApiModel(value="FlowExamineMember对象", description="审批流程指定成员记录表")
public class FlowExamineMember implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

	@ApiModelProperty(value = "模块ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "审批流程ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long flowId;

    @ApiModelProperty(value = "审批人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @ApiModelProperty(value = "1 依次审批 2 会签 3 或签")
    private Integer type;

    @ApiModelProperty(value = "排序规则")
    private Integer sort;

    @ApiModelProperty(value = "批次ID")
    private String batchId;

    @ApiModelProperty(value = "流程元数据ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long flowMetadataId;

}
