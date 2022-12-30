package com.kakarote.module.mapper;

import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.module.entity.PO.FlowCondition;
import com.kakarote.module.entity.PO.ModuleFieldDataCommon;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 流程条件表 Mapper 接口
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
public interface FlowConditionMapper extends BaseMapper<FlowCondition> {

    @Select(SQL_GET_BY_CONDITION_ID)
    FlowCondition getByConditionIdAndVersion(Long conditionId, Integer version);

    String SQL_GET_BY_CONDITION_ID = "select * from wk_flow_condition where  condition_id = #{conditionId} and version = #{version}";

}
