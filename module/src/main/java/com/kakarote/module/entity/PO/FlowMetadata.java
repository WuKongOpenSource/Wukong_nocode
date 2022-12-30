package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 模块自定义流程元数据表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_flow_metadata")
@ApiModel(value="FlowMetadata对象", description="模块自定义流程元数据表")
public class FlowMetadata implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "metadata_id", type = IdType.ASSIGN_ID)
    private Long metadataId;

    @ApiModelProperty(value = "流程类型 0 系统 1 自定义按钮")
    private Integer type;

    @ApiModelProperty(value = "对应类型ID")
    private Long typeId;

    @ApiModelProperty(value = "状态 1 正常 2 停用")
    private Integer status;

    @ApiModelProperty(value = "批次ID")
    private String batchId;

    @ApiModelProperty(value = "模块ID")
    private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;


}
