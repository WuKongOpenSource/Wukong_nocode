package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zjj
 * @title: ModuleRoleUser
 * @description: 角色用户表
 * @date 2021/12/213:07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_module_role_user")
@ApiModel(value="ModuleRoleUser对象", description="角色用户表")
public class ModuleRoleUser implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "名称")
    private Long roleId;

    @ApiModelProperty(value = "用户ID")
    private Long UserId;

    @ApiModelProperty(value = "应用ID")
    private Long applicationId;
}
