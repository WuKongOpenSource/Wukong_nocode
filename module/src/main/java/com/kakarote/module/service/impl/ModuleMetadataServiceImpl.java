package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.result.SystemCodeEnum;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.BaseUtil;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.service.UserService;
import com.kakarote.module.common.ElasticUtil;
import com.kakarote.module.constant.AppTypeEnum;
import com.kakarote.module.constant.ModuleCodeEnum;
import com.kakarote.module.entity.BO.ModuleMetadataBO;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.mapper.ModuleMetadataMapper;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * <p>
 * 应用表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
@Service
public class ModuleMetadataServiceImpl extends BaseServiceImpl<ModuleMetadataMapper, ModuleMetadata> implements IModuleMetadataService {

    @Autowired
    private IModuleService moduleService;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * 修改应用
     *
     * @param moduleMetadataBO bo
     */
    @Override
    public void update(ModuleMetadataBO moduleMetadataBO) {
        if (moduleMetadataBO.getApplicationId() == null) {
            return;
        }
        lambdaUpdate()
                .set(ModuleMetadata::getName, moduleMetadataBO.getName())
                .set(ModuleMetadata::getDescription, moduleMetadataBO.getDescription())
                .set(ModuleMetadata::getDetail, moduleMetadataBO.getDetail())
                .set(ModuleMetadata::getIcon, moduleMetadataBO.getIcon())
                .set(ModuleMetadata::getIsFeatured, moduleMetadataBO.getIsFeatured())
                .set(ModuleMetadata::getMainPicture, moduleMetadataBO.getMainPicture())
                .set(ModuleMetadata::getDetailPicture, moduleMetadataBO.getDetailPicture())
                .eq(ModuleMetadata::getApplicationId, moduleMetadataBO.getApplicationId())
                .update();
    }

    /**
     * 修改应用状态
     *
     * @param applicationId 应用ID
     * @param status        状态
     */
    @Override
    public void updateStatus(Long applicationId, Integer status) {
        if (!Objects.equals(1, status) && !Objects.equals(2, status)) {
            throw new BusinessException(SystemCodeEnum.SYSTEM_NO_VALID);
        }
        ModuleMetadata moduleMetadata = getById(applicationId);
        if (moduleMetadata != null) {
            if (!Objects.equals(moduleMetadata.getStatus(), status)) {
                lambdaUpdate()
                        .set(ModuleMetadata::getStatus,status)
                        .eq(ModuleMetadata::getApplicationId,applicationId)
                        .update();
            }
        }
    }

    /**
     * @param applicationId 应用ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long applicationId) {
        ModuleMetadata moduleMetadata = getById(applicationId);
        if (moduleMetadata == null) {
            return;
        }
        LambdaQueryWrapper<ModuleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(ModuleEntity::getModuleId);
        wrapper.eq(ModuleEntity::getApplicationId, applicationId);
        List<Long> moduleIdList = moduleService.list(wrapper).stream().map(ModuleEntity::getModuleId).collect(Collectors.toList());
        // 删除应用下模块
        moduleService.deleteModule(moduleIdList);
        // 删除应用下分组
        ApplicationContextHolder.getBean(IModuleGroupService.class).deleteByApplicationId(applicationId);
        // 删除应用的角色
        ApplicationContextHolder.getBean(IModuleRoleService.class).lambdaUpdate().eq(ModuleRole::getApplicationId, applicationId).remove();
        ApplicationContextHolder.getBean(IModuleRoleUserService.class).lambdaUpdate().eq(ModuleRoleUser::getApplicationId, applicationId).remove();
        // 删除应用
        removeById(applicationId);
    }

    @Override
    public Map<String, Object> queryAppConfig(Long applicationId) {
        ModuleMetadata moduleMetadata = getById(applicationId);
        List<ModuleGroup> groups = ApplicationContextHolder.getBean(IModuleGroupService.class).getByApplicationId(applicationId);
        List<ModuleGroupSort> groupSorts = ApplicationContextHolder.getBean(IModuleGroupSortService.class).getByApplicationId(applicationId);
        List<ModuleEntity> modules = moduleService.lambdaQuery()
                .eq(ModuleEntity::getApplicationId, applicationId)
                .eq(ModuleEntity::getIsActive, true)
                .eq(ModuleEntity::getStatus, 1)
                .orderByDesc(ModuleEntity::getCreateTime)
                .list();
        Map<String, Object> data = new HashMap<>(16);
        List<Future<Map<String, Object>>> futureList = new ArrayList<>();
        for (ModuleEntity module : modules) {
            Future<Map<String, Object>> future = taskExecutor.submit(() -> {
                Map<String, Object> moduleConfig = moduleService.queryModuleConfig(module.getModuleId());
                return moduleConfig;
            });
            futureList.add(future);
        }
        List<Map<String, Object>> moduleDataList = futureList.stream().map(f -> {
            try {
                return f.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        data.put("app", moduleMetadata);
        data.put("groups", groups);
        data.put("groupSorts", groupSorts);
        data.put("modules", moduleDataList);
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModuleMetadata importApp(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        BufferedReader reader = IoUtil.getUtf8Reader(inputStream);
        StringBuffer content = new StringBuffer();
        try {
            String jsonStr;
            while ((jsonStr = reader.readLine()) != null) {
                content = content.append(jsonStr);
            }
            String str = StrUtil.str(Base64Utils.decodeFromString(content.toString()), StandardCharsets.UTF_8);
            Map<String, Object> data = JSON.parseObject(str, Map.class);
            return this.applyApp(data, AppTypeEnum.IMPORTED);
        } catch (Exception e) {
            throw new BusinessException(ModuleCodeEnum.IMPORT_APP_PARSE_ERROR);
        } finally {
            reader.close();
            inputStream.close();
        }
    }

    @Override
    public ModuleMetadata applyApp(Map<String, Object> data, AppTypeEnum typeEnum) {
        // region 保存app
        ModuleMetadata app = MapUtil.get(data, "app", ModuleMetadata.class);
        if (ObjectUtil.equal(AppTypeEnum.INSTALLED, typeEnum)) {
            app.setSourceId(app.getApplicationId());
        }
        app.setApplicationId(BaseUtil.getNextId());
        app.setIsFeatured(false);
        app.setCreateTime(DateUtil.date());
        app.setType(typeEnum.getCode());
        ApplicationContextHolder.getBean(IModuleMetadataService.class).save(app);
        List<ModuleGroup> groups = JSON.parseArray(MapUtil.getStr(data, "groups"), ModuleGroup.class);
        // 新旧模块ID对应关系 旧-新
        Map<Long, Long> oldNewGroupIdMap = new HashMap<>(16);
        if (CollUtil.isNotEmpty(groups)) {
            for (ModuleGroup group : groups) {
                Long newGroupId = BaseUtil.getNextId();
                oldNewGroupIdMap.put(group.getId(), newGroupId);
                group.setId(newGroupId);
                group.setApplicationId(app.getApplicationId());
                group.setCreateTime(LocalDateTimeUtil.now());
                group.setUpdateTime(LocalDateTimeUtil.now());
                group.setCreateUserId(UserUtil.getUserId());
                group.setUpdateUserId(UserUtil.getUserId());
            }
            ApplicationContextHolder.getBean(IModuleGroupService.class).saveBatch(groups);
        }
        // endregion
        List<Map<String, Object>> moduleDataList = MapUtil.get(data, "modules", List.class);
        // 新旧模块ID对应关系 旧-新
        Map<Long, Long> oldNewModuleIdMap = new HashMap<>(16);
        // region 保存模块
        for (Map<String, Object> moduleData : moduleDataList) {
            ModuleEntity module = MapUtil.get(moduleData, "module", ModuleEntity.class);
            Long newModuleId = BaseUtil.getNextId();
            oldNewModuleIdMap.put(module.getModuleId(), newModuleId);
            module.setId(null);
            module.setIndexName(ElasticUtil.getIndexName(newModuleId));
            module.setModuleId(newModuleId);
            module.setVersion(0);
            module.setApplicationId(app.getApplicationId());
            module.setManageUserId(JSON.toJSONString(Arrays.asList(ApplicationContextHolder.getBean(UserService.class).querySuperUserId().getData())));
            module.setCreateTime(DateUtil.date());
            module.setUpdateTime(DateUtil.date());
            module.setCreateUserId(UserUtil.getUserId());
            ApplicationContextHolder.getBean(IModuleService.class).save(module);
            ApplicationContextHolder.getBean(IModuleStatusService.class).updateStatus(newModuleId, true);
            // 初始化 ES
            moduleService.initES(module);
            // 自定义字段 ES索引处理
            List<ModuleField> fields = JSON.parseArray(MapUtil.getStr(moduleData, "fields"), ModuleField.class);
            moduleService.addESFields(fields, newModuleId);
        }
        List<ModuleGroupSort> groupSorts = JSON.parseArray(MapUtil.getStr(data, "groupSorts"), ModuleGroupSort.class);
        if (CollUtil.isNotEmpty(groupSorts)) {
            List<ModuleGroupSort> moduleGroupSorts = new ArrayList<>();
            for (ModuleGroupSort groupSort : groupSorts) {
                groupSort.setId(null);
                groupSort.setApplicationId(app.getApplicationId());
                groupSort.setCreateTime(LocalDateTimeUtil.now());
                groupSort.setUpdateTime(LocalDateTimeUtil.now());
                groupSort.setCreateUserId(UserUtil.getUserId());
                if (ObjectUtil.isNotNull(groupSort.getGroupId())) {
                    groupSort.setGroupId(oldNewGroupIdMap.get(groupSort.getGroupId()));
                }
                if (ObjectUtil.isNotNull(groupSort.getModuleId())) {
                    // 模块未发布，则跳过
                    Long newModuleId = oldNewModuleIdMap.get(groupSort.getModuleId());
                    if (ObjectUtil.isNull(newModuleId)) {
                        continue;
                    }
                    groupSort.setModuleId(newModuleId);
                }
                moduleGroupSorts.add(groupSort);
            }
            ApplicationContextHolder.getBean(IModuleGroupSortService.class).saveBatch(moduleGroupSorts);
        }
        // endregion
        Long userId = UserUtil.getUserId();
        for (Map<String, Object> moduleData : moduleDataList) {
            taskExecutor.submit(() -> {
                try {
                    UserUtil.setUser(userId);
                    moduleService.applyModule(moduleData, oldNewModuleIdMap);
                } finally {
                    UserUtil.removeUser();
                }
            });
        }
        return app;
    }

    @Override
    public List<ModuleMetadata> getCustomAppList() {
        List<ModuleMetadata> list = lambdaQuery().list();
        return list;
    }
}
