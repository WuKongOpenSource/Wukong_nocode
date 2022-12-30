package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zjj
 * @title: StageComment
 * @description: 阶段评论表
 * @date 2022/4/13 10:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_stage_comment")
@ApiModel(value = "StageComment 对象", description = "阶段评论表")
public class StageComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

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

    @ApiModelProperty(value = "是否删除")
    private Boolean isDelete;

    @ApiModelProperty("数据ID")
    private Long dataId;

    @ApiModelProperty("模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "创建人")
    private Long createUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
