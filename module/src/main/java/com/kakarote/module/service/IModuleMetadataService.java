package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.constant.AppTypeEnum;
import com.kakarote.module.entity.BO.ModuleMetadataBO;
import com.kakarote.module.entity.PO.ModuleMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 应用表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
public interface IModuleMetadataService extends BaseService<ModuleMetadata> {

    /**
     * 修改应用
     *
     * @param moduleMetadataBO data
     */
    void update(ModuleMetadataBO moduleMetadataBO);

    /**
     * 修改应用状态
     * @param applicationId 应用ID
     * @param status 状态
     */
    void updateStatus(Long applicationId, Integer status);

    /**
     * 删除应用
     *
     * @param applicationId 应用ID
     */
    void delete(Long applicationId);

    /**
     * 导入应用
     *
     * @param file
     * @return
     * @throws IOException
     */
    ModuleMetadata importApp(MultipartFile file) throws IOException;

    /**
     * 应用数据到当前企业
     *
     * @param data
     * @param typeEnum
     * @return
     */
    ModuleMetadata applyApp(Map<String, Object> data, AppTypeEnum typeEnum);

    /**
     * 获取用户自定义应用列表
     *
     * @return
     */
    List<ModuleMetadata> getCustomAppList();
}
