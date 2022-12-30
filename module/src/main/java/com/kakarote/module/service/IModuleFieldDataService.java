package com.kakarote.module.service;

import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.FlowConditionData;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldData;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 模块字段值表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
public interface IModuleFieldDataService extends BaseService<ModuleFieldData> {

	/**
	 * 删除模块字段值
	 *
	 * @param dataDeleteBO
	 */
	void delete(ModuleFieldDataDeleteBO dataDeleteBO);

	/**
	 * 获取字段值map（计算公式专用）
	 *
	 * @param dataId 数据ID
	 * @return data
	 */
	Map<Long, Object> queryAllFieldIdDataMap(Long dataId);

	/**
	 * 获取字段值map（自定义字段）
	 *
	 * @param dataId
	 * @return
	 */
	Map<Long, Object> queryFieldIdDataMap(Long dataId);

	/**
	 * 获取自定义字段值
	 *
	 * @param dataId	数据 ID
	 * @return
	 */
	List<ModuleFieldData> queryFieldData(Long dataId);

	/**
	 * 获取指定字段的自定义字段值
	 *
	 * @param dataIds  数据 ID
	 * @param fieldIds 自定义字段 ID
	 * @return data
	 */
	List<ModuleFieldData> queryFieldData(List<Long> dataIds, List<Long> fieldIds);

	/**
	 * 字段值转换
	 *
	 * @param moduleId
	 * @param version
	 * @param fieldValueBOS
	 * @return
	 */
	List<ModuleFieldValueBO> transFieldValue(Long moduleId, Integer version, List<ModuleFieldValueBO> fieldValueBOS);

    /**
     * 获取主字段值
     *
     * @param dataId
     * @return
     */
    String queryMainFieldValue(Long dataId);

	/**
	 * 更新指定字段的值
	 *
	 * @param field
	 * @param value
	 * @param dataId
	 * @param version
	 * @param moduleId
	 */
	void saveOrUpdate(ModuleField field, String value, Long dataId, Integer version, Long moduleId);

	/**
	 * 根据字段ID获取所有的数据ID
	 *
	 * @param fieldId
	 * @param moduleId
	 * @return
	 */
	List<Long> getDataIdsByFieldId(Long fieldId, Long moduleId);

	/**
	 * 转移负责人
	 *
	 * @param transferOwnerBO
	 */
	void transferOwner(TransferOwnerBO transferOwnerBO);

	/**
	 *  字段值验重
	 *
	 * @param checkBO
	 * @return
	 */
	Boolean doubleCheck(DoubleCheckBO checkBO);

    /**
     * 计算公式字段字段值
     *
     * @param fieldDataSaveBO
     * @return
     */
    List<ModuleFieldData> calculateFieldFormula(ModuleFieldDataSaveBO fieldDataSaveBO);

	/**
	 * 设置数据分组
	 *
	 * @param bo
	 */
	void setDataCategory(SetDataCategoryBO bo);

	/**
	 * 根据数据关联字段的fieldId查询到关联模块的主字段的valueList
	 *
	 * @param fieldId
	 * @param queryBO
	 * @return
	 */
    List<ModuleFieldData> getTargetFieldValuesByUnionFieldId(Long fieldId, FieldQueryBO queryBO);

	/**
	 * 数据关联多选-查询多个dataId的主字段值
	 *
	 * @param dataIds 逗号隔开的字符串
	 * @return 逗号隔开的主字段值
	 */
	String queryMultipleMainFieldValue(String dataIds);
}
