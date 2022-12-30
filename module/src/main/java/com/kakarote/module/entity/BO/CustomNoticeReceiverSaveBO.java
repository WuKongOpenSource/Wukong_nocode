package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author zjj
 * @title: CustomNoticeReceiverSaveBO
 * @description: 自定义通知接收人保存 BO
 * @date 2022/3/23 10:21
 */
@Data
@ApiModel(value = "自定义通知接收人保存 BO", description = "自定义通知接收人保存 BO")
public class CustomNoticeReceiverSaveBO {

    @NotEmpty
    @ApiModelProperty("模块ID")
    private Long moduleId;

    @NotEmpty
    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty("提醒ID")
    private Long noticeId;

    @ApiModelProperty("提醒内容")
    private String content;

    @ApiModelProperty("通知创建人")
    private Boolean noticeCreator;

    @ApiModelProperty("通知负责人")
    private Boolean noticeOwner;

    @ApiModelProperty("指定成员")
    private List<Long> noticeUser;

    @ApiModelProperty("指定角色")
    private List<Long> noticeRole;

    @ApiModelProperty("负责人上级")
    private List<Integer> parentLevel;

    @ApiModelProperty("人员字段")
    private List<Long> userField;

    @ApiModelProperty("部门字段")
    private List<Long> deptField;
}
