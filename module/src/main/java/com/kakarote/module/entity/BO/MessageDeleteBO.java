package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zjj
 * @title: MessageDeleteBO
 * @description: 删除系统消息BO
 * @date 2021/11/3011:41
 */
@Data
@ApiModel("删除系统消息BO")
public class MessageDeleteBO {

    @ApiModelProperty(value = "消息ID")
    private List<Long> messageId;

    @ApiModelProperty(value = "删除已读消息")
    private Boolean deleteReadMessage = false;
}
