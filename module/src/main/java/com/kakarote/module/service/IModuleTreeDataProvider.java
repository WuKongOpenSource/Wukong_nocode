package com.kakarote.module.service;

import com.kakarote.module.entity.BO.TreeDataBO;
import com.kakarote.module.entity.BO.TreeDataQueryBO;

/**
 * @author : zjj
 * @since : 2022/12/27
 */
public interface IModuleTreeDataProvider {

    /**
     * 获取指定树字段的指定节点树型数据
     *
     * @param queryBO queryBO
     * @return data
     */
    TreeDataBO queryTreeDataByDataId(TreeDataQueryBO queryBO);
}
