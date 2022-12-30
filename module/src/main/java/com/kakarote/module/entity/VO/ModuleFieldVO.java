package com.kakarote.module.entity.VO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.kakarote.module.entity.BO.ModuleFieldBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author wwl
 * @date 2022/4/1 9:58
 */
@Data
public class ModuleFieldVO {

    @ApiModelProperty(value = "模块ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long moduleId;

    @ApiModelProperty(value = "模块名称")
    private String name;

    @ApiModelProperty(value = "字段list")
    List<ModuleFieldBO> fieldList = new LinkedList<>();

}
