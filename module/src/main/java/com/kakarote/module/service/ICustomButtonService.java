package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.CustomButtonFieldDataSaveBO;
import com.kakarote.module.entity.BO.CustomButtonSaveBO;
import com.kakarote.module.entity.BO.ExecuteButtonRequestBO;
import com.kakarote.module.entity.PO.CustomButton;
import com.kakarote.module.entity.PO.FlowExamineRecord;

import java.util.List;

/**
 * @author zjj
 * @title: ICustomButtonService
 * @description: 自定义按钮服务接口
 * @date 2022/3/16 14:48
 */
public interface ICustomButtonService extends BaseService<CustomButton> {

    /**
     *  获取模块的自定义按钮
     *
     * @param moduleId
     * @param version
     * @return
     */
    List<CustomButton> getByModuleIdAndVersion(Long moduleId, Integer version);

    /**
     * 获取指定的自定义按钮
     *
     * @param moduleId
     * @param buttonId
     * @param version
     * @return
     */
    CustomButton getByButtonId(Long moduleId, Long buttonId, Integer version);

    /**
     * 获取自定义按钮配置
     *
     * @param moduleId
     * @param version
     * @return
     */
    List<CustomButtonSaveBO> queryList(Long moduleId, Integer version);

    /**
     * 执行按钮
     *
     * @param requestBO
     */
    FlowExamineRecord execute(ExecuteButtonRequestBO requestBO);

    /**
     * 自定义按钮填写字段值
     *
     * @param saveBO
     */
    void saveFieldData(CustomButtonFieldDataSaveBO saveBO);
}
