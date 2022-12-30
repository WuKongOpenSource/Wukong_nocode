package com.kakarote.module.entity.VO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.kakarote.module.constant.ModuleFieldEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 字段排序表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-19
 */
@Data
@Accessors(chain = true)
@ApiModel(value="ModuleFieldSort列表对象", description="字段排序表")
public class ModuleFieldSortVO implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "字段ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fieldId;

    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    @ApiModelProperty(value = "展示名称")
    private String name;

    @ApiModelProperty(value = "字段类型")
    private Integer type;

    @ApiModelProperty(value = "类型")
    private String formType;

    @ApiModelProperty(value = "字段宽度")
    private Integer width;

    @ApiModelProperty(value = "是否隐藏 0、不隐藏 1、隐藏")
    private Integer isHide;

    @ApiModelProperty(value = "是否必填 1 是 0 否")
    private Integer isNull;

    @ApiModelProperty(value = "字段锁定")
    private Boolean isLock;

    public ModuleFieldSortVO() {
    }

    public ModuleFieldSortVO(String fieldName, String name, ModuleFieldEnum fieldEnum) {
        this.fieldName = fieldName;
        this.name = name;
        this.type = fieldEnum.getType();
    }
}
