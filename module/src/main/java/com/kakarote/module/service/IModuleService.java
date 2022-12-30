package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.ModuleSaveBO;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.VO.ModuleListVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 应用模块表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
public interface IModuleService extends BaseService<ModuleEntity> {

	List<ModuleEntity> getAll();

	/**
	 * 获取当前正在使用的模块-正常
	 *
	 * @param moduleId 模块id
	 * @return 模块信息
	 */
	ModuleEntity getNormal(Long moduleId);

	/**
	 * 根据模块和版本号获取模块信息
	 *
	 * @param moduleId 模块 Id
	 * @param version  版本号
	 * @return 模块信息
	 */
	ModuleEntity getByModuleIdAndVersion(Long moduleId, Integer version);

	/**
	 * 初始化模块基础配置
	 *
	 * @param module 模块
	 */
	void initES(ModuleEntity module);

	/**
	 * 自定义字段添加 ES字段
	 *
	 * @param fields   自定义字段
	 * @param moduleId 模块 ID
	 */
	void addESFields(List<ModuleField> fields, Long moduleId);

    /**
     * 查询模块详情
     *
     * @param moduleId 模块id
     * @param version 版本号
     * @return 模块
     */
	ModuleSaveBO queryDetail(Long moduleId, Integer version);

	/**
	 * 查询模块详情
	 *
	 * @param moduleId 模块 ID
	 * @param isLatest 是否最新模块
	 * @return
	 */
	ModuleSaveBO queryById(Long moduleId, Boolean isLatest);

    /**
     * 查询应用下模块列表
     *
     * @param applicationId 应用ID
     * @return 模块列表
     */
    List<ModuleListVO> queryModuleList(Long applicationId);

    /**
     * 删除模块
     *
     * @param ids ids
     */
    void deleteModule(List<Long> ids);

	/**
	 * 查询模块配置
	 *
	 * @param moduleId
	 * @return
	 */
	Map<String, Object> queryModuleConfig(Long moduleId);

	/**
	 * 应用模块数据
	 *
	 * @param moduleData
	 * @param oldNewModuleIdMap
	 */
	void applyModule(Map<String, Object> moduleData, Map<Long, Long> oldNewModuleIdMap);

    /**
     * 获取最新的模块
     *
     * @param applicationId 应用id
     * @return 模块信息list
     */
    List<ModuleEntity> getLatestModules(Long applicationId);

	/**
	 * 获取 BI 模块
	 *
	 * @param applicationId
	 * @return
	 */
	List<ModuleEntity> getBIModules(Long applicationId);

	/**
	 * 获取应用下正在使用的模块
	 *
	 * @param applicationId
	 * @return
	 */
	List<ModuleSaveBO> getActiveModules(Long applicationId);

    /**
     * 获取模块流程管理员
     *
     * @param moduleId 模块id
     * @param version 版本号
     * @return data
     */
    List<Long> getManagerUserIds(Long moduleId, Integer version);

}
