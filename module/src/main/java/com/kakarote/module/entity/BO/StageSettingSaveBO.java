package com.kakarote.module.entity.BO;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.kakarote.common.entity.SimpleUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zjj
 * @title: StageSettingSaveBO
 * @description: 阶段流程保存BO
 * @date 2022/4/11 14:24
 */
@Data
@ApiModel(value = "阶段流程保存BO", description = "阶段流程保存BO")
public class StageSettingSaveBO {

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "阶段流程ID")
    private Long stageSettingId;

    @ApiModelProperty(value = "阶段流程名称")
    private String stageSettingName;

    @ApiModelProperty(value = "成功阶段的名称")
    private String successName;

    @ApiModelProperty(value = "失败阶段的名称")
    private String failedName;

    @ApiModelProperty(value = "适用部门")
    private List<Long> deptIds;

    @ApiModelProperty(value = "适用部门")
    private List<JSONObject> deptList;

    @ApiModelProperty(value = "适用员工")
    private List<Long> userIds;

    @ApiModelProperty(value = "适用员工")
    private List<SimpleUser> userList;

    @ApiModelProperty(value = "状态 1 正常 0 停用")
    private Integer status;

    @ApiModelProperty(value = "阶段列表")
    private List<StageBO> stageBOList;

    @ApiModelProperty(value = "创建人")
    private Long createUserId;

    @ApiModelProperty(value = "创建人")
    private SimpleUser createUser;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Data
    @ApiModel(value = "阶段BO", description = "阶段BO")
    public static class StageBO {

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

        @ApiModelProperty(value = "任务列表")
        private List<StageTaskBO> taskBOList;

        @ApiModelProperty(value = "字段列表")
        private List<StageFieldBO> fieldBOList;

        @ApiModelProperty(value = "节点信息")
        List<FlowSaveBO> flowSaveBOList;


    }

    @Data
    @ApiModel(value = "阶段字段BO", description = "阶段字段BO")
    public static class StageTaskBO {

        @ApiModelProperty(value = "模块ID")
        private Long moduleId;

        @ApiModelProperty(value = "版本号")
        private Integer version;

        @ApiModelProperty(value = "阶段流程ID")
        private Long stageSettingId;

        @ApiModelProperty(value = "阶段ID")
        private Long stageId;

        @ApiModelProperty(value = "任务ID")
        private Long taskId;

        @ApiModelProperty(value = "任务名称")
        private String taskName;

        @ApiModelProperty(value = "排序")
        private Integer sort;

        @ApiModelProperty(value = "是否必做")
        private Boolean isMust;
    }

    @Data
    @ApiModel(value = "阶段字段BO", description = "阶段字段BO")
    public static class StageFieldBO {

        @ApiModelProperty(value = "模块ID")
        private Long moduleId;

        @ApiModelProperty(value = "版本号")
        private Integer version;

        @ApiModelProperty(value = "阶段流程ID")
        private Long stageSettingId;

        @ApiModelProperty(value = "阶段ID")
        private Long stageId;

        @ApiModelProperty(value = "主键ID")
        private Long fieldId;

        @ApiModelProperty(value = "自定义字段英文标识")
        private String fieldName;

        @ApiModelProperty(value = "字段名称")
        private String name;

        @ApiModelProperty(value = "字段类型")
        private Integer type;

        @ApiModelProperty(value = "0 系统 1 自定义")
        private Integer fieldType;

        @ApiModelProperty(value = "类型")
        private String formType;

        @ApiModelProperty(value = "字段提示")
        private String remark;

        @ApiModelProperty(value = "输入提示")
        private String inputTips;

        @ApiModelProperty(value = "最大长度")
        private Integer maxLength;

        @ApiModelProperty(value = "是否唯一 1 是 0 否")
        private Integer isUnique;

        @ApiModelProperty(value = "是否必填 1 是 0 否")
        private Integer isNull;

        @ApiModelProperty(value = "排序 从小到大")
        private Integer sorting;

        @ApiModelProperty(value = "是否隐藏  0不隐藏 1隐藏")
        private Integer isHidden;

        @ApiModelProperty(value = "样式百分比%")
        private Integer stylePercent;

        @ApiModelProperty(value = "精度，允许的最大小数位")
        private Integer precisions;

        @ApiModelProperty(value = "表单定位 坐标格式： 1,1")
        private String formPosition;

        @ApiModelProperty(value = "限制的最大数值")
        private String maxNumRestrict;

        @ApiModelProperty(value = "限制的最小数值")
        private String minNumRestrict;
    }
}
