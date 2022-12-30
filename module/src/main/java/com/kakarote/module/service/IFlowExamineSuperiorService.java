package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.FlowExamineSuperior;

import java.util.List;

/**
 * <p>
 * 审批流程主管审批记录表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
public interface IFlowExamineSuperiorService extends BaseService<FlowExamineSuperior> {

    List<FlowExamineSuperior> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId);

}
