package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.FlowExamineMember;

import java.util.List;

/**
 * <p>
 * 审批流程指定成员记录表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
public interface IFlowExamineMemberService extends BaseService<FlowExamineMember> {

    List<FlowExamineMember> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId);

}
