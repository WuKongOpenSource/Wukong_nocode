package com.kakarote.module.entity.BO;

import com.kakarote.common.result.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "待办查询 BO", description = "待办查询 BO")
public class ToDoListQueryBO extends PageEntity {

    @ApiModelProperty("查询类型 0 待我处理、1 我发起的、2 抄送我的、3 已完成")
    private Integer queryType = 0;

    @ApiModelProperty("发起人")
    private List<Long> createUserIds;

    @ApiModelProperty("应用 ID")
    private Long applicationId;

    @ApiModelProperty("模块ID")
    private Long moduleId;

    @ApiModelProperty("开始时间")
    private Date fromDate;

    @ApiModelProperty("结束时间")
    private Date toDate;
}
