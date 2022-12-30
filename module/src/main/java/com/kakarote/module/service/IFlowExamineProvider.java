package com.kakarote.module.service;

import com.kakarote.module.entity.BO.ExamineBO;
import com.kakarote.module.entity.BO.ExamineRecordSaveBO;
import com.kakarote.module.entity.VO.ExamineRecordReturnVO;

/**
 * @author : zjj
 * @since : 2022/12/27
 */
public interface IFlowExamineProvider {

    /**
     * 保存审批流记录
     *
     * @param recordSaveBO
     * @return
     */
    ExamineRecordReturnVO save(ExamineRecordSaveBO recordSaveBO);
}
