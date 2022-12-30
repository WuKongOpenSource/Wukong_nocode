package com.kakarote.module.service;

import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.RoleFieldRequestBO;
import com.kakarote.module.entity.BO.RoleFieldSaveBO;
import com.kakarote.module.entity.PO.ModuleFieldData;
import com.kakarote.module.entity.PO.ModuleRoleField;

import java.util.List;
import java.util.Map;

/**
 * @author zjj
 * @title: IModuleRoleFieldService
 * @description: 角色字段关系表
 * @date 2021/12/410:13
 */
public interface IModuleRoleFieldService extends BaseService<ModuleRoleField> {

    JSONObject getAuth(RoleFieldRequestBO requestBO);

    void saveRoleField(RoleFieldSaveBO saveBO);

    void replaceMaskFieldValue(List<? extends Map<String, Object>> list, Long moduleId);

    /**
     * 字段掩码处理-详情页专用
     *
     * @param fieldDataList
     * @param moduleId
     */
    void replaceMaskFieldValue2(List<ModuleFieldData> fieldDataList, Long moduleId);


    Object parseValue(Integer type, Object value);

}
