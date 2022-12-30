package com.kakarote.module.entity.BO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author zjj
 * @title: TeamMemberSaveBO
 * @description: 团队成员保存BO
 * @date 2021/11/229:36
 */

@Data
@ApiModel(value = "团队成员保存BO")
@Accessors(chain = true)
public class TeamMemberSaveBO extends TeamMemberRemoveBO{

    @ApiModelProperty("权限（1.只读 2.读写）")
    private Integer power;

    @ApiModelProperty("过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expiresTime;
}
