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
 * @title: BiElement
 * @description: BiElement
 * @date 2022/7/6
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_bi_element")
@ApiModel(value = "BiElement 对象", description = "BiElement")
public class BiElement implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "element_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long elementId;

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "组件类型")
    private Integer type;

    @ApiModelProperty(value = "json类型 x 横轴坐标 y 纵轴坐标 w 组件宽度 h 组件高度")
    private String coordinate;

    @ApiModelProperty(value = "仪表盘样式")
    private String wsStyle;

    @ApiModelProperty(value = "数据来源ID")
    private Long targetId;

    @ApiModelProperty(value = "数据来源类型1 crm 2 低代码")
    private Integer targetType;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "修改人ID")
    @TableField(fill = FieldFill.UPDATE)
    private Long updateUserId;

}
