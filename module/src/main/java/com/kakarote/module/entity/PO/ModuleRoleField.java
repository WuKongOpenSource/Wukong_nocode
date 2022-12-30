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
 * @title: ModuleRoleField
 * @description: 角色字段关系表
 * @date 2021/12/410:06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_module_role_field")
@ApiModel(value="ModuleRoleField 对象", description="角色字段关系表")
public class ModuleRoleField implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "名称")
    private Long roleId;

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "字段ID")
    private Long fieldId;

    @ApiModelProperty(value = "权限")
    private Integer authLevel;

    @ApiModelProperty(value = "操作权限")
    private Integer operateType;

    @ApiModelProperty(value = "掩码类型")
    private Integer maskType;
}
