package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zjj
 * @title: CustomCategoryRule
 * @description: 自定义模块分类规则
 * @date 2022/3/29 15:11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_custom_category_rule")
@ApiModel(value = "CustomCategoryRule 对象", description = "自定义模块分类规则")
public class CustomCategoryRule implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty("规则ID")
    private Long ruleId;

    @ApiModelProperty(value = "分类ID")
    private Long categoryId;

    @TableField(value = "`from`")
    @ApiModelProperty(value = "数据来源分组ID")
    private Long from;

    @TableField(value = "`to`")
    @ApiModelProperty(value = "数据去向分组ID")
    private Long to;

    @ApiModelProperty(value = "公式")
    private String formula;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty("模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
