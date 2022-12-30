package com.kakarote.module.entity.BO;

import com.alibaba.fastjson.annotation.JSONField;

import com.kakarote.module.entity.proto.FieldDataSnapshotBuf;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author zjj
 * @title: DoubleCheckResultBO
 * @description: 字段验重结果BO
 * @date 2022/2/2513:39
 */
@Data
@ToString
@ApiModel(value = "字段验重结果BO", description = "字段验重结果BO")
public class DoubleCheckResultBO {

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "验重通过")
    private Boolean isPass = true;

    @ApiModelProperty(value = "重复的字段和值")
    private List<ModuleFieldValueBO> repeatFieldValueList;

    @JSONField(serialize = false)
    @ApiModelProperty(value = "当前数据快照")
    private FieldDataSnapshotBuf.FieldDataSnapshot currentSnapshot;

}
