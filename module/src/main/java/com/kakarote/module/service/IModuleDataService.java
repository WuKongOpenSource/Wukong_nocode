package com.kakarote.module.service;

import com.kakarote.common.result.BasePage;
import com.kakarote.module.entity.BO.FieldQueryBO;
import com.kakarote.module.entity.BO.ModuleDataQueryBO;
import com.kakarote.module.entity.BO.SearchBO;
import com.kakarote.module.entity.BO.UploadExcelBO;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface IModuleDataService {

    /**
     * 查询列表数据
     * @param searchBO 搜索条件
     * @param moduleId 模块ID
     * @return data
     */
    BasePage<Map<String, Object>> queryList(SearchBO searchBO, Long moduleId);

    /**
     * 查询指定数据列表数据
     *
     * @param queryBO 查询模块数据 BO
     * @return  data
     */
    BasePage<Map<String, Object>> queryByDataIds(ModuleDataQueryBO queryBO);

    Map<String, Object> getFields( FieldQueryBO queryBO, @Nullable List<Long> sortIds);

    /**
     * 导出
     *
     * @param response 返回
     * @param queryBO 查询字段的参数包含moduleId，version，categoryId
     * @param search 查询条件
     * @param sortIds 字段
     * @param isXls 导出文件格式
     */
    void exportExcel(HttpServletResponse response, FieldQueryBO queryBO, SearchBO search, List<Long> sortIds, Integer isXls);

    /**
     * 下载导入模板
     *
     * @param queryBO 查找模块信息的参数
     * @param response response
     */
    void downloadExcel(FieldQueryBO queryBO, HttpServletResponse response);

    Long uploadExcel(MultipartFile file, UploadExcelBO uploadExcelBO);

    /**
     * 根据批次id 删除导入的数据
     *
     * @param batchId batchId
     */
    void revertImport(String batchId);
}
