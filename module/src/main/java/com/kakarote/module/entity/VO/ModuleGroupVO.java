package com.kakarote.module.entity.VO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wwl
 * @date 2022/3/17 14:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("模块分组返回给页面的参数")
public class ModuleGroupVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "应用id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;

    @ApiModelProperty(value = "应用id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long applicationId;

    @ApiModelProperty(value = "分组名称")
    private String groupName;

    @ApiModelProperty(value = "分组的图标")
    private String icon;

    @ApiModelProperty(value = "分组的图标的颜色")
    private String iconColor;

    @ApiModelProperty(value = "创建者id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @ApiModelProperty(value = "创建人名称")
    private String createUserName;

    @ApiModelProperty(value = "模块id")
    private List<Long> moduleIdList;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

}
