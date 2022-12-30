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
 * @title: CustomNotice
 * @description: 自定义提醒
 * @date 2022/3/22 16:40
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_custom_notice")
@ApiModel(value = "CustomNotice 对象", description = "自定义提醒")
public class CustomNotice implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty("提醒ID")
    private Long noticeId;

    @ApiModelProperty("按钮名称")
    private String noticeName;

    @ApiModelProperty(value = "生效类型: 0  新增数据, 1 更新数据 2 更新指定字段 3 根据模块时间字段 4 自定义时间")
    private Integer effectType;

    @ApiModelProperty(value = "指定更新字段", dataType = "json")
    private String updateFields;

    @ApiModelProperty(value = "模块时间字段配置", dataType = "json")
    private String timeFieldConfig;

    @ApiModelProperty(value = "生效时间")
    private Date effectTime;

    @ApiModelProperty(value = "重复周期", dataType = "json")
    private String repeatPeriod;

    @ApiModelProperty(value = "生效条件", dataType = "json")
    private String effectConfig;

    @ApiModelProperty("模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

}
