package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zjj
 * @title: UserRoleSaveBO
 * @description: 保存用户的角色BO
 * @date 2021/12/315:18
 */
@Data
@ApiModel(value = "保存用户的角色BO", description = "保存用户的角色BO")
public class UserRoleSaveBO {

    @ApiModelProperty(value = "用户ID")
    private List<Long> userIds;

    @ApiModelProperty(value = "部门ID")
    private List<Long> deptIds;

    @ApiModelProperty(value = "应用ID")
    private List<ApplicationRoleBO> roles;

    @Data
    public static class ApplicationRoleBO{

        @ApiModelProperty(value = "应用ID")
        @NotNull
        private Long applicationId;

        @ApiModelProperty(value = "角色ID")
        private List<Long> roleIds;

    }
}
