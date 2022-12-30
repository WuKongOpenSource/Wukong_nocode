package com.kakarote.module.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * @author zjj
 * @title: CustomNoticeSaveBO
 * @description: 自定义通知保存 BO
 * @date 2022/3/22 16:54
 */
@Data
@ApiModel(value = "自定义通知保存 BO", description = "自定义通知保存 BO")
public class CustomNoticeSaveBO {

    @NotEmpty
    @ApiModelProperty("模块ID")
    private Long moduleId;

    @NotEmpty
    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty("提醒ID")
    private Long noticeId;

    @ApiModelProperty("提醒名称")
    private String noticeName;

    @ApiModelProperty(value = "生效类型: 0  新增数据, 1 更新数据 2 更新指定字段 3 根据模块时间字段 4 自定义时间")
    private Integer effectType;

    @ApiModelProperty(value = "指定更新字段", dataType = "json")
    private List<Long> updateFields;

    @ApiModelProperty(value = "模块时间字段配置", dataType = "json")
    private CustomNoticeTimeFieldConfig timeFieldConfig;

    @ApiModelProperty(value = "生效时间")
    private Date effectTime;

    @ApiModelProperty(value = "重复周期", dataType = "json")
    private CustomNoticeRepeatPeriod repeatPeriod;

    @ApiModelProperty(value = "生效条件", dataType = "json")
    private List<CommonConditionBO> effectConfig;

    @ApiModelProperty(value = "接收人配置", dataType = "json")
    private CustomNoticeReceiverSaveBO receiveConfig;


    @ApiModel("模块时间字段配置")
    @Data
    public static class CustomNoticeTimeFieldConfig {

        @ApiModelProperty(value = "字段ID")
        private Long fieldId;

        @ApiModelProperty(value = "天数")
        private Integer days;

        @ApiModelProperty(value = "小时")
        private Integer hour;

        @ApiModelProperty(value = "分钟")
        private Integer minute;
    }

    @ApiModel("重复周期")
    @Data
    public static class CustomNoticeRepeatPeriod {

        @ApiModelProperty(value = "单位: H 小时,D 天,W 周,M 月,Y 年")
        private String unit;

        @ApiModelProperty(value = "值")
        private Integer value;

        @ApiModelProperty(value = "停止类型: 0 永不结束, 1 指定重复次数, 2 根据表单内时间段")
        private Integer stopType;

        @ApiModelProperty(value = "重复次数")
        private Integer repeatCount;

        @ApiModelProperty(value = "字段ID")
        private Long fieldId;
    }
}
