package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.FlowTimeLimit;

import java.util.List;

/**
 * <p>
 * 流程限时处理设置 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-28
 */
public interface IFlowTimeLimitService extends BaseService<FlowTimeLimit> {

	FlowTimeLimit getByModuleIdAndFlowId(Long moduleId, Long flowId);

	/**
	 *  获取模块的流程高级配置
	 *
	 * @param moduleId
	 * @param version
	 * @param flowMetaDataId
	 * @return
	 */
	List<FlowTimeLimit> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId);

}
