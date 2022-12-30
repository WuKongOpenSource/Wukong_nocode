package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.FlowExamineContinuousSuperior;

import java.util.List;

/**
 * <p>
 * 审批流程连续多级主管审批记录表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
public interface IFlowExamineContinuousSuperiorService extends BaseService<FlowExamineContinuousSuperior> {

    List<FlowExamineContinuousSuperior> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId);

}
