package com.kakarote.module.mapper;

import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.module.entity.PO.ModuleFieldSort;
import com.kakarote.module.entity.VO.ModuleFieldSortVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 字段排序表 Mapper 接口
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-19
 */
public interface ModuleFieldSortMapper extends BaseMapper<ModuleFieldSort> {

    String SQL_QUERY_LIST_HEAD = "SELECT field_id,field_name,name,type,style AS width,is_hide,is_lock FROM " +
            "wk_module_field_sort AS a WHERE module_id=#{moduleId} AND user_id=#{userId} " +
            "AND category_id is null AND type NOT IN (8,50) ORDER BY is_lock DESC,sort ASC";

    String SQL_QUERY_LIST_HEAD_BY_CATEGORY_ID = "SELECT field_id,field_name,name,type,style AS width,is_hide,is_lock FROM " +
            "wk_module_field_sort AS a WHERE module_id=#{moduleId} AND user_id=#{userId} " +
            "AND category_id = #{categoryId} AND type NOT IN (8,50) ORDER BY is_lock DESC,sort ASC";

    @Select(SQL_QUERY_LIST_HEAD)
    List<ModuleFieldSortVO> queryListHead(@Param("moduleId") Long moduleId,@Param("userId") Long userId);

    @Select(SQL_QUERY_LIST_HEAD_BY_CATEGORY_ID)
    List<ModuleFieldSortVO> queryListHeadByCategoryId(@Param("moduleId") Long moduleId, @Param("categoryId") Long categoryId, @Param("userId") Long userId);
}
