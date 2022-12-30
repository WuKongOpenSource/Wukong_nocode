package com.kakarote.module.entity.BO;

import com.kakarote.module.entity.PO.ModuleRoleField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zjj
 * @title: RoleFieldSaveBO
 * @description: 字段权限保存BO
 * @date 2021/12/412:20
 */
@Data
@ApiModel(value = "字段权限保存BO", description = "字段权限保存BO")
public class RoleFieldSaveBO {

    @ApiModelProperty(value = "模块ID")
    @NotNull
    private Long moduleId;

    @ApiModelProperty(value = "角色ID")
    @NotNull
    private Long roleId;

    @ApiModelProperty(value = "字段权限")
    private List<ModuleRoleField> roleFieldList;
}
