package com.kakarote.module.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.UserRoleSaveBO;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleMetadata;
import com.kakarote.module.entity.PO.ModuleRole;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zjj
 * @title: IModuleRoleService
 * @description: 角色 service
 * @date 2021/12/115:11
 */
public interface IModuleRoleService extends BaseService<ModuleRole> {

    /**
     * 查询所有角色
     *
     * @return
     */
    JSONArray queryAllRoles();

    /**
     * 权限查询
     *
     * @return
     */
    JSONArray getAuth();

    /**
     * 角色查询
     *
     * @param roleIds
     * @return
     */
    List<Map<String, Object>> listByRoleId(List<Long> roleIds);

    /**
     * 处理模块权限
     *
     * @param metadata
     * @param moduleGroupByAppId
     * @param authModuleIdMap
     * @param authCategoryIdMap
     * @return
     */
    JSONObject dealModuleAuth(ModuleMetadata metadata,
                              Map<Long, List<ModuleEntity>> moduleGroupByAppId,
                              Map<Long, Map<String, String>> authModuleIdMap,
                              Map<Long, Map<String, String>> authCategoryIdMap);

    /**
     * 查询应用下的所有角色
     *
     * @param applicationId
     * @return
     */
    JSONArray getRoleList(Long applicationId);

    /**
     * 保存角色
     *
     * @param role
     */
    void saveRole(ModuleRole role);

    /**
     * 删除角色
     *
     * @param roleId
     */
    void deleteRole(Long roleId);

    /**
     * 查看当前用户可查看指定模块数据的用户
     *
     * @param moduleId
     * @return
     */
    Set<Long> queryViewableUserIds(Long moduleId);

    /**
     * 保存用户的角色
     *
     * @param userRoleBO
     */
    void saveUserRole(UserRoleSaveBO userRoleBO);

    /**
     * 获取用户的角色
     *
     * @param userIds
     * @return
     */
    Map<Long, List<ModuleRole>> getByUserIds(List<Long> userIds);

    /**
     * 获取数据权限
     *
     * @param dataId
     * @return
     */
    Boolean getDataAuth(Long dataId);
}
