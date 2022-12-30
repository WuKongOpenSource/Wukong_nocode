package com.kakarote.module.mapper;

import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.module.entity.PO.ModuleFieldData;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 模块字段值表 Mapper 接口
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
@Repository
public interface ModuleFieldDataMapper extends BaseMapper<ModuleFieldData> {

    /**
     * 获取制定数据ID的所有字段值对象
     *
     * @param DataId
     * @return
     */
	@Select(SQL_GET_BY_DATA_ID)
	List<ModuleFieldData> getByDataId(@Param("dataId") Long DataId);

    /**
     * 获取制定字段字段值
     *
     * @param DataId
     * @param fieldId
     * @return
     */
	@Select(SQL_GET_BY_DATA_ID_AND_FIELD_ID)
	String getValueByDataIdAndFieldId(@Param("dataId") Long DataId, @Param("fieldId") Long fieldId);

    /**
     * 获取制定字段字段值对象
     *
     * @param DataId
     * @param fieldId
     * @return
     */
	@Select(SQL_GET_VALUE_BY_DATA_ID_AND_FIELD_ID)
	ModuleFieldData getByDataIdAndFieldId(@Param("dataId") Long DataId, @Param("fieldId") Long fieldId);

    /**
     * 获取主字段字段值
     *
     * @param DataId
     * @return
     */
    @Select(SQL_GET_MAIN_FIELD_VALUE_BY_DATA_ID)
    String getMainFieldValue(@Param("dataId") Long DataId);

	/**
	 * 根据数据Id获取字段名和字段值map
	 *
	 * @param DataId
	 * @return
	 */
	@Select(SQL_GET_FIELD_NAME_VALUE_BY_DATA_ID)
	List<Map<String, Object>> getFieldNameValueByDataId(@Param("dataId") Long DataId);

	String SQL_GET_BY_DATA_ID = "select * from wk_module_field_data where data_id = #{dataId}";

	String SQL_GET_VALUE_BY_DATA_ID_AND_FIELD_ID = "select * from wk_module_field_data where data_id = #{dataId} " +
			"and field_id = #{fieldId}";

	String SQL_GET_BY_DATA_ID_AND_FIELD_ID = "select value from wk_module_field_data where data_id = #{dataId} and field_id = #{fieldId}";

	String SQL_GET_FIELD_NAME_VALUE_BY_DATA_ID = "select b.field_name, a.*\n" +
			"from wk_module_field_data a\n" +
			"         left join wk_module_field b on a.field_id = b.field_id and a.version = b.version and a.module_id = b.module_id\n" +
			"where a.data_id = #{dataId}";

    String SQL_GET_MAIN_FIELD_VALUE_BY_DATA_ID = "select value\n" +
			"from wk_module_field_data a\n" +
			"         inner join wk_module b on a.module_id = b.module_id and a.field_id = b.main_field_id\n" +
			"    and b.is_active = true and b.status = 1\n" +
			"where data_id = #{dataId}";


	List<String> queryMultipleMainFieldValue(@Param("dataIds") List<String> dataIds);
}
