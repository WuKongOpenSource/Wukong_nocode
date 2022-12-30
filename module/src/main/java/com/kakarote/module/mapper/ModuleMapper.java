package com.kakarote.module.mapper;

import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.module.entity.BO.ModuleSaveBO;
import com.kakarote.module.entity.PO.ModuleEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 应用模块表 Mapper 接口
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
public interface ModuleMapper extends BaseMapper<ModuleEntity> {

	@Select(SQL_SELECT_UNION_MODULES_BY_MODULE_ID)
	List<ModuleEntity> selectUnionModulesByModuleId(@Param("moduleId") Long moduleId, @Param("version") Integer version);

	String SQL_SELECT_UNION_MODULES_BY_MODULE_ID = "select distinct * " +
            " from ( " +
            "         select distinct b.* " +
            "         from wk_module_field_union a " +
            "         left join wk_module b " +
            "         on a.target_module_id = b.module_id " +
            "         where a.module_id = #{moduleId} " +
            "           and a.version = #{version} " +
            "           and a.type = 1 " +
            "           and b.status = 1 and b.is_active = true " +
            " union " +
            "         select distinct b.* " +
            "         from wk_module_field_union a " +
            "         left join wk_module b " +
            "         on a.module_id = b.module_id and a.version = b.version" +
            "         where a.target_module_id = #{moduleId} " +
            "           and a.type = 1 " +
            "           and b.status = 1 and b.is_active = true " +
            "     ) a";

    /**
     * 获取最新的模块
     *
     * @param applicationId
     * @return
     */
    @Select(SQL_GET_LATEST_MODULES)
    List<ModuleEntity> getLatestModules(@Param("applicationId") Long applicationId);

    String SQL_GET_LATEST_MODULES = "select * " +
            "from wk_module as a " +
            "where not exists(select * from wk_module where module_id = a.module_id and version > a.version) " +
            "  and a.application_id = #{applicationId}";

    /**
     * 获取最新版本模块
     */
    @Select(SQL_GET_LATEST_MODULE)
    ModuleEntity getLatestModule(@Param("moduleId") Long moduleId);

    String SQL_GET_LATEST_MODULE = "select * from wk_module where module_id = #{moduleId} order by version desc limit 1";


    /**
     * 获取应用下正在使用的模块
     *
     * @param applicationId
     * @return
     */
    @Select(SQL_GET_ACTIVE_MODULES)
    List<ModuleSaveBO> getActiveModules(@Param("applicationId") Long applicationId);

    String SQL_GET_ACTIVE_MODULES = "select a.*, b.is_enable\n" +
            "from (select * from wk_module a where a.is_active = true and a.status = 1 and application_id = #{applicationId}) a\n" +
            "         left join wk_module_status b on a.module_id = b.module_id";
}
