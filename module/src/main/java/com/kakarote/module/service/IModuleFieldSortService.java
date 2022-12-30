package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.ListHeadQueryBO;
import com.kakarote.module.entity.BO.ModuleFieldSortBO;
import com.kakarote.module.entity.BO.ModuleFieldStyleSaveBO;
import com.kakarote.module.entity.PO.ModuleFieldSort;
import com.kakarote.module.entity.VO.ModuleFieldSortVO;

import java.util.List;

/**
 * <p>
 * 字段排序表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-19
 */
public interface IModuleFieldSortService extends BaseService<ModuleFieldSort> {

    /**
     * 查询列表字段
     *
     * @param moduleId
     * @param version
     * @return
     */
    List<ModuleFieldSortVO> queryListHead(Long moduleId, Integer version);

    /**
     * 列表头查询
     *
     * @param moduleId
     * @param version
     * @param categoryId
     * @return
     */
    List<ModuleFieldSortVO> queryListHead(Long moduleId, Integer version, Long categoryId);

    /**
     * 查询模块列表字段头部信息
     * @param queryBO 模块ID
     * @return data
     */
    List<ModuleFieldSortVO> queryListHead(ListHeadQueryBO queryBO);

    /**
     * 字段样式设置
     *
     * @param saveBO
     */
    void setFieldStyle(ModuleFieldStyleSaveBO saveBO);

    /**
     * 设置表头的隐藏和显示
     *
     * @param fieldSortBO
     */
    void setFieldSort(ModuleFieldSortBO fieldSortBO);
}
