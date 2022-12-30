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
 * @author zjj
 * @title: BiElementField
 * @description: BiElementField
 * @date 2022/7/6
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_bi_element_field")
@ApiModel(value = "BiElementField 对象", description = "BiElementField")
public class BiElementField implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "组件ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long elementId;

    @ApiModelProperty(value = "模块ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long moduleId;

    @ApiModelProperty(value = "自定义字段ID")
    private  Long fieldId;

    @ApiModelProperty(value = "字段类型")
    private String formType;

    @ApiModelProperty(value = "自定义字段名称")
    private String fieldName;

    @ApiModelProperty(value = "自定义字段展示名称")
    private String name;

    @ApiModelProperty(value = "字段数据类型，1、左维度字段 2 右维度字段 3 左指标字段 4 右指标字段 5 过滤条件")
    private Integer dataType;

    @ApiModelProperty(value = "字段排序方式 0 默认 1 升序 2 降序")
    private Integer orderType;

    @ApiModelProperty(value = "维度字段和指标字段为汇总方式，过滤条件为搜索条件")
    private String fieldText;

    @ApiModelProperty(value = "其余额外信息")
    private String extraText;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "修改人ID")
    @TableField(fill = FieldFill.UPDATE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUserId;

}
