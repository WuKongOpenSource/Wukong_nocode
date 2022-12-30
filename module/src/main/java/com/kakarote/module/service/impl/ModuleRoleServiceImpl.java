package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.service.DeptService;
import com.kakarote.ids.provider.service.UserService;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleFieldDataCommon;
import com.kakarote.module.constant.ModuleType;
import com.kakarote.module.entity.BO.UserRoleSaveBO;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.mapper.ModuleRoleMapper;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: ModuleRoleServiceImpl
 * @description: 角色服务
 * @date 2021/12/115:12
 */
@Service
public class ModuleRoleServiceImpl extends BaseServiceImpl<ModuleRoleMapper, ModuleRole> implements IModuleRoleService {


    @Autowired
    private IModuleService moduleService;

    @Autowired
    private IModuleRoleModuleService roleModuleService;

    @Autowired
    private IModuleRoleUserService roleUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private IModuleMetadataService metadataService;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public JSONArray queryAllRoles() {
        JSONArray result = new JSONArray();
        List<ModuleMetadata> metadataList = metadataService.lambdaQuery().orderByDesc(ModuleMetadata::getCreateTime).list();
        if (CollUtil.isEmpty(metadataList)) {
            return result;
        }
        List<ModuleRole> allRoles = lambdaQuery().list();
        Map<Long, List<ModuleRole>> roleGroupByAppId = allRoles.stream().collect(Collectors.groupingBy(ModuleRole::getApplicationId));
        for (ModuleMetadata metadata : metadataList) {
            JSONObject o = new JSONObject();
            o.put("applicationId", metadata.getApplicationId());
            o.put("name", metadata.getName());
            List<ModuleRole> roles = roleGroupByAppId.get(metadata.getApplicationId());
            if (CollUtil.isNotEmpty(roles)) {
                o.put("roles", roles);
            } else {
                o.put("roles", Collections.emptyList());
            }
            result.add(o);
        }
        return result;
    }

    @Override
    public JSONArray getAuth() {
        JSONArray result = new JSONArray();
        List<ModuleMetadata> metadataList = metadataService.lambdaQuery()
                .orderByDesc(ModuleMetadata::getCreateTime).list();
        if (CollUtil.isEmpty(metadataList)) {
            return result;
        }
        List<ModuleEntity> allModules = moduleService.getAll().stream()
                .filter(m -> ObjectUtil.equal(ModuleType.MODULE.getType(), m.getModuleType()))
                .collect(Collectors.toList());
        Map<Long, List<ModuleEntity>> moduleGroupByAppId = allModules.stream().collect(Collectors.groupingBy(ModuleEntity::getApplicationId));
        List<Map<String, String>> userAuth = roleUserService.getUserAuth(UserUtil.getUserId());
        userAuth = userAuth.stream().filter(m -> ObjectUtil.isNotNull(MapUtil.getLong(m, "moduleId"))).collect(Collectors.toList());
        Map<Long, Map<String, String>> authModuleIdMap = new HashMap<>(16);
        Map<Long, Map<String, String>> authCategoryIdMap = new HashMap<>(16);
        for (Map<String, String> map : userAuth) {
            Long categoryId = MapUtil.getLong(map, "categoryId");
            if (ObjectUtil.isNull(categoryId)) {
                Long moduleId = MapUtil.getLong(map, "moduleId");
                Map<String, String> authMap = authModuleIdMap.get(moduleId);
                if (ObjectUtil.isNull(authMap)) {
                    authModuleIdMap.put(moduleId, map);
                } else {
                    String authStr1 = MapUtil.getStr(authMap, "auth");
                    String authStr2 = MapUtil.getStr(map, "auth");
                    List<String> authList = StrUtil.split(authStr1, ",");
                    authList.addAll(StrUtil.split(authStr2, ","));
                    CollUtil.distinct(authList);
                    Collections.sort(authList);
                    String authStr = CollUtil.join(authList, ",");
                    authMap.put("auth", authStr);
                    authModuleIdMap.put(moduleId, authMap);
                }
            } else {
                Map<String, String> authMap = authCategoryIdMap.get(categoryId);
                if (ObjectUtil.isNull(authMap)) {
                    authCategoryIdMap.put(categoryId, map);
                } else {
                    String authStr1 = MapUtil.getStr(authMap, "auth");
                    String authStr2 = MapUtil.getStr(map, "auth");
                    List<String> authList = StrUtil.split(authStr1, ",");
                    authList.addAll(StrUtil.split(authStr2, ","));
                    CollUtil.distinct(authList);
                    Collections.sort(authList);
                    String authStr = CollUtil.join(authList, ",");
                    authMap.put("auth", authStr);
                    authCategoryIdMap.put(categoryId, authMap);
                }
            }
        }

        List<Future<JSONObject>> futureList = new ArrayList<>();
        Long userId = UserUtil.getUserId();
        for (ModuleMetadata metadata : metadataList) {
            Future<JSONObject> future = taskExecutor.submit(() -> {
                try {
                    UserUtil.setUser(userId);
                    JSONObject jsonObject = dealModuleAuth(metadata, moduleGroupByAppId, authModuleIdMap, authCategoryIdMap);
                    return jsonObject;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    UserUtil.removeUser();
                }
            });
            futureList.add(future);
        }
        List<JSONObject> moduleDataList = futureList.stream().map(f -> {
            try {
                JSONObject jsonObject = f.get();
                return jsonObject;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).filter(o -> ObjectUtil.isNotNull(o)).collect(Collectors.toList());
        result.addAll(moduleDataList);
        return result;
    }

    /**
     * 处理模块权限
     *
     * @param metadata
     * @param moduleGroupByAppId
     * @param authModuleIdMap
     * @param authCategoryIdMap
     * @return
     */
    public JSONObject dealModuleAuth(ModuleMetadata metadata,
                                     Map<Long, List<ModuleEntity>> moduleGroupByAppId,
                                     Map<Long, Map<String, String>> authModuleIdMap,
                                     Map<Long, Map<String, String>> authCategoryIdMap) {
        JSONObject app = new JSONObject();
        app.fluentPut("applicationId", metadata.getApplicationId()).fluentPut("name", metadata.getName());
        List<ModuleEntity> moduleList = moduleGroupByAppId.get(metadata.getApplicationId());
        if (CollUtil.isNotEmpty(moduleList)) {
            JSONArray modules = new JSONArray();
            for (ModuleEntity module : moduleList) {
                // 模块的权限
                String auth;
                // 管理员返回全部权限
                if (UserUtil.isAdmin()) {
                    auth = "1,2,3,4,5,6,7,8,9,10,11";
                } else {
                    Map<String, String> authMap = authModuleIdMap.get(module.getModuleId());
                    auth = MapUtil.getStr(authMap, "auth");
                }
                if (StrUtil.isNotEmpty(auth)) {
                    JSONObject m = new JSONObject();
                    m.fluentPut("moduleId", module.getModuleId()).fluentPut("name", module.getName());
                    m.put("auth", auth);
                    modules.add(m);
                }

                // 分类的权限
                List<CustomCategory> categories = ApplicationContextHolder.getBean(ICustomCategoryService.class).getByModuleIdAndVersion(module.getModuleId(), module.getVersion());
                for (CustomCategory category : categories) {
                    if (ObjectUtil.equal(0, category.getType())) {
                        continue;
                    }
                    String categoryAuth;
                    // 管理员返回全部权限
                    if (UserUtil.isAdmin()) {
                        categoryAuth = "1,2,3,4,5,6,7,8,9,10,11";
                    } else {
                        Map<String, String> authMap = authCategoryIdMap.get(category.getCategoryId());
                        categoryAuth = MapUtil.getStr(authMap, "auth");
                    }
                    if (StrUtil.isEmpty(categoryAuth)) {
                        continue;
                    }
                    JSONObject c = new JSONObject();
                    c.fluentPut("moduleId", module.getModuleId()).fluentPut("name", module.getName())
                            .fluentPut("categoryId", category.getCategoryId())
                            .fluentPut("categoryName", category.getCategoryName());
                    c.put("auth", categoryAuth);
                    modules.add(c);
                }
            }
            if (CollUtil.isNotEmpty(modules)) {
                app.put("modules", modules);
            }
        }
        if (ObjectUtil.isNull(app.getJSONArray("modules"))) {
            return null;
        }
        return app;
    }

    @Override
    public List<Map<String, Object>> listByRoleId(List<Long> roleIds) {
        List<Map<String, Object>> roleList = userService.listByRoleId(roleIds).getData();
        List<ModuleRole> moduleRoles = lambdaQuery().select(ModuleRole::getRoleId, ModuleRole::getRoleName, ModuleRole::getApplicationId).in(ModuleRole::getRoleId, roleIds).list();
        List<Map<String, Object>> roleMaps = moduleRoles.stream().map(r -> BeanUtil.beanToMap(r)).collect(Collectors.toList());
        roleList.addAll(roleMaps);
        return roleList;
    }

    @Override
    public JSONArray getRoleList(Long applicationId) {
        List<ModuleRole> allRoles = lambdaQuery()
                .eq(ModuleRole::getApplicationId, applicationId)
                .list();
        if (CollUtil.isEmpty(allRoles)) {
            return null;
        }
        List<Long> roleIds = allRoles.stream().map(ModuleRole::getRoleId).collect(Collectors.toList());
        List<ModuleRoleModule> roleModules = roleModuleService.lambdaQuery()
                .in(ModuleRoleModule::getRoleId, roleIds)
                .list();
        Map<Long, List<ModuleRoleModule>> roleModuleGroupByRoleId = roleModules.stream().collect(Collectors.groupingBy(ModuleRoleModule::getRoleId));
        List<ModuleRoleUser> roleUsers = roleUserService.lambdaQuery()
                .in(ModuleRoleUser::getRoleId, roleIds)
                .list();

        Map<Long,List<ModuleRoleUser>> roleUserGroupByRoleId = roleUsers.stream().collect(Collectors.groupingBy(ModuleRoleUser::getRoleId));

        List<UserInfo> allUsers = userService.queryUserInfoList().getData();
        JSONArray roles = new JSONArray();
        for (ModuleRole role : allRoles) {
            JSONObject o = JSONObject.parseObject(JSON.toJSONString(role));
            List<ModuleRoleModule> roleModuleList = roleModuleGroupByRoleId.get(role.getRoleId());
            if (CollUtil.isNotEmpty(roleModuleList)) {
                o.put("roleModuleList", roleModuleList);
            } else {
                o.put("roleModuleList", Collections.emptyList());
            }
            List<ModuleRoleUser> roleUserList = roleUserGroupByRoleId.get(role.getRoleId());
            if (CollUtil.isNotEmpty(roleUserList)) {
                List<Long> userIds = roleUserList.stream().map(ModuleRoleUser::getUserId).collect(Collectors.toList());
                List<UserInfo> users = allUsers.stream().filter(u -> userIds.contains(u.getUserId())).collect(Collectors.toList());
                o.put("roleUserList", users);
            } else {
                o.put("roleUserList", Collections.emptyList());
            }
            roles.add(o);
        }
        return roles;
    }

    @Override
    public void saveRole(ModuleRole role) {
        if(ObjectUtil.isNull(role.getApplicationId()) || ObjectUtil.isNull(role.getRoleName())) {
            return;
        }
        if (ObjectUtil.isNull(role.getRoleId())) {
            role.setCreateTime(DateUtil.date());
            role.setCreateUserId(UserUtil.getUserId());
            save(role);
        } else {
            ModuleRole sourceRole = getById(role.getRoleId());
            sourceRole.setRoleName(role.getRoleName());
            sourceRole.setRangeType(role.getRangeType());
            sourceRole.setIsActive(role.getIsActive());
            updateById(sourceRole);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        removeById(roleId);
        roleUserService.lambdaUpdate().eq(ModuleRoleUser::getRoleId, roleId)
                .remove();
        roleModuleService.lambdaUpdate().eq(ModuleRoleModule::getRoleId, roleId).remove();
    }

    @Override
    public Set<Long> queryViewableUserIds(Long moduleId) {
        Set<Long> userIds = new HashSet<>();
        ModuleEntity module = moduleService.getNormal(moduleId);
        if (ObjectUtil.isNull(module)) {
            return userIds;
        }
        // admin 可查看所有用户
        if (UserUtil.isAdmin()) {
            List<UserInfo> allUsers = userService.queryUserInfoList().getData();
            return allUsers.stream().map(UserInfo::getUserId).collect(Collectors.toSet());
        }
        ModuleRole moduleRole = getBaseMapper().getByUserIdAndModuleId(UserUtil.getUserId(), moduleId);
        if (ObjectUtil.isNull(moduleRole)) {
            // 模块的创建人可查看自己的数据
            if (ObjectUtil.equal(module.getCreateUserId(), UserUtil.getUserId())) {
                userIds.add(UserUtil.getUserId());
            }
        } else {
            userIds.add(UserUtil.getUserId());
            // 1-本人 2-本人及下属 3-本部门 4-本部门及下属部门 5-全部
           Integer rangeType = moduleRole.getRangeType();
           switch (rangeType) {
               case 1:{
                   return userIds;
               }
               case 2:{
                   List<Long> ids = userService.queryChildUserId(UserUtil.getUserId()).getData();
                   userIds.addAll(ids);
                   break;
               }
               case 3:{
                   List<Long> ids = userService.queryUserByDeptIds(Collections.singletonList(UserUtil.getUser().getDeptId())).getData();
                   userIds.addAll(ids);
                   break;
               }
               case 4:{
                   List<Long> deptIds = deptService.queryChildDeptId(UserUtil.getUser().getDeptId()).getData();
                   deptIds.add(UserUtil.getUser().getDeptId());
                   List<Long> ids = userService.queryUserByDeptIds(deptIds).getData();
                   userIds.addAll(ids);
                   break;
               }
               case 5:{
                   List<UserInfo> allUsers = userService.queryUserInfoList().getData();
                   userIds.addAll(allUsers.stream().map(UserInfo::getUserId).collect(Collectors.toSet()));
                   break;
               }
               default:break;
           }
        }
        return userIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUserRole(UserRoleSaveBO userRoleBO) {
        if (ObjectUtil.isNull(userRoleBO) || CollUtil.isEmpty(userRoleBO.getRoles())) {
            return;
        }
        Set<Long> allUserIds = new HashSet<>();
        // 获取部门的所有员工
        if (CollUtil.isNotEmpty(userRoleBO.getDeptIds())) {
            List<Long> userIds = userService.queryUserByDeptIds(userRoleBO.getDeptIds()).getData();
            allUserIds.addAll(userIds);
        }
        if (CollUtil.isNotEmpty(userRoleBO.getUserIds())) {
            allUserIds.addAll(userRoleBO.getUserIds());
        }
        if (CollUtil.isNotEmpty(allUserIds)) {
            // 先删除用户的角色
            roleUserService.lambdaUpdate()
                    .in(ModuleRoleUser::getUserId, allUserIds)
                    .remove();
            List<ModuleRoleUser> roleUsers = new ArrayList<>();
            for (Long userId : allUserIds) {
                for (UserRoleSaveBO.ApplicationRoleBO role : userRoleBO.getRoles()) {
                    for (Long roleId : role.getRoleIds()) {
                        ModuleRoleUser roleUser = new ModuleRoleUser();
                        roleUser.setUserId(userId);
                        roleUser.setRoleId(roleId);
                        roleUser.setApplicationId(role.getApplicationId());
                        roleUsers.add(roleUser);
                    }
                }
            }
            roleUserService.saveBatch(roleUsers);
        }
    }

    @Override
    public Map<Long, List<ModuleRole>> getByUserIds(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return null;
        }
        List<ModuleRoleUser> roleUsers = roleUserService.lambdaQuery().in(ModuleRoleUser::getUserId, userIds).list();
        if (CollUtil.isEmpty(roleUsers)) {
            return null;
        }
        List<Long> roleIds = roleUsers.stream().map(ModuleRoleUser::getRoleId).collect(Collectors.toList());
        List<ModuleRole> roles = lambdaQuery().in(ModuleRole::getRoleId, roleIds).list();
        Map<Long, ModuleRole> roleIdEntityMap = roles.stream().collect(Collectors.toMap(ModuleRole::getRoleId, Function.identity()));
        Map<Long, List<ModuleRole>> result = roleUsers.stream().collect(Collectors.groupingBy(ModuleRoleUser::getUserId, Collectors.mapping(m -> roleIdEntityMap.get(m.getRoleId()), Collectors.toList())));
        return result;
    }

    @Override
    public Boolean getDataAuth(Long dataId) {
        if (UserUtil.isAdmin()) {
            return true;
        }
        ModuleFieldDataCommon dataCommon = ApplicationContextHolder.getBean(IModuleFieldDataCommonService.class).getByDataId(dataId);
        Long moduleId = dataCommon.getModuleId();
        ModuleRole moduleRole = getBaseMapper().getByUserIdAndModuleId(UserUtil.getUserId(), moduleId);
        if (ObjectUtil.isNull(moduleRole)) {
            return false;
        }
        Long ownerUserId = dataCommon.getOwnerUserId();
        // 1-本人 2-本人及下属 3-本部门 4-本部门及下属部门 5-全部
        Integer rangeType = moduleRole.getRangeType();
        List<Long> userIds = new ArrayList<>();
        switch (rangeType) {
            case 1: {
                userIds.add(UserUtil.getUserId());
                break;
            }
            case 2: {
                List<Long> ids = userService.queryChildUserId(UserUtil.getUserId()).getData();
                userIds.addAll(ids);
                break;
            }
            case 3: {
                List<Long> ids = userService.queryUserByDeptIds(Collections.singletonList(UserUtil.getUser().getDeptId())).getData();
                userIds.addAll(ids);
                break;
            }
            case 4: {
                List<Long> deptIds = deptService.queryChildDeptId(UserUtil.getUser().getDeptId()).getData();
                deptIds.add(UserUtil.getUser().getDeptId());
                List<Long> ids = userService.queryUserByDeptIds(deptIds).getData();
                userIds.addAll(ids);
                break;
            }
            case 5: {
                List<UserInfo> allUsers = userService.queryUserInfoList().getData();
                userIds.addAll(allUsers.stream().map(UserInfo::getUserId).collect(Collectors.toSet()));
                break;
            }
            default:
                break;
        }
        return CollUtil.contains(userIds, ownerUserId);
    }
}
