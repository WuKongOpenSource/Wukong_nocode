package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.FlowExamineOptional;

import java.util.List;

/**
 * <p>
 * 审批流程自选成员记录表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
public interface IFlowExamineOptionalService extends BaseService<FlowExamineOptional> {

    List<FlowExamineOptional> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId);

}
