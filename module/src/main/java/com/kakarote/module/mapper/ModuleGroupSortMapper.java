package com.kakarote.module.mapper;

import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.module.entity.PO.ModuleGroupSort;
import com.kakarote.module.entity.VO.ModuleGroupSortVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author wwl
 * @date 2022/3/24 19:40
 */

public interface ModuleGroupSortMapper extends BaseMapper<ModuleGroupSort> {

    @Select(" select a.application_id, a.group_id, a.module_id, a.sort, IF(a.module_id <> '' , null ,b.icon) as icon, IF(a.module_id <> '' , null ,b.icon_color) as icon_color" +
            " from wk_module_group_sort a" +
            " left join wk_module_group b on a.group_id = b.id" +
            " where a.application_id = #{applicationId} ORDER BY a.sort")
    List<ModuleGroupSortVO> queryList(@Param("applicationId") Long applicationId);

    @Select("select * from wk_module_group_sort where group_id=#{groupId} and module_id is not null and application_id = #{applicationId} ORDER BY sort desc limit 1")
    ModuleGroupSort getMaxSortInGroup(Long applicationId, Long groupId);

    @Select("select * from wk_module_group_sort where (group_id is null or module_id is null) and application_id =#{applicationId} ORDER BY sort desc limit 1")
    ModuleGroupSort getMaxSortInApplication(Long applicationId);

    @Select("SELECT * FROM wk_module_group_sort WHERE (module_id IS NULL OR group_id IS NULL) and application_id =#{applicationId}  ORDER BY sort ")
    List<ModuleGroupSortVO> getOutterList(Long applicationId);

    @Select("select * from wk_module_group_sort WHERE group_id =#{groupId} and module_id is not null ORDER BY sort ")
    List<ModuleGroupSortVO> getInnerModule(Long groupId);

    @Select("select * from wk_module_group_sort where (group_id IS NULL OR module_id IS NULL) AND application_id = #{applicationId} AND sort BETWEEN #{min} AND #{max} ORDER BY sort ASC")
    List<ModuleGroupSort> getOutterSortBetween(Long applicationId, Integer min, Integer max);
}
