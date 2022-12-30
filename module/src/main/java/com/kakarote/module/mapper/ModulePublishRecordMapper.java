package com.kakarote.module.mapper;

import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.module.entity.PO.ModulePublishRecord;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zjj
 * @description: ModulePublishRecordMapper
 * @date 2022/6/17
 */
public interface ModulePublishRecordMapper extends BaseMapper<ModulePublishRecord> {


    /**
     * 获取每个应用下模块最新的发布记录
     *
     * @return
     */
    @Select(SQL_QUERY_LATEST_RECORD_GROUP_BY_APP_ID)
    List<ModulePublishRecord> getLatestRecordGroupByAppId();

    String SQL_QUERY_LATEST_RECORD_GROUP_BY_APP_ID = "select *\n" +
            "from wk_module_publish_record a\n" +
            "where not exists(select id\n" +
            "                 from wk_module_publish_record b\n" +
            "                 where b.application_id = a.application_id and b.create_time > a.create_time)";
}
