package com.kakarote.module.mapper;

import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.module.entity.PO.ModuleGroup;
import com.kakarote.module.entity.VO.ModuleGroupVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模块分组
 * @author wwl
 * @date 20220304
 */
public interface ModuleGroupMapper extends BaseMapper<ModuleGroup> {

    /**
     * 查询应用下的分组内容与分组关系
     *
     * @param applicationId 应用id
     * @return list
     */
    List<ModuleGroupVO> getGroupList(@Param("applicationId") Long applicationId);

}
