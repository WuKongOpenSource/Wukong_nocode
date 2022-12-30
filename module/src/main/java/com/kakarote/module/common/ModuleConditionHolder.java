package com.kakarote.module.common;

import com.kakarote.module.entity.BO.CommonUnionConditionBO;
import com.kakarote.module.entity.BO.ConditionDataBO;
import com.kakarote.module.entity.PO.FlowConditionData;

import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-13 15:11
 */
public class ModuleConditionHolder {
	private static ThreadLocal<List<CommonUnionConditionBO>> threadLocal = new ThreadLocal<>();
	private static ThreadLocal<Long> dataIdThreadLocal = new ThreadLocal<>();
	private static ThreadLocal<ConditionDataBO> dataBOThreadLocal = new ThreadLocal<>();
	private static ThreadLocal<List<FlowConditionData>> flowConditionsThreadLocal = new ThreadLocal<>();

	public static List<CommonUnionConditionBO> get(){
		return threadLocal.get();
	}

	public static void set(List<CommonUnionConditionBO> unionConditions) {
		threadLocal.set(unionConditions);
	}

	public static ConditionDataBO getDataBO(){
		return dataBOThreadLocal.get();
	}

	public static void setDataBO(ConditionDataBO dataBO){
		dataBOThreadLocal.set(dataBO);
	}

	public static Long getDataId(){
		return dataIdThreadLocal.get();
	}

	public static void setDataId(Long dataId){
		dataIdThreadLocal.set(dataId);
	}

	public static List<FlowConditionData> getFlowConditions() {
		return flowConditionsThreadLocal.get();
	}

	public static void setFlowConditions(List<FlowConditionData> flowConditions) {
		flowConditionsThreadLocal.set(flowConditions);
	}

	public static void remove() {
		threadLocal.remove();
		dataIdThreadLocal.remove();
        dataBOThreadLocal.remove();
		flowConditionsThreadLocal.remove();
	}



}
