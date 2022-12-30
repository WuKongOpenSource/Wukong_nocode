package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zjj
 * @title: ModuleRole
 * @description: 角色表
 * @date 2021/12/115:06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_module_role")
@ApiModel(value="ModuleRole对象", description="角色表")
public class ModuleRole implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("场景ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long roleId;

    @ApiModelProperty(value = "名称")
    @NotNull
    private String roleName;

    @ApiModelProperty(value = "应用ID")
    @NotNull
    private Long applicationId;

    @ApiModelProperty(value = "权限范围：1本人 2本人及下属 3本部门 4本部门及下属部门 5全部 ")
    @NotNull
    private Integer rangeType;

    @ApiModelProperty(value = "1 启用 0 禁用")
    private Boolean isActive;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;
}
