package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.StageCommentDeleteBO;
import com.kakarote.module.entity.BO.StageCommentSaveBO;
import com.kakarote.module.entity.PO.StageComment;

import java.util.List;

/**
 * @author zjj
 * @title: IStageCommentService
 * @description: 阶段评论表服务接口
 * @date 2022/4/13 10:54
 */
public interface IStageCommentService extends BaseService<StageComment> {

    /**
     * 保存阶段评论
     *
     * @param saveBO
     */
    StageComment saveStageComment(StageCommentSaveBO saveBO);

    /**
     * 删除评论
     *
     * @param deleteBO
     */
    void deleteStageComment(StageCommentDeleteBO deleteBO);

    /**
     * 评论列表
     *
     * @param dataId
     * @return
     */
    List<StageCommentSaveBO> queryList(Long dataId);
}
