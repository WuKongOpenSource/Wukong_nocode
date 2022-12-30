package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.FlowExamineRole;

import java.util.List;

/**
 * <p>
 * 审批流程角色审批记录表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
public interface IFlowExamineRoleService extends BaseService<FlowExamineRole> {

    List<FlowExamineRole> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId);

}
