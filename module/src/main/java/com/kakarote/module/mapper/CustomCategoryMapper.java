package com.kakarote.module.mapper;

import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.module.entity.PO.CustomCategory;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zjj
 * @title: CustomCategoryMapper
 * @description: 自定义模块分类 mapper
 * @date 2022/3/29 15:21
 */
public interface CustomCategoryMapper extends BaseMapper<CustomCategory> {

    @Select(SQL_GET_ALL)
    List<CustomCategory> getALL();

        String SQL_GET_ALL = "select a.*\n" +
                "from wk_custom_category a\n" +
                "         inner join wk_module b\n" +
                "                   on a.module_id = b.module_id and a.version = b.version and b.is_active = true and status = 1";
}
