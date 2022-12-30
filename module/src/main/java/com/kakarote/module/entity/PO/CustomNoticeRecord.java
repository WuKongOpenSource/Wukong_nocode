package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zjj
 * @title: CustomNoticeRecord
 * @description: 自定义提醒记录
 * @date 2022/3/23 16:57
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_custom_notice_record")
@ApiModel(value = "CustomNoticeRecord 对象", description = "自定义提醒记录")
public class CustomNoticeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty("数据ID")
    private Long dataId;

    @ApiModelProperty("提醒ID")
    private Long noticeId;

    @ApiModelProperty("0 未处理 1 已发送 2 废弃")
    private Integer status;

    @ApiModelProperty("模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty("批次ID")
    private String batchId;

    @ApiModelProperty(value = "重复次数")
    private Integer repeatCount;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "上次处理时间")
    private LocalDateTime lastDealTime;

}
