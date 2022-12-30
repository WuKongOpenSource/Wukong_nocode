package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjj
 * @title: RoleFieldRequestBO
 * @description: 字段权限请求BO
 * @date 2021/12/411:45
 */
@Data
@ApiModel(value = "字段权限请求BO", description = "字段权限请求BO")
public class RoleFieldRequestBO {

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "角色ID")
    private Long roleId;
}
