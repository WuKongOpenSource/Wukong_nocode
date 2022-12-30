package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 应用表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_module_metadata")
@ApiModel(value="ModuleMetadata对象", description="应用表")
public class ModuleMetadata implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "应用ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(type = IdType.ASSIGN_ID)
    private Long applicationId;

    @ApiModelProperty(value = "源应用ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sourceId;

    @ApiModelProperty(value = "应用名称")
    private String name;

    @ApiModelProperty(value = "应用简介")
    private String description;

    @ApiModelProperty(value = "应用详情描述")
    private String detail;

    @ApiModelProperty(value = "是否精选")
    private Boolean isFeatured;

    @ApiModelProperty(value = "主图")
    private String mainPicture;

    @ApiModelProperty(value = "详情图")
    private String detailPicture;

    @ApiModelProperty(value = "0 自定义 1 安装 2 导入 3 自用系统 4 其他")
    private Integer type;

    @ApiModelProperty(value = "关联的应用 ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long relateApplicationId;

    @ApiModelProperty(value = "应用图标")
    private String icon;

	@ApiModelProperty(value = "图标颜色")
	private String iconColor;

    @ApiModelProperty(value = "状态 1 正常 2 禁用")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
