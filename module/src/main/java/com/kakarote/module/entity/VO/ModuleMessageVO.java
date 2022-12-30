package com.kakarote.module.entity.VO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.kakarote.common.entity.SimpleUser;
import com.kakarote.module.entity.PO.Message;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zjj
 * @title: MessageVO
 * @description: 系统消息VO
 * @date 2021/11/2613:41
 */
@Data
@ApiModel("系统消息VO")
public class ModuleMessageVO {

    @ApiModelProperty(value = "应用ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long applicationId;

    @ApiModelProperty(value = "应用名称")
    private String name;

    @ApiModelProperty(value = "模块")
    private List<ModuleIdNameMessageVO> modules = new ArrayList<>();

    @ApiModelProperty(value = "消息列表")
    private List<MessageVO> messages = new ArrayList<>();


    @Data
    @ApiModel("模块")
    public static class ModuleIdNameMessageVO {

        @ApiModelProperty(value = "模块ID")
        @JsonSerialize(using = ToStringSerializer.class)
        private Long moduleId;

        @ApiModelProperty(value = "模块名称")
        private String moduleName;

        // 产品说不需要模块排序
        @ApiModelProperty(value = "消息列表")
        private List<MessageVO> messages = new ArrayList<>();
    }
    @Data
    @ApiModel("模块")
    public static class MessageVO extends Message{

        @ApiModelProperty(value = "创建人")
        private SimpleUser createUser;

        @ApiModelProperty(value = "接收人")
        private SimpleUser receiverUser;
    }

}
