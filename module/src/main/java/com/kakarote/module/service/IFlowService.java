package com.kakarote.module.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.Flow;
import com.kakarote.module.entity.PO.FlowExamineRecord;
import com.kakarote.module.entity.PO.FlowMetadata;
import com.kakarote.module.entity.VO.FlowConditionDataVO;
import com.kakarote.module.entity.VO.FlowVO;

import java.util.List;

/**
 * <p>
 * 模块流程表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
public interface IFlowService extends BaseService<Flow> {

	/**
	 * 获取模块的节点
	 *
	 * @param moduleId
	 * @param version
	 * @return
	 */
	List<Flow> getByModuleIdAndVersion(Long moduleId, Integer version);

    /**
     * 获取模块的节点
     *
     * @param moduleId
     * @param version
     * @param metaDataId
     * @return
     */
    List<Flow> getByModuleIdAndVersion(Long moduleId, Integer version, Long metaDataId);

	/**
	 * 获取当前节点的下一个节点
	 *
	 * @param flow
	 * @return
	 */
	Flow getNextFlow(Flow flow);


	/**
	 * 获取条件下的下一个节点
	 *
	 * @param conditionId    条件 ID
	 * @param flowMetadataId 流程元数据 ID
	 * @return
	 */
	Flow getNextConditionFlow(Long conditionId, Long flowMetadataId);

	/**
	 * 从当前条件查所属的条件节点
	 *
	 * @param flow
	 * @return
	 */
	Flow findConditionFlow(Flow flow);

	Flow getByFlowId(Long flowId, Integer version);

    Flow getByFlowId(Long flowId);

	/**
	 * 获取发起人节点
	 *
	 * @param moduleId   模块 ID
	 * @param version    版本号
	 * @param metaDataId 元数据 ID
	 * @return
	 */
	Flow getStartFlow(Long moduleId, Integer version, Long metaDataId);
}
