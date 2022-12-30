package com.kakarote.module.mapper;

import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.module.entity.PO.ModuleRoleUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author zjj
 * @title: ModuleRoleUserMapper
 * @description: 角色用户表
 * @date 2021/12/213:08
 */
public interface ModuleRoleUserMapper extends BaseMapper<ModuleRoleUser> {


    /**
     * 获取用户的权限
     *
     * @param userId
     * @return
     */
    @Select(SQL_GET_USER_AUTH)
    List<Map<String, String>> getUserAuth(@Param("userId") Long userId);

    /**
     * 获取用户的字段权限
     *
     * @param userId
     * @param moduleId
     * @return
     */
    @Select(SQL_GET_USER_FIELD_AUTH)
    List<Map<String, String>> getUserFieldAuth(@Param("userId") Long userId, @Param("moduleId") Long moduleId, @Param("version") Integer version);

    String SQL_GET_USER_AUTH = "select a.role_id, a.role_name, b.user_id, b.application_id, c.module_id,c.category_id, c.auth\n" +
            "from wk_module_role a\n" +
            "         left join wk_module_role_user b on a.role_id = b.role_id\n" +
            "         left join wk_module_role_module c on a.role_id = c.role_id\n" +
            "where b.user_id = #{userId}";

    String SQL_GET_USER_FIELD_AUTH = "select c.field_id, d.name, d.field_name, d.field_type, d.type, max(c.auth_level) auth_level\n" +
            "from wk_module_role_user a\n" +
            "         left join wk_module_role b on a.role_id = b.role_id\n" +
            "         left join wk_module_role_field c on b.role_id = c.role_id\n" +
            "         left join wk_module_field d on c.field_id = d.field_id and d.version = #{version}\n" +
            "where a.user_id = #{userId}\n" +
            "  and c.module_id = #{moduleId}\n" +
            "group by c.field_id";

}
