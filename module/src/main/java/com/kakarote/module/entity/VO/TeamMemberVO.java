package com.kakarote.module.entity.VO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author zjj
 * @title: TeamMemberVO
 * @description: 团队成员VO
 * @date 2021/11/2214:49
 */

@Data
@ApiModel(value = "团队成员VO")
@Accessors(chain = true)
public class TeamMemberVO {

    @ApiModelProperty(value = "用户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @ApiModelProperty("昵称")
    private String realname;

    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @ApiModelProperty(value = "1 只读 2 读写 3 负责人")
    private Integer power;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "过期时间")
    private Date expiresTime;

    @ApiModelProperty(value = "创建人")
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;
}
