package com.kakarote.module.service;

import com.kakarote.module.entity.BO.ExamineBO;

/**
 * @author : zjj
 * @since : 2022/12/28
 */
public interface IAuditExamineProvider {


    /**
     * 审批
     *
     * @param examineBO
     */
    void auditExamine(ExamineBO examineBO);
}
