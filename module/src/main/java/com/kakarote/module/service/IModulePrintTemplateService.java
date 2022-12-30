package com.kakarote.module.service;

import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.ModulePrintRecord;
import com.kakarote.module.entity.PO.ModulePrintTemplate;
import com.kakarote.module.entity.VO.ModuleFieldVO;

import java.util.List;

/**
 * @author wwl
 * @date 2022/3/9 11:32
 */
public interface IModulePrintTemplateService extends BaseService<ModulePrintTemplate> {

    /**
     * 分页查询打印模板列表
     *
     * @param templateBO search
     * @return data
     */
    BasePage<ModulePrintTemplate> queryPrintTemplateList(Long moduleId, Integer version, PageEntity templateBO);

    /**
     * 打印
     *
     * @param templateId 模板id
     * @param dataId     数据id
     * @return data
     */
    String print(Long templateId, Long dataId);

    /**
     * 预览
     *
     * @param content 文件正文
     * @param type    类型（pdf或者word）
     * @return data
     */
    String preview(String content, String type);

    /**
     * 保存打印记录
     *
     * @param printRecord 记录实体类
     */
    void savePrintRecord(ModulePrintRecord printRecord);

    /**
     * 查询打印记录
     *
     * @param moduleId 模块id
     * @return data
     */
    List<ModulePrintRecord> queryPrintRecord(Long moduleId);

    /**
     * 查询单个打印记录
     *
     * @param recordId 记录id
     * @return data
     */
    ModulePrintRecord queryPrintRecordById(Long recordId);


    /**
     * 查询模块字段列表,与关联的模块的字段
     *
     * @param moduleId 模块id
     * @param version  版本号
     * @return list
     */
    List<ModuleFieldVO> getFields(Long moduleId, Integer version);

    /**
     * 查询
     *
     * @param moduleId 模块id
     * @param version  版本号
     * @return list
     */
    List<ModulePrintTemplate> getByModuleIdAndVersion(Long moduleId, Integer version);
}
