package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjj
 * @title: StageCommentDeleteBO
 * @description: 阶段评论删除BO
 * @date 2022/4/13 11:10
 */
@Data
@ApiModel(value = "阶段评论删除BO", description = "阶段评论删除BO")
public class StageCommentDeleteBO {

    @ApiModelProperty("模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "数据ID")
    private Long dataId;

    @ApiModelProperty(value = "评论Id")
    private Long commentId;
}
