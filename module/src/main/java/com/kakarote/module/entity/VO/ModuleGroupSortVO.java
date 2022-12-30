package com.kakarote.module.entity.VO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wwl
 * @date 2022/3/26 10:19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ModuleGroupSortVO extends ModuleListVO  {

    @ApiModelProperty(value = "分组id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;


}
