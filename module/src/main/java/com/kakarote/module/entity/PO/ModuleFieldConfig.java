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
 * <p>
 * 字段配置表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_module_field_config")
@ApiModel(value="ModuleFieldConfig对象", description="字段配置表")
public class ModuleFieldConfig implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    @ApiModelProperty(value = "字段类型 1 keyword 2 date 3 number 4 nested 5 datetime 6 detailTable")
    private Integer fieldType;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


    public ModuleFieldConfig() {
    }

    public ModuleFieldConfig(String fieldName) {
        this.fieldName = fieldName;
    }
}
