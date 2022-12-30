package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.RoleUserSaveBO;
import com.kakarote.module.entity.PO.ModuleRoleUser;

import java.util.List;
import java.util.Map;

/**
 * @author zjj
 * @title: IModuleRoleUserService
 * @description: 角色用户关系
 * @date 2021/12/213:09
 */
public interface IModuleRoleUserService extends BaseService<ModuleRoleUser> {

    /**
     * 保存角色用户关系
     *
     * @param saveBO
     */
    void saveRoleUser(RoleUserSaveBO saveBO);

    /**
     * 获取用户权限
     *
     * @param userId
     * @return
     */
    List<Map<String, String>> getUserAuth(Long userId);


    /**
     *  获取用户的字段权限
     *
     * @param userId
     * @param moduleId
     * @return
     */
    List<Map<String, String>> getUserFieldAuth(Long userId, Long moduleId);

    /**
     * 获取用户的角色
     *
     * @param userId
     * @return
     */
    List<ModuleRoleUser> getByUserId(Long userId);

    /**
     * 获取指定角色的用户
     *
     * @param roleId
     * @return
     */
    List<ModuleRoleUser> getByRoleId(Long roleId);
}
