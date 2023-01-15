package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author : zjj
 * @since : 2023/1/5
 */
@Data
@Accessors(chain = true)
@TableName("wk_router")
@ApiModel(value = "AdminRouter对象", description = "全局路由配置表")
public class Router implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("路由 ID")
    private Long routerId;

    @ApiModelProperty("上级路由 ID")
    private Long parentId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("别名")
    private String name;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("路径")
    private String path;

    @ApiModelProperty("权限")
    private String permissions;

    @ApiModelProperty("组件")
    private String component;

    @ApiModelProperty("重定向")
    private String redirect;

    @ApiModelProperty("隐藏")
    private Boolean hidden;

    @ApiModelProperty("路由类型 0 模块 1 分组")
    private Integer type;

    @ApiModelProperty("是系统级别")
    private Boolean isSystem;

    @ApiModelProperty("排序（同级有效）")
    private Integer sort;

    @ApiModelProperty("状态 1 启用 0 禁用")
    private Integer status;

    @ApiModelProperty("扩展数据")
    private String extData;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("模块 ID")
    private Long sourceModuleId;

    @ApiModelProperty("源应用 ID")
    private Long sourceApplicationId;

    @ApiModelProperty("应用 ID")
    private Long applicationId;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;
}
