package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.module.common.EasyExcelParseUtil;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class ModuleDataServiceImpl implements IModuleDataService, ModulePageService {

    @Autowired
    private IModuleFieldService fieldService;

    @Autowired
    private IModuleService moduleService;

    /**
     * 查询列表数据
     *
     * @param searchBO 搜索条件
     * @param moduleId 模块ID
     * @return data
     */
    @Override
    public BasePage<Map<String, Object>> queryList(SearchBO searchBO, Long moduleId) {
        searchBO.setAuthFilter(true);
        BasePage<Map<String, Object>> result = queryPageList(searchBO, moduleId, true);
        return result;
    }

    @Override
    public BasePage<Map<String, Object>> queryByDataIds(ModuleDataQueryBO queryBO) {
        return queryByDataIds(queryBO.getDataId(), queryBO.getModuleId());
    }

    /**
     * map.put("module", normal); 当前模块
     * map.put("needFields", list); 导入或者导出表头的字段
     * map.put("allFields", fields); 所有字段
     * @param queryBO
     * @param sortIds
     * @return
     */
    @Override
    public Map<String, Object> getFields(FieldQueryBO queryBO, List<Long> sortIds) {
        ModuleEntity normal = moduleService.getNormal(queryBO.getModuleId());
        Long mainFieldId = normal.getMainFieldId();
        AtomicReference<ModuleFieldBO> mainField = new AtomicReference<>(new ModuleFieldBO());
        List<ModuleFieldBO> fields = fieldService.queryList(queryBO);
        List<ModuleFieldBO> excludeMainFields = fields
                .stream()
                .filter(f -> {
                    if (ObjectUtil.notEqual(f.getFieldId(), mainFieldId)) {
                        return true;
                    } else {
                        mainField.set(BeanUtil.copyProperties(f, ModuleFieldBO.class));
                        return false;
                    }
                }).collect(Collectors.toList());
        List<ModuleFieldBO> needFields;
        if (CollUtil.isNotEmpty(sortIds)) {
            needFields = excludeMainFields
                    .stream()
                    .filter(head -> CollUtil.contains(sortIds, head.getFieldId()))
                    .collect(Collectors.toList());
        } else {
            // 只包括大字段，不包括表格内字段
            needFields = excludeMainFields
                    .stream()
                    .filter(f ->  ObjectUtil.equal(0, f.getIsHidden()))
                    .filter(f ->  ObjectUtil.equal(1, f.getFieldType()))
                    .filter(f ->  ObjectUtil.isNull(f.getGroupId()) || ObjectUtil.equal(f.getType(), ModuleFieldEnum.DETAIL_TABLE.getType()))
                    .collect(Collectors.toList());
        }
        List<ModuleFieldBO> list  = new ArrayList<>();
        list.add(mainField.get());
        list.addAll(needFields);
        Map<String, Object> map = new HashMap<>(4);
        map.put("module", normal);
        map.put("needFields", list);
        map.put("allFields", fields);
        return map;
    }

    /**
     * 导出数据
     * 主字段必导出，放在第一位
     *
     * @param response 返回
     * @param queryBO 查询字段的参数包含moduleId，version，categoryId
     * @param search 查询条件
     * @param sortIds 字段
     * @param isXls 导出文件格式
     */
    @Override
    public void exportExcel(HttpServletResponse response, FieldQueryBO queryBO, SearchBO search, List<Long> sortIds, Integer isXls) {
        Map<String, Object> map = this.getFields(queryBO, sortIds);
        exportExcel(search, (List<ModuleFieldBO>)map.get("needFields"), (List<ModuleFieldBO>)map.get("allFields"), response, isXls, (record, headMap) -> {});
    }

    /**
     * 下载导入模板
     * 主字段必导出，放在第一位
     * @param queryBO 查找模块信息的参数
     * @param response response
     */
    @Override
    public void downloadExcel(FieldQueryBO queryBO, HttpServletResponse response) {
        Map<String, Object> map = this.getFields(queryBO, null);
        EasyExcelParseUtil.moduleImportExcel(
                ((ModuleEntity)map.get("module")).getName()
                , (List<ModuleFieldBO>)map.get("needFields")
                , (List<ModuleFieldBO>)map.get("allFields")
                , queryBO
                , response
        );
    }

    @Override
    public Long uploadExcel(MultipartFile file, UploadExcelBO uploadExcelBO) {
        FieldQueryBO queryBO = BeanUtil.copyProperties(uploadExcelBO, FieldQueryBO.class);
        Map<String, Object> fields = this.getFields(queryBO, null);
        uploadExcelBO.setParam(fields);
        Long msgId = ApplicationContextHolder.getBean(IModuleUploadExcelService.class).uploadExcel(file, uploadExcelBO);
        return msgId;
    }

    @Override
    public void revertImport(String batchId){
        ApplicationContextHolder.getBean(IModuleFieldDataCommonService.class).revertImport(batchId);
    }


}
