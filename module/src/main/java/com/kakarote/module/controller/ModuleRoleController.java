package com.kakarote.module.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.ModuleRole;
import com.kakarote.module.service.IModuleRoleFieldService;
import com.kakarote.module.service.IModuleRoleModuleService;
import com.kakarote.module.service.IModuleRoleService;
import com.kakarote.module.service.IModuleRoleUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zjj
 * @title: ModuleRoleController
 * @description: 角色
 * @date 2021/12/115:32
 */
@RestController
@RequestMapping("/moduleRole")
@Api(tags = "角色")
public class ModuleRoleController {

    @Autowired
    private IModuleRoleService roleService;

    @Autowired
    private IModuleRoleModuleService roleModuleService;

    @Autowired
    private IModuleRoleUserService roleUserService;

    @Autowired
    private IModuleRoleFieldService roleFieldService;

    @PostMapping("/roles")
    @ApiOperation("查询所有角色")
    public Result<JSONArray> queryAllRoles() {
        JSONArray result = roleService.queryAllRoles();
        return Result.ok(result);
    }

    @PostMapping("/list/{applicationId}")
    @ApiOperation("查询应用下的所有角色")
    public Result<JSONArray> list(@PathVariable("applicationId") Long applicationId) {
        JSONArray result = roleService.getRoleList(applicationId);
        return Result.ok(result);
    }


    @PostMapping("/listByRoleId")
    @ApiOperation("角色查询")
    public Result<List<Map<String, Object>>> listByRoleId(@RequestBody List<Long> roleIds) {
        List<Map<String, Object>> result = roleService.listByRoleId(roleIds);
        return Result.ok(result);
    }

    @PostMapping("/auth")
    @ApiOperation("权限查询")
    public Result<JSONArray> getAuth() {
        JSONArray result = roleService.getAuth();
        return Result.ok(result);
    }

    @PostMapping("/fieldAuth/{moduleId}/{userId}")
    @ApiOperation("获取用户的字段权限")
    public Result<List<Map<String, String>>> getUserFieldAuth(@PathVariable("userId") Long userId, @PathVariable("moduleId") Long moduleId) {
        List<Map<String, String>> result = roleUserService.getUserFieldAuth(userId, moduleId);
        return Result.ok(result);
    }

    @PostMapping("/queryViewableUserIds/{moduleId}")
    @ApiOperation("查看当前用户可查看指定模块数据的用户")
    public Result<Set<Long>> queryViewableUserIds(@PathVariable("moduleId") Long moduleId) {
        Set<Long> userIds = roleService.queryViewableUserIds(moduleId);
        return Result.ok(userIds);
    }

    @PostMapping("/save")
    @ApiOperation("保存角色")
    public Result saveRole(@RequestBody ModuleRole role) {
        roleService.saveRole(role);
        return Result.ok();
    }

    @PostMapping("/module/save")
    @ApiOperation("保存角色模块关系")
    public Result saveRole(@RequestBody RoleModuleSaveBO saveBO) {
        roleModuleService.saveRoleModule(saveBO);
        return Result.ok();
    }

    @PostMapping("/user/save")
    @ApiOperation("保存角色用户关系")
    public Result saveRole(@RequestBody RoleUserSaveBO saveBO) {
        roleUserService.saveRoleUser(saveBO);
        return Result.ok();
    }

    @PostMapping("/delete/{roleId}")
    @ApiOperation("删除角色")
    public Result deleteRole(@PathVariable("roleId") Long roleId) {
        roleService.deleteRole(roleId);
        return Result.ok();
    }

    @PostMapping("/userRole/save")
    @ApiOperation("保存用户的角色")
    public Result saveUserRole(@RequestBody UserRoleSaveBO saveBO) {
        roleService.saveUserRole(saveBO);
        return Result.ok();
    }

    @PostMapping("/roleField")
    @ApiOperation("字段权限查询")
    public Result<JSONObject> getRoleField(@RequestBody RoleFieldRequestBO requestBO) {
        JSONObject result = roleFieldService.getAuth(requestBO);
        return Result.ok(result);
    }

    @PostMapping("/roleField/save")
    @ApiOperation("保存字段权限")
    public Result saveUserRole(@RequestBody RoleFieldSaveBO saveBO) {
        roleFieldService.saveRoleField(saveBO);
        return Result.ok();
    }

    @PostMapping("/roles/users")
    @ApiOperation("获取用户的角色")
    public Result<Map<Long, List<ModuleRole>>> getByUserIds(@RequestBody List<Long> userIds) {
        Map<Long, List<ModuleRole>> result = roleService.getByUserIds(userIds);
        return Result.ok(result);
    }

    @PostMapping("/dataAuth/{dataId}")
    @ApiOperation("获取用户数据权限")
    public Result<Boolean> getDataAuth(@PathVariable Long dataId) {
        Boolean result = roleService.getDataAuth(dataId);
        return Result.ok(result);
    }
}
