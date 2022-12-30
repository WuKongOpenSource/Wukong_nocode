package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zjj
 * @title: TeamMemberRemoveBO
 * @description: 团队成员移除BO
 * @date 2021/11/2210:06
 */
@Data
@ApiModel(value = "团队成员移除BO")
@Accessors(chain = true)
public class TeamMemberRemoveBO {

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "数据ID")
    private Long dataId;

    @ApiModelProperty("成员ids")
    private List<Long> memberIds;
}
