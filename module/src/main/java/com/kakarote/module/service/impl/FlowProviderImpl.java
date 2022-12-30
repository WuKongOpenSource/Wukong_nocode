package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.service.UserService;
import com.kakarote.module.constant.FlowMetadataTypeEnum;
import com.kakarote.module.constant.FlowTypeEnum;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.entity.VO.FlowConditionDataVO;
import com.kakarote.module.entity.VO.FlowVO;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : zjj
 * @since : 2022/12/27
 */
@Service
public class FlowProviderImpl implements IFlowCommonService, IFlowProvider {

    @Autowired
    private IFlowService flowService;

    @Autowired
    private IModuleFieldDataProvider fieldDataProvider;

    @Autowired
    private IFlowDataDealRecordService dealRecordService;

    @Autowired
    private IFlowExamineRecordOptionalService optionalService;

    @Autowired
    private IFlowFieldAuthService fieldAuthService;


    @Autowired
    private IFlowMetadataService metadataService;

    @Autowired
    private UserService userService;

    @Autowired
    private IFlowExamineRecordService examineRecordService;

    /**
     * 获取节点的下一个节点
     *
     * @param flow 当前节点
     * @return
     */
    @Override
    public Flow getNextOrUpperNextFlow(Flow flow) {
        Flow nextFlow = flowService.getNextFlow(flow);
        // 当前层级节点无下一个节点，且是条件下的节点
        if (ObjectUtil.isNull(nextFlow) && ObjectUtil.notEqual(0L, flow.getConditionId())) {
            Flow conditionFlow = flowService.findConditionFlow(flow);
            return getNextOrUpperNextFlow(conditionFlow);
        }
        return nextFlow;
    }

    @Override
    public List<FlowVO> getFlowVOList(FlowMetadata flowMetadata, Long ownerUserId) {
        List<FlowVO> result = new ArrayList<>();
        if (ObjectUtil.isNull(flowMetadata)) {
            return result;
        }
        // 获取模块所有节点
        List<Flow> allFlows = flowService.lambdaQuery()
                .eq(Flow::getBatchId, flowMetadata.getBatchId())
                .eq(Flow::getModuleId, flowMetadata.getModuleId())
                .list();
        if (CollUtil.isEmpty(allFlows)) {
            return result;
        }
        Map<Long, List<Flow>> flowMap = allFlows.stream().collect(Collectors.groupingBy(Flow::getConditionId));
        List<Flow> flows = flowMap.remove(0L);
        flows.sort((f1, f2) -> f1.getPriority() > f2.getPriority() ? 1 : -1);
        Map<String, Object> allData = new HashMap<>(16);
        allData.put("FLOW", flowMap);
        for (FlowTypeEnum flowTypeEnum : FlowTypeEnum.values()) {
            IFlowTypeService flowTypeService = ApplicationContextHolder.getBean(flowTypeEnum.getServiceName());
            flowTypeService.queryExamineData(allData, flowMetadata.getBatchId());
        }
        // 流程的所有字段授权
        List<FlowFieldAuth> flowFieldAuths = fieldAuthService.getByModuleIdAndVersion(flowMetadata.getModuleId(), flowMetadata.getVersion(), flowMetadata.getMetadataId());
        Map<Long, FlowFieldAuth> flowIdFieldAuthMap = flowFieldAuths.stream().collect(Collectors.toMap(FlowFieldAuth::getFlowId, Function.identity()));
        List<UserInfo> userInfos = userService.queryUserInfoList().getData();
        for (Flow flow : flows) {
            FlowTypeEnum flowTypeEnum = FlowTypeEnum.parse(flow.getFlowType());
            IFlowTypeService flowTypeService = ApplicationContextHolder.getBean(flowTypeEnum.getServiceName());
            FlowVO flowInfo = flowTypeService.createFlowInfo(allData, flow, userInfos, ownerUserId);
            if (MapUtil.isNotEmpty(flowIdFieldAuthMap)) {
                FlowFieldAuth flowFieldAuth = flowIdFieldAuthMap.get(flow.getFlowId());
                if (ObjectUtil.isNotNull(flowFieldAuth)) {
                    flowInfo.setFieldAuth(flowFieldAuth.getAuth());
                }
            }
            result.add(flowInfo);
        }
        return result;
    }

    @Override
    public List<FlowConditionDataVO> queryConditionData(FlowConditionQueryBO conditionQueryBO) {
        FlowMetadata metadata;
        if (ObjectUtil.isNull(conditionQueryBO.getMetadataId())) {
            FlowExamineRecord examineRecord = examineRecordService.getById(conditionQueryBO.getRecordId());
            Long metadataId = Optional.ofNullable(examineRecord).map(r -> r.getFlowMetadataId()).orElse(null);
            metadata = metadataService.getByMetadataId(metadataId);
        } else {
            metadata = metadataService.getByMetadataId(conditionQueryBO.getMetadataId());
        }
        List<FlowVO> flowVOS = this.getFlowVOList(metadata, null);
        List<FlowConditionDataVO> conditionDataVOS = new ArrayList<>();
        getAllConditionData(flowVOS, conditionDataVOS);
        return conditionDataVOS;
    }

    private void getAllConditionData(List<FlowVO> flowVOS, List<FlowConditionDataVO> conditionDataVOS) {
        flowVOS.stream().filter(f -> ObjectUtil.equal(FlowTypeEnum.CONDITION.getType(), f.getFlowType()))
                .forEach(flowVO -> {
                    JSONObject data = flowVO.getData();
                    Object dataObj = data.get("data");
                    String jsonStr = JSON.toJSONString(dataObj);
                    List<FlowVO.FlowCondition> conditionList = JSON.parseArray(jsonStr, FlowVO.FlowCondition.class);
                    if (CollUtil.isNotEmpty(conditionList)) {
                        conditionList.forEach(flowCondition -> {
                            List<FlowConditionBO> conditionBOS = flowCondition.getConditionDataList();
                            if (CollUtil.isNotEmpty(conditionBOS)) {
                                conditionBOS.forEach(flowConditionBO -> {
                                    CommonConditionBO conditionDataBO = flowConditionBO.getSearch();
                                    conditionDataVOS.add(BeanUtil.copyProperties(conditionDataBO,
                                            FlowConditionDataVO.class));
                                });
                            }
                            if (CollUtil.isNotEmpty(flowCondition.getFlowDataList())) {
                                getAllConditionData(flowCondition.getFlowDataList(), conditionDataVOS);
                            }
                        });
                    }
                });
    }

    @Override
    public List<FlowVO> previewFlow(FlowPreviewBO previewBO) {
        List<FlowVO> flowVOList = new ArrayList<>();
        FlowMetadata metadata = metadataService.getByModuleId(previewBO.getModuleId(), previewBO.getVersion(),
                previewBO.getTypeId(), previewBO.getFlowMetadataType());
        if (ObjectUtil.isNull(metadata)) {
            return flowVOList;
        }
        List<FlowVO> flowVOS = getFlowVOList(metadata, UserUtil.getUserId());
        this.getConfirmFlow(flowVOS, previewBO.getFieldValues(), flowVOList, null, previewBO.getModuleId(), metadata.getVersion());

        return flowVOList;
    }

    private void getConfirmFlow(List<FlowVO> flowVOS, List<ModuleFieldValueBO> fieldValueBOS,
                                List<FlowVO> flowVOList, List<FlowDataDealRecord> records,
                                Long moduleId, Integer version) {
        Map<Long, FlowDataDealRecord> flowIdRecordMap = CollUtil.isEmpty(records) ?
                MapUtil.newHashMap() : records.stream().collect(Collectors.toMap(FlowDataDealRecord::getFlowId, Function.identity()));

        for (FlowVO flowVO : flowVOS) {
            if (ObjectUtil.notEqual(FlowTypeEnum.START.getType(), flowVO.getFlowType())) {
                JSONArray data = flowVO.getData().getJSONArray("data");
                String dataJsonString = Optional.ofNullable(data).orElse(new JSONArray()).toJSONString();
                List<FlowVO.FlowCondition> conditionList = JSON.parseArray(dataJsonString, FlowVO.FlowCondition.class);
                if (MapUtil.isNotEmpty(flowIdRecordMap) && flowIdRecordMap.containsKey(flowVO.getFlowId())) {
                    FlowDataDealRecord record = flowIdRecordMap.get(flowVO.getFlowId());
                    if (ObjectUtil.isNotNull(record)) {
                        if (CollUtil.isNotEmpty(conditionList)) {
                            FlowVO.FlowCondition flowCondition = conditionList.stream()
                                    .filter(c -> ObjectUtil.equal(record.getConditionId(), c.getConditionId()))
                                    .findFirst().orElse(null);
                            if (ObjectUtil.isNull(flowCondition)) {
                                continue;
                            }
                            this.getConfirmFlow(flowCondition.getFlowDataList(), fieldValueBOS, flowVOList, records, moduleId, version);
                        } else {
                            flowVO.setFlowStatus(record.getFlowStatus());
                            flowVOList.add(flowVO);
                        }
                        continue;
                    }
                }

                if (CollUtil.isNotEmpty(conditionList)) {
                    for (FlowVO.FlowCondition flowCondition : conditionList) {
                        List<FlowConditionBO> conditionBOS = flowCondition.getConditionDataList();
                        if (CollUtil.isNotEmpty(conditionBOS)) {
                            boolean isPass = conditionPass(conditionBOS, fieldValueBOS, moduleId, version);
                            if (isPass) {
                                if (CollUtil.isNotEmpty(flowCondition.getFlowDataList())) {
                                    this.getConfirmFlow(flowCondition.getFlowDataList(), fieldValueBOS, flowVOList, records, moduleId, version);
                                } else {
                                    flowVOList.add(flowVO);
                                }
                                break;
                            }
                        }
                    }
                    // 条件节点没有通过
                    if (CollUtil.isEmpty(flowVOList)) {
                        return;
                    }
                } else {
                    flowVOList.add(flowVO);
                }
            } else {
                flowVOList.add(flowVO);
            }
        }
    }

    @Override
    public List<ModuleFieldValueBO> getConditionDataMap(FlowExamineRecord record) {
        FlowConditionQueryBO conditionQueryBO = new FlowConditionQueryBO();
        conditionQueryBO.setModuleId(record.getModuleId());
        conditionQueryBO.setVersion(record.getVersion());
        conditionQueryBO.setRecordId(record.getRecordId());
        conditionQueryBO.setMetadataId(record.getFlowMetadataId());
        List<String> fieldNames;
        List<FlowConditionDataVO> conditionDataVOS = this.queryConditionData(conditionQueryBO);
        if (CollUtil.isNotEmpty(conditionDataVOS)) {
            fieldNames = conditionDataVOS.stream().map(FlowConditionDataVO::getFieldName).distinct().collect(Collectors.toList());
            fieldNames.removeIf(StrUtil::isEmpty);
            List<ModuleFieldValueBO> fieldValueBOS = fieldDataProvider.queryValueMap(record.getModuleId(), record.getVersion(), record.getDataId(), fieldNames);
            return fieldValueBOS;
        }
        return new ArrayList<>();
    }

    @Override
    public List<FlowVO> flowDetail(FlowDetailQueryBO queryBO) {
        Long moduleId = queryBO.getModuleId();
        Long dataId = queryBO.getDataId();
        Long typeId = queryBO.getTypeId();
        List<FlowVO> flowVOList = new ArrayList<>();
        // 获取当前数据的审批记录
        FlowExamineRecord examineRecord;
        if (ObjectUtil.isNull(typeId)) {
            examineRecord = examineRecordService.lambdaQuery()
                    .eq(FlowExamineRecord::getModuleId, moduleId)
                    .eq(FlowExamineRecord::getDataId, dataId)
                    .eq(FlowExamineRecord::getFlowMetadataType, FlowMetadataTypeEnum.SYSTEM.getType())
                    .orderByDesc(FlowExamineRecord::getCreateTime)
                    .one();
        } else {
            examineRecord = examineRecordService.lambdaQuery()
                    .eq(FlowExamineRecord::getModuleId, moduleId)
                    .eq(FlowExamineRecord::getDataId, dataId)
                    .eq(FlowExamineRecord::getTypeId, typeId)
                    .eq(FlowExamineRecord::getFlowMetadataType, FlowMetadataTypeEnum.CUSTOM_BUTTON.getType())
                    .orderByDesc(FlowExamineRecord::getCreateTime)
                    .one();
        }
        if (ObjectUtil.isNull(examineRecord)) {
            return flowVOList;
        }
        FlowMetadata metadata = metadataService.getByMetadataId(examineRecord.getFlowMetadataId());
        List<FlowVO> flowVOS = getFlowVOList(metadata, examineRecord.getCreateUserId());
        List<ModuleFieldValueBO> fieldValueBOS = fieldDataProvider.queryValueMap(moduleId, examineRecord.getVersion(), dataId, null);

        List<FlowDataDealRecord> dealRecords = dealRecordService.getMainByRecordId(examineRecord.getRecordId());
        this.getConfirmFlow(flowVOS, fieldValueBOS, flowVOList, dealRecords, examineRecord.getModuleId(), examineRecord.getVersion());
        List<UserInfo> userInfos = userService.queryUserInfoList().getData();
        for (FlowVO flowVO : flowVOList) {
            if (ObjectUtil.equal(FlowTypeEnum.COPY.getType(), flowVO.getFlowType())) {
                FlowVO.FlowCopyData data = JSON.parseObject(flowVO.getData().toJSONString(), FlowVO.FlowCopyData.class);
                List<FlowVO.User> userList = data.getUserList();
                if (ObjectUtil.equal(1, data.getIsAdd())) {
                    List<FlowExamineRecordOptional> optionalList = optionalService.getOptional(examineRecord.getRecordId(), flowVO.getFlowId());
                    List<Long> userIds = optionalList.stream().map(FlowExamineRecordOptional::getUserId).collect(Collectors.toList());
                    userList = searchUsers(userInfos, userIds);
                }
                flowVO.setUsers(userList);
            }
            if (Arrays.asList(FlowTypeEnum.EXAMINE.getType(), FlowTypeEnum.FILL.getType()).contains(flowVO.getFlowType())) {
                FlowVO.FlowExamineData data = JSON.parseObject(flowVO.getData().toJSONString(), FlowVO.FlowExamineData.class);
                if (ObjectUtil.equal(4, data.getExamineType())) {
                    List<FlowExamineRecordOptional> optionalList = optionalService.getOptional(examineRecord.getRecordId(), flowVO.getFlowId());
                    List<Long> userIds = optionalList.stream().map(FlowExamineRecordOptional::getUserId).collect(Collectors.toList());
                    List<FlowVO.User> users = searchUsers(userInfos, userIds);
                    flowVO.setUsers(users);
                }
            }
            flowVO.setCreateUserId(examineRecord.getCreateUserId());
        }
        return flowVOList;
    }

    @Override
    public Flow getFirstFlow(FlowPreviewBO previewBO) {
        List<FlowVO> flowVOList = this.previewFlow(previewBO);
        FlowVO flowVO = CollUtil.getFirst(flowVOList);
        if (ObjectUtil.isNotNull(flowVO)) {
            return flowService.getByFlowId(flowVO.getFlowId(), previewBO.getVersion());
        }
        return null;
    }
}
