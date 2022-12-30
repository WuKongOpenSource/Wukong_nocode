package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.FlowCopy;

import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-25 15:20
 */
public interface IFlowCopyService extends BaseService<FlowCopy> {

	FlowCopy getByFlowId(Long flowId);

	List<FlowCopy> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId);
}
