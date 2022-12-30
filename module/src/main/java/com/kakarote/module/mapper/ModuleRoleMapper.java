package com.kakarote.module.mapper;

import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.module.entity.PO.ModuleRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author zjj
 * @title: ModuleRoleMapper
 * @description: 角色Mapper
 * @date 2021/12/115:11
 */
public interface ModuleRoleMapper extends BaseMapper<ModuleRole> {

    @Select(SQL_GET_BY_USER_ID_AND_MODULE_ID)
    ModuleRole getByUserIdAndModuleId(@Param("userId") Long userId, @Param("moduleId") Long moduleId);

    String SQL_GET_BY_USER_ID_AND_MODULE_ID = "select a.*\n" +
            "from wk_module_role a\n" +
            "         left join wk_module_role_user b on a.role_id = b.role_id\n" +
            "         left join wk_module_role_module c on a.role_id = c.role_id\n" +
            "where b.user_id = #{userId}\n" +
            "  and c.module_id = #{moduleId}\n" +
            "order by a.range_type desc\n" +
            "limit 1;";
}
