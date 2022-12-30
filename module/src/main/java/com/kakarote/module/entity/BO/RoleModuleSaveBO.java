package com.kakarote.module.entity.BO;

import com.kakarote.module.entity.PO.ModuleRoleModule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zjj
 * @title: RoleModuleSaveBO
 * @description: 角色模块权限保存BO
 * @date 2021/12/211:48
 */
@Data
@ApiModel(value = "角色模块权限保存BO", description = "角色模块权限保存BO")
public class RoleModuleSaveBO {

    @ApiModelProperty(value = "角色ID")
    private Long roleId;

    @ApiModelProperty(value = "角色模块关系")
    private List<ModuleRoleModule> roleModules;
}
