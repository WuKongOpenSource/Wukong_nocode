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
 * @description: Category
 * @date 2022/6/9
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_category")
@ApiModel(value = "Category 对象", description = "应用分类表")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "category_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long categoryId;

    @ApiModelProperty("分类名称")
    private String name;

    @ApiModelProperty(value = "父级分类")
    private Long parentId;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "0 关闭 1 开启")
    private Integer status;

    @ApiModelProperty(value = "分类类型 0 自定义 1 收藏")
    private Integer type;

    @ApiModelProperty(value = "系统分类")
    private Boolean isSystem;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
