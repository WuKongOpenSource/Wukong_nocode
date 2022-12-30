package com.kakarote.module.mapper;

import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.module.entity.PO.CustomNoticeRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zjj
 * @title: CustomNoticeRecordMapper
 * @description: 自定义提醒记录 mapper
 * @date 2022/3/23 17:10
 */
public interface CustomNoticeRecordMapper extends BaseMapper<CustomNoticeRecord> {

    String SQL_QUERY_LIST = "select * from wk_custom_notice_record where status = #{status} limit #{limit}";

    /**
     * 根据状态查询自定义提醒记录
     *
     * @param status
     * @param limit
     * @return
     */
    @Select(SQL_QUERY_LIST)
    List<CustomNoticeRecord> queryList(@Param("status") Integer status, @Param("limit") Integer limit);
}
