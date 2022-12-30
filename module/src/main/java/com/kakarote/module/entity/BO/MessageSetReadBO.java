package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zjj
 * @title: MessageSetReadBO
 * @description: 系统消息设置已读BO
 * @date 2021/11/2614:08
 */
@Data
@ApiModel("系统消息设置已读BO")
public class MessageSetReadBO {

    @ApiModelProperty(value = "消息ID")
    private List<Long> messageId;

    @ApiModelProperty(value = "全部已读")
    private Boolean allRead = false;
}
