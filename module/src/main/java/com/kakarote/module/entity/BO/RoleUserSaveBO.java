package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zjj
 * @title: RoleUserSaveBO
 * @description: 角色用户关系保存BO
 * @date 2021/12/213:11
 */
@Data
@ApiModel(value = "角色用户关系保存BO", description = "角色用户关系保存BO")
public class RoleUserSaveBO {

    @ApiModelProperty(value = "应用ID")
    private Long applicationId;

    @ApiModelProperty(value = "角色ID")
    private Long roleId;

    @ApiModelProperty(value = "用户ID")
    private List<Long> userIds;

}
