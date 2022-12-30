package com.kakarote.module.entity.BO;

import com.kakarote.module.entity.PO.CustomButton;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("流程处理详情 BO")
public class FlowDealDetailBO {

    @ApiModelProperty(value = "审核记录ID")
    private Long recordId;

    @ApiModelProperty("自定义按钮")
    private CustomButton customButton;

    @ApiModelProperty(value = "负责人ID")
    private Long userId;

    @ApiModelProperty(value = "负责人名称")
    private String userName;

    @ApiModelProperty(value = "节点状态 0 待处理 1 已处理 2 拒绝 3 处理中 4 撤回 5 未提交 8 作废 9 忽略")
    private Integer flowStatus;

    @ApiModelProperty(value = "修改时间")
    private Date dealTime;
}
