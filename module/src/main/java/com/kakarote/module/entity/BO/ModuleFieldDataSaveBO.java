package com.kakarote.module.entity.BO;

import com.kakarote.module.entity.PO.ModuleFieldData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-08 17:47
 */
@Data
@ToString
@ApiModel(value = "ModuleFieldDataSaveBO 保存对象", description = "ModuleFieldDataSaveBO")
public class ModuleFieldDataSaveBO {

	@ApiModelProperty(value = "模块ID")
	@NotNull
	private Long moduleId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

	@ApiModelProperty(value = "分类ID")
	private Long categoryId;

	@ApiModelProperty(value = "数据ID: {新增: null, 编辑: NOT NULL}")
	private Long dataId;

	@ApiModelProperty(value = "节点状态 0 待处理 1 已处理 2 拒绝 3 处理中 4 撤回 5 未提交 8 作废 9 忽略")
	private Integer status;

	@ApiModelProperty(value = "数据批次号")
	private String batchId;

	@ApiModelProperty(value = "模块字段值列表")
	private List<ModuleFieldData> fieldDataList = new ArrayList<>();

	@ApiModelProperty(value = "流程数据")
	private ExamineRecordSaveBO flowSaveBO;

}
