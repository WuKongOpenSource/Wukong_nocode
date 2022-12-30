package com.kakarote.module.entity.BO;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zjj
 * @title: StageDataBO
 * @description: 阶段数据BO
 * @date 2022/4/13 17:29
 */
@Data
@ApiModel(value = "阶段数据BO", description = "阶段数据BO")
public class StageDataBO {

    @ApiModelProperty(value = "阶段流程ID")
    private Long stageSettingId;

    @ApiModelProperty(value = "阶段流程主体")
    private Boolean isMain;

    @ApiModelProperty(value = "阶段ID")
    private Long stageId;

    @ApiModelProperty(value = "阶段名称")
    private String stageName;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty("数据ID")
    private Long dataId;

    @ApiModelProperty("表单数据")
    private String fieldData;

    @ApiModelProperty("阶段工作数据")
    private JSONArray taskData;

    @ApiModelProperty(value = "0 未开始 1 完成 2 草稿 3 成功 4 失败")
    private Integer status;

    @ApiModelProperty("模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "创建人")
    private Long createUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
