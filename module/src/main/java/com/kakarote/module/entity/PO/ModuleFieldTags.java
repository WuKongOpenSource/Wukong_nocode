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
 * @title: ModuleFieldTags
 * @description: 字段标签选项表
 * @date 2022/3/314:47
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_module_field_tags")
@ApiModel(value="ModuleFieldTags对象", description="字段标签选项表")
public class ModuleFieldTags implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "字段ID")
    private Long fieldId;

    @TableField(value = "`key`")
    @ApiModelProperty(value = "选项ID")
    private String key;

    @TableField(value = "`value`")
    @ApiModelProperty(value = "选项值")
    private String value;

    @ApiModelProperty(value = "颜色")
    private String color;

    @ApiModelProperty(value = "排序")
    private Integer sorting;

    @ApiModelProperty(value = "分组ID")
    private Integer groupId;

    @ApiModelProperty(value = "分组名字")
    private String groupName;
}
