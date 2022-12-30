package com.kakarote.module.mapper;

import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.module.entity.PO.ModuleFieldDataCommon;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-08 17:34
 */
@Repository
public interface ModuleFieldDataCommonMapper extends BaseMapper<ModuleFieldDataCommon> {

	@Select(SQL_GET_BY_DATA_ID)
	ModuleFieldDataCommon getByDataId(@Param("dataId") Long  DataId);

	String SQL_GET_BY_DATA_ID = "select * from wk_module_field_data_common where  data_id = #{dataId}";

    void revertImport(@Param("batchId")String batchId);
}
