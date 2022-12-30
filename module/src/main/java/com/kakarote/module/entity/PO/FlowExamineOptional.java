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
 * 审批流程自选成员记录表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_flow_examine_optional")
@ApiModel(value="FlowExamineOptional对象", description="审批流程自选成员记录表")
public class FlowExamineOptional implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

	@ApiModelProperty(value = "模块ID")
	private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "审核流程ID")
    private Long flowId;

    @ApiModelProperty(value = "审批人ID")
    private Long userId;

    @ApiModelProperty(value = "角色ID")
    private Long roleId;

    @ApiModelProperty(value = "选择类型 1 自选一人 2 自选多人")
    private Integer chooseType;

    @ApiModelProperty(value = "1 依次审批 2 会签 3 或签")
    private Integer type;

    @ApiModelProperty(value = "排序规则")
    private Integer sort;

    @ApiModelProperty(value = "批次ID")
    private String batchId;

    @ApiModelProperty(value = "流程元数据ID")
    private Long flowMetadataId;

    @ApiModelProperty(value = "选择范围 1 全公司 2 指定成员 3 指定角色 ")
    private Integer rangeType;

}
