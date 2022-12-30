package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("crm审核对象")
public class ExamineBO {

    @ApiModelProperty("审核记录ID")
    private Long recordId;

    @ApiModelProperty("审核流程ID")
    private Long flowId;

    @ApiModelProperty("节点状态 0 待处理 1 已处理 2 拒绝 3 处理中 4 撤回 5 未提交 8 作废 9 忽略")
    private Integer status;

    @ApiModelProperty("数据ID")
    private Long dataId;

    @ApiModelProperty("审核备注")
    private String remarks;
}
