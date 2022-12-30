package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.FlowSave;

import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-25 15:32
 */
public interface IFlowSaveService extends BaseService<FlowSave> {

	FlowSave getByFlowId(Long flowId);

	List<FlowSave> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId);
}
