package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zjj
 * @title: ModuleFieldDataCommonBO
 * @description: 通用模块字段值BO
 * @date 2022/3/25 11:45
 */
@Data
@ApiModel(value = "ModuleFieldDataCommonBO 对象", description = "通用模块字段值BO")

public class ModuleFieldDataCommonBO {

    @ApiModelProperty(value = "数据ID")
    private Long dataId;

    @ApiModelProperty(value = "创建人ID")
    private Long createUserId;

    @ApiModelProperty(value = "创建人用户名")
    private String createUserName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "负责人ID")
    private Long ownerUserId;

    @ApiModelProperty(value = "负责人用户名")
    private String ownerUserName;

    @ApiModelProperty(value = "类型 0 审批 1 其他")
    private Integer type;

    @ApiModelProperty(value = "当前节点ID")
    private Long currentFlowId;

    @ApiModelProperty(value = "节点状态 0 待处理 1 已处理 2 拒绝 3 处理中 4 撤回 5 未提交 8 作废 9 忽略")
    private Integer flowStatus;

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "分类ID")
    private Long categoryId;

}
