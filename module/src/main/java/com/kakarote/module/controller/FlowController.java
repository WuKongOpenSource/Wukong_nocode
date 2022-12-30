package com.kakarote.module.controller;


import com.alibaba.fastjson.JSONArray;
import com.kakarote.common.result.Result;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.VO.FlowVO;
import com.kakarote.module.service.IFlowDataDealRecordService;
import com.kakarote.module.service.IFlowFillService;
import com.kakarote.module.service.IFlowProvider;
import com.kakarote.module.service.IFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 模块流程表 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
@RestController
@RequestMapping("/moduleFlow")
@Api(tags = "流程配置")
public class FlowController {

	@Autowired
	private IFlowProvider flowProvider;

	@Autowired
	private IFlowFillService flowFillService;

	@Autowired
	private IFlowDataDealRecordService dealRecordService;

	@PostMapping("/preview")
	@ApiOperation("预览流程")
	public Result<List<FlowVO>> previewFlow(@RequestBody FlowPreviewBO previewBO) {
		return Result.ok(flowProvider.previewFlow(previewBO));
	}

	@PostMapping("/flowDetail")
	@ApiOperation("流程详情")
	public Result<List<FlowVO>> flowDetail(@RequestBody FlowDetailQueryBO queryBO) {
		return Result.ok(flowProvider.flowDetail(queryBO));
	}

	@PostMapping("/fill/saveFieldValue")
	@ApiOperation("填写节点保存字段数据值")
	public Result flowDetail(@RequestBody FlowFillFieldDataSaveBO dataSaveBO) {
		flowFillService.saveFieldValue(dataSaveBO);
		return Result.ok();
	}

	@PostMapping("/dealDetail")
	@ApiOperation("获取数据的节点处理详情")
	public Result<JSONArray> dealDetail(FlowDealDetailQueryBO queryBO) {
		JSONArray result = dealRecordService.getFlowDealDetail(queryBO);
		return Result.ok(result);
	}

	@PostMapping("/transferFlow")
	@ApiOperation("节点转交")
	public Result transferFlow(TransferFlowBO transferFlowBO) {
		dealRecordService.transferFlow(transferFlowBO);
		return Result.ok();
	}
}

