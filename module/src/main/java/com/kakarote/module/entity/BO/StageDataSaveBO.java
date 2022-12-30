package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjj
 * @title: StageDataSaveBO
 * @description: 阶段数据保存BO
 * @date 2022/4/12 16:49
 */
@Data
@ApiModel(value = "阶段数据保存BO", description = "阶段数据保存BO")
public class StageDataSaveBO {

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "阶段流程ID")
    private Long stageSettingId;

    @ApiModelProperty(value = "阶段ID")
    private Long stageId;

    @ApiModelProperty(value = "阶段名称")
    private String stageName;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "数据ID")
    private Long dataId;

    @ApiModelProperty(value = "0 未开始 1 完成 2 草稿 3 结束")
    private Integer status;

    @ApiModelProperty(value = "阶段工作数据")
    private String taskData;

    @ApiModelProperty(value = "阶段流程主体")
    private Boolean isMain = false;

    @ApiModelProperty(value = "清除包含当前及后续的工作")
    private Boolean clearAll;
}
