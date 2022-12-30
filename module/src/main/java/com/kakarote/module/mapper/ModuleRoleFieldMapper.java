package com.kakarote.module.mapper;

import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.module.entity.PO.ModuleRoleField;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author zjj
 * @title: ModuleRoleFieldMapper
 * @description: 角色字段关系表
 * @date 2021/12/410:09
 */
public interface ModuleRoleFieldMapper extends BaseMapper<ModuleRoleField> {

    @Select(GET_USER_ROLE_FILED)
    List<Map<String, String>> getUserRoleField(@Param("userId") Long userId, @Param("moduleId") Long moduleId, @Param("version") Integer version);

    String GET_USER_ROLE_FILED = "select a.field_id, b.field_name, b.name, b.field_type, b.type, max(a.mask_type) mask_type\n" +
            "from wk_module_role_field a\n" +
            "         left join wk_module_field b on a.field_id = b.field_id and a.module_id = b.module_id\n" +
            "    and b.version = #{version}\n" +
            "         left join wk_module_role_user c on a.role_id = c.role_id\n" +
            "where c.user_id = #{userId}\n" +
            "  and a.module_id = #{moduleId}\n" +
            "  and a.mask_type > 0\n" +
            "group by a.field_id";

}
