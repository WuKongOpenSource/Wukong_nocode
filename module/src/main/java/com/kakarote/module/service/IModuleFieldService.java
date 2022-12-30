package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.VO.ModuleFieldSortVO;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * <p>
 * 自定义字段表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
public interface IModuleFieldService extends BaseService<ModuleField> {

	/**
	 * 获取模块的字段
	 *
	 * @param moduleId
	 * @param version
	 * @param fieldType 可为null
	 * @return
	 */
	List<ModuleField> getByModuleIdAndVersion(Long moduleId, Integer version, @Nullable Integer fieldType);

	List<ModuleField> getByFieldIds(Long moduleId, List<Long> fieldIds, Integer version);


	/**
	 * 获取正常模块的所有字段
	 *
	 * @param moduleId
	 * @param fieldType
	 * @return
	 */
	List<ModuleField> getByModuleId(Long moduleId, @Nullable Integer fieldType);
	/**
	 * 查询自定义字段列表
	 *
	 * @param queryBO
	 * @return
	 */
    List<ModuleFieldBO> queryList(FieldQueryBO queryBO);

	/**
	 * 查询自定义字段列表(二维数组)
	 *
	 * @param queryBO
	 * @return
	 */
	List<List<ModuleField>> formList(FieldFormQueryBO queryBO);

	List<ModuleField> queryDefaultField(Long moduleId, Integer version);

    /**
     * 验证唯一字段是否存在
     * @param moduleFieldVerifyBO 字段信息
     * @return data
     */
    ModuleFieldVerifyBO verify(ModuleFieldVerifyBO moduleFieldVerifyBO);

	/**
	 * 转换字段列表根据表单定位
	 * */
	<T> List<List<T>> convertFormPositionFieldList(List<T> fieldList, Function<T,Integer> groupMapper,
												   Function<T,Integer> sortMapper,Function<T,Integer> defaultSortMapper);

	/**
	 *  根据字段ID获取字段信息
	 *
	 * @param moduleId
	 * @param fieldId
	 * @param version
	 * @return
	 */
	ModuleField getByFieldId(Long moduleId, Long fieldId, Integer version);

	/**
	 * 根据 fieldName 获取字段信息
	 *
	 * @param moduleId
	 * @param fieldName
	 * @return
	 */
	ModuleField getByFieldName(Long moduleId, String fieldName);

	/**
	 * 根据分组获取字段信息
	 *
	 * @param moduleId
	 * @param groupId
	 * @param version
	 * @return
	 */
	List<ModuleField> getFieldByGroupId(Long moduleId, Integer groupId, Integer version);

	/**
	 *  获取明细表格字段
	 *
	 * @param moduleId
	 * @param groupId
	 * @param version
	 * @return
	 */
	ModuleField getDetailTableFieldByGroupId(Long moduleId, Integer groupId, Integer version);

	/**
	 * 点击导出按钮-弹框所需显示的字段
	 * @param queryBO
	 * @return
	 */
    List<ModuleFieldSortVO> queryExportHeadList(FieldQueryBO queryBO);
}
