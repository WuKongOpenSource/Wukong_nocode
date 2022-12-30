package com.kakarote.module.entity.BO;

import com.kakarote.common.entity.SimpleUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zjj
 * @title: StageCommentSaveBO
 * @description: 阶段评论保存BO
 * @date 2022/4/13 10:56
 */
@Data
@ApiModel(value = "阶段评论保存BO", description = "阶段评论保存BO")
public class StageCommentSaveBO {

    @ApiModelProperty(value = "评论Id")
    private Long commentId;

    @ApiModelProperty(value = "阶段流程ID")
    private Long stageSettingId;

    @ApiModelProperty(value = "阶段ID")
    private Long stageId;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "主评论id")
    private Long mainId;

    @ApiModelProperty(value = "回复评论用户ID")
    private Long replyUserId;

    @ApiModelProperty("回复评论用户")
    private SimpleUser replyUser;

    @ApiModelProperty("数据ID")
    private Long dataId;

    @ApiModelProperty("模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建人")
    private Long createUserId;

    @ApiModelProperty("创建人")
    private SimpleUser user;
}
