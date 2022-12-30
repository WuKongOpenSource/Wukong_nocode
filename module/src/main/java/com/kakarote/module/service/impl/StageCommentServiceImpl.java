package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.BaseUtil;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.entity.BO.StageCommentDeleteBO;
import com.kakarote.module.entity.BO.StageCommentSaveBO;
import com.kakarote.module.entity.PO.StageComment;
import com.kakarote.module.mapper.StageCommentMapper;
import com.kakarote.module.service.IStageCommentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: StageCommentServiceImpl
 * @description: 阶段评论表服务实现
 * @date 2022/4/13 10:54
 */
@Service
public class StageCommentServiceImpl extends BaseServiceImpl<StageCommentMapper, StageComment> implements IStageCommentService {

    @Override
    public StageComment saveStageComment(StageCommentSaveBO saveBO) {
        StageComment stageComment = BeanUtil.copyProperties(saveBO, StageComment.class);
        stageComment.setCommentId(BaseUtil.getNextId());
        stageComment.setCreateUserId(UserUtil.getUserId());
        stageComment.setCreateTime(DateUtil.date());
        save(stageComment);
        return stageComment;
    }

    @Override
    public void deleteStageComment(StageCommentDeleteBO deleteBO) {
        lambdaUpdate()
                .set(StageComment::getIsDelete, true)
                .eq(StageComment::getModuleId, deleteBO.getModuleId())
                .eq(StageComment::getDataId, deleteBO.getDataId())
                .eq(StageComment::getCommentId, deleteBO.getCommentId())
                .update();
    }

    @Override
    public List<StageCommentSaveBO> queryList(Long dataId) {
        List<StageComment> comments = lambdaQuery()
                .eq(StageComment::getDataId, dataId)
                .eq(StageComment::getIsDelete, false)
                .orderByDesc(StageComment::getCreateTime)
                .list();
        return comments.stream().map(c -> {
            StageCommentSaveBO saveBO = BeanUtil.copyProperties(c, StageCommentSaveBO.class);
            saveBO.setUser(UserCacheUtil.getSimpleUser(saveBO.getCreateUserId()));
            saveBO.setReplyUser(Optional.ofNullable(saveBO.getReplyUserId()).map(s -> UserCacheUtil.getSimpleUser(c.getReplyUserId())).orElse(null));
            return saveBO;
        }).collect(Collectors.toList());
    }
}
