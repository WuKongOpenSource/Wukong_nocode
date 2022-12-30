package com.kakarote.module.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_module_data_operation_record")
@ApiModel(value="ModuleDataOperationRecord 对象", description="字段值操作记录表")
public class ModuleDataOperationRecord implements Serializable {
	private static final long serialVersionUID=1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	@ApiModelProperty(value = "模块ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

	@ApiModelProperty(value = "数据ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long dataId;

	@ApiModelProperty(value = "值")
	private String value;

	@ApiModelProperty(value = "操作类型")
	private Integer actionType;

	@ApiModelProperty(value = "原负责人ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long fromUserId;

	@ApiModelProperty(value = "现负责人ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long toUserId;

	@ApiModelProperty(value = "团队成员ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long teamUserId;

	@ApiModelProperty(value = "节点ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long flowId;

	@ApiModelProperty(value = "审核记录ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long examineRecordId;

	@ApiModelProperty(value = "扩展数据")
	private String extData;

	@ApiModelProperty(value = "创建人ID")
	@TableField(fill = FieldFill.INSERT)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long createUserId;

	@ApiModelProperty(value = "创建时间")
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;

	@ApiModelProperty(value = "备注")
	private String remarks;
}
