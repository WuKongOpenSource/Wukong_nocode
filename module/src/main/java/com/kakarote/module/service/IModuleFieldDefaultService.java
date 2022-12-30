package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.ConditionSearchRequest;
import com.kakarote.module.entity.BO.ModuleDefaultValueBO;
import com.kakarote.module.entity.BO.ModuleFieldDataSaveBO;
import com.kakarote.module.entity.PO.ModuleFieldDefault;
import com.kakarote.module.entity.VO.ModuleDefaultValueVO;
import com.kakarote.module.entity.VO.ModuleFieldValueVO;

import java.util.List;

/**
 * <p>
 * 字段默认值配置表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-03-06
 */
public interface IModuleFieldDefaultService extends BaseService<ModuleFieldDefault> {

	/**
	 * 查询字段默认值配置
	 *
	 * @param moduleId 模块ID
	 * @param version  版本号
	 * @return data
	 */
	List<ModuleDefaultValueBO> queryDefaultValueList(Long moduleId, Integer version);

    /**
     * 查询字段默认值（固定值）
     *
     * @param moduleId 模块ID
     * @param version  版本号
     * @return data
     */
    List<ModuleDefaultValueVO> values(Long moduleId, Integer version);

	/**
	 * 查询字段默认值（自定义筛选）
	 *
	 * @param searchBO 查询条件
	 * @return data
	 */
	List<ModuleFieldValueVO> values(ConditionSearchRequest searchBO);

	/**
	 * 查询字段默认值（公式）
	 *
	 * @param saveBO
	 * @return
	 */
	List<ModuleFieldValueVO> values(ModuleFieldDataSaveBO saveBO);

	/**
	 * 通过模块ID、版本号查询
	 *
	 * @param moduleId 模块ID
	 * @param version 版本号
	 * @return data
	 */
	List<ModuleFieldDefault> getByModuleIdAndVersion(Long moduleId, Integer version);
}
