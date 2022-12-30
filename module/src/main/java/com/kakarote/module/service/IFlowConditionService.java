package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.ModuleFieldValueBO;
import com.kakarote.module.entity.PO.Flow;
import com.kakarote.module.entity.PO.FlowCondition;

import java.util.List;

/**
 * <p>
 * 流程条件表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
public interface IFlowConditionService extends BaseService<FlowCondition> {

	/**
	 * 获取条件节点的所有条件分支
	 *
	 * @param flowId
	 * @param version
	 * @return
	 */
	List<FlowCondition> getByFlowId(Long flowId, Integer version);

	/**
	 * 获取条件节点的下一个节点
	 *
	 * @param flowId
	 * @param version
	 * @param fieldValueBOS
	 * @return
	 */
	Flow getNextFlow(Long flowId, Integer version, List<ModuleFieldValueBO> fieldValueBOS);

	/**
	 * 获取模块的所有条件节点
	 *
	 * @param moduleId
	 * @param version
	 * @param flowMetaDataId
	 * @return
	 */
	List<FlowCondition> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId);

}
