package com.kakarote.module.entity.VO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.kakarote.common.entity.SimpleUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zjj
 * @title: ModuleDataOperationRecordVO
 * @description:  操作记录VO
 * @date 2021/11/2510:58
 */
@Data
@ApiModel("操作记录VO")
public class ModuleDataOperationRecordVO {

    @ApiModelProperty(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "模块ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long moduleId;

    @ApiModelProperty(value = "数据ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dataId;

    @ApiModelProperty(value = "值")
    private String value;

    @ApiModelProperty(value = "操作类型")
    private Integer actionType;

    @ApiModelProperty(value = "原负责人")
    private SimpleUser fromUser;

    @ApiModelProperty(value = "现负责人")
    private SimpleUser toUser;

    @ApiModelProperty(value = "团队成员")
    private SimpleUser teamUser;

    @ApiModelProperty(value = "审批记录")
    private ExamineRecord examineRecord;

    @ApiModelProperty(value = "扩展数据")
    private String extData;

    @ApiModelProperty(value = "创建人")
    private SimpleUser createUser;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @Data
    @ApiModel("审核记录")
    public static class ExamineRecord{
        @ApiModelProperty(value = "审核记录ID")
        @JsonSerialize(using = ToStringSerializer.class)
        private Long recordId;

        @ApiModelProperty(value = "创建人")
        private SimpleUser createUser;
    }
}
