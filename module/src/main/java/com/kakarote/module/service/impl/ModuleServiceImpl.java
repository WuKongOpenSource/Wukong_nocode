package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.entity.SimpleUser;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.result.Result;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.BaseUtil;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.service.UserService;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.common.ElasticUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.common.mq.ProducerUtil;
import com.kakarote.module.constant.MessageTagEnum;
import com.kakarote.module.constant.ModuleCodeEnum;
import com.kakarote.module.constant.ModuleType;
import com.kakarote.module.entity.BO.ModuleSaveBO;
import com.kakarote.module.entity.BO.MsgBodyBO;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.entity.VO.ModuleListVO;
import com.kakarote.module.mapper.ModuleMapper;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 应用模块表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
@Service
public class ModuleServiceImpl extends BaseServiceImpl<ModuleMapper, ModuleEntity> implements IModuleService, ModulePageService {

	@Autowired
	private ElasticsearchRestTemplate restTemplate;
	@Autowired
	private ProducerUtil producerUtil;

	@Autowired
	private IModuleFieldService fieldService;

	@Autowired
	private IModuleFieldConfigService fieldConfigService;

	@Autowired
	private IModuleStatusService statusService;

	@Autowired
	private UserService userService;

	@Override
	public List<ModuleEntity> getAll() {
		return lambdaQuery()
                .eq(ModuleEntity::getStatus, 1)
                .eq(ModuleEntity::getIsActive, true)
				.orderByDesc(ModuleEntity::getCreateTime)
                .list();
	}

	@Override
	public ModuleEntity getNormal(Long moduleId) {
		return lambdaQuery()
				.eq(ModuleEntity::getModuleId, moduleId)
				.eq(ModuleEntity::getStatus, 1)
				.one();
	}

	@Override
	public ModuleEntity getByModuleIdAndVersion(Long moduleId, Integer version) {
		return lambdaQuery()
				.eq(ModuleEntity::getModuleId, moduleId)
				.eq(ModuleEntity::getVersion, version).one();
	}

	@Override
	public void initES(ModuleEntity module) {
		// 自定义模块要初始化
		if (ObjectUtil.isNotNull(module.getModuleType()) && ObjectUtil.notEqual(1, module.getModuleType())) {
			return;
		}
		String indexName = ElasticUtil.getIndexName(module.getModuleId());
		// 新建模块时创建索引
		if (!ElasticUtil.indexExist(indexName)) {
			restTemplate.execute(client -> {
				ElasticUtil.createIndex(client, indexName);
				return Result.ok();
			});
			ElasticUtil.BaseField[] baseFields = new ElasticUtil.BaseField[]{
					new ElasticUtil.BaseField("dataId", 3),
					new ElasticUtil.BaseField("ownerUserName", 1),
					new ElasticUtil.BaseField("ownerUserId", 1),
					new ElasticUtil.BaseField("createUserId", 1),
					new ElasticUtil.BaseField("createUserName", 1),
					new ElasticUtil.BaseField("updateTime", 5),
					new ElasticUtil.BaseField("createTime", 5),
					new ElasticUtil.BaseField("remarks", 1),
					new ElasticUtil.BaseField("teamMember", 1),
					new ElasticUtil.BaseField("type", 1),
					new ElasticUtil.BaseField("currentFlowId", 1),
					new ElasticUtil.BaseField("flowType", 1),
					new ElasticUtil.BaseField("flowStatus", 1),
					new ElasticUtil.BaseField("moduleId", 1),
					new ElasticUtil.BaseField("categoryId", 1),
					new ElasticUtil.BaseField("stageId", 1),
					new ElasticUtil.BaseField("stageName", 1),
					new ElasticUtil.BaseField("stageStatus", 1),
					new ElasticUtil.BaseField("batchId", 1),
			};
			restTemplate.execute(client -> {
				ElasticUtil.addField(client, module.getModuleId(), baseFields);
				return Result.ok();
			});
		}
	}

	@Override
	public void addESFields(List<ModuleField> fields, Long moduleId) {
		List<ModuleField> fieldList = fields.stream().filter(f -> ObjectUtil.equal(1, f.getFieldType())).collect(Collectors.toList());
		List<ElasticUtil.BaseField> toInitESFieldConfig = new ArrayList<>();
		for (ModuleField field : fieldList) {
			String fieldName = field.getFieldName();
			int type = ElasticUtil.parseType(field.getType());
			// 待初始化的字段配置
			ModuleFieldConfig fieldConfig = fieldConfigService.getByFieldNameAndType(fieldName, type);
			if (ObjectUtil.isNull(fieldConfig)) {
				fieldConfig = new ModuleFieldConfig();
				fieldConfig.setFieldType(type);
				fieldConfig.setFieldName(fieldName);
				fieldConfig.setCreateTime(DateTime.now());
				fieldConfigService.save(fieldConfig);
				toInitESFieldConfig.add(new ElasticUtil.BaseField(fieldName, type));
			}
		}
		restTemplate.execute(client -> {
			ElasticUtil.addField(client, moduleId, toInitESFieldConfig.toArray(new ElasticUtil.BaseField[toInitESFieldConfig.size()]));
			return Result.ok();
		});
	}

	@Override
	public ModuleSaveBO queryDetail(Long moduleId, Integer version) {
		ModuleEntity module = getByModuleIdAndVersion(moduleId, version);
        if (ObjectUtil.isNull(module)) {
            throw new BusinessException(ModuleCodeEnum.MODULE_NOT_FOUND);
        }
		ModuleSaveBO result = BeanUtil.copyProperties(module, ModuleSaveBO.class);
		List<Long> userIds = JSON.parseArray(module.getManageUserId(), Long.class);
		List<SimpleUser> users = UserCacheUtil.getSimpleUsers(userIds);
		if (ObjectUtil.isNotNull(module.getMainFieldId())) {
			ModuleField mainField = fieldService.getByFieldId(module.getModuleId(), module.getMainFieldId(), version);
			result.setMainFieldName(mainField.getFieldName());
		}
		result.setManageUsers(users);
		return result;
	}

	@Override
	public ModuleSaveBO queryById(Long moduleId, Boolean isLatest) {
		ModuleEntity latestModule = getBaseMapper().getLatestModule(moduleId);
		if (ObjectUtil.isNull(latestModule)) {
			return null;
		}
		ModuleSaveBO result;
		if (isLatest) {
			result = BeanUtil.copyProperties(latestModule, ModuleSaveBO.class);
		} else {
			if (ObjectUtil.equal(1, latestModule.getStatus())) {
				result = BeanUtil.copyProperties(latestModule, ModuleSaveBO.class);
			} else {
				ModuleEntity module = ModuleCacheUtil.getByIdAndVersion(moduleId, latestModule.getVersion() - 1);
				result = BeanUtil.copyProperties(module, ModuleSaveBO.class);
			}
		}
		if (ObjectUtil.isNotNull(result.getMainFieldId())) {
			ModuleField mainField = fieldService.getByFieldId(moduleId, result.getMainFieldId(), result.getVersion());
			result.setMainFieldName(mainField.getFieldName());
		}
		return result;
	}

	/**
	 * 查询应用下模块列表
	 *
	 * @param applicationId 应用ID
	 * @return 模块列表
	 */
	@Override
	public List<ModuleListVO> queryModuleList(Long applicationId) {
		List<ModuleEntity> modules = lambdaQuery()
				.eq(ModuleEntity::getApplicationId, applicationId)
				.in(ModuleEntity::getStatus, 1, 2)
				.orderByAsc(ModuleEntity::getSort)
				.orderByAsc(ModuleEntity::getVersion)
				.list();
		Set<Long> moduleIds = modules.stream().map(ModuleEntity::getModuleId).collect(Collectors.toSet());
		List<ModuleStatus> moduleStatuses = statusService.listByModuleId(moduleIds);
		Map<Long, ModuleStatus> moduleStatusMap = moduleStatuses.stream().collect(Collectors.toMap(ModuleStatus::getModuleId, Function.identity()));
		Map<Long, List<ModuleListVO>> idVOMap = new HashMap<>(16);
		for (ModuleEntity module : modules) {
			ModuleListVO vo = BeanUtil.copyProperties(module, ModuleListVO.class);
			ModuleStatus status = moduleStatusMap.get(module.getModuleId());
			if (ObjectUtil.isNotNull(status)) {
				vo.setIsEnable(status.getIsEnable());
			}
			vo.setCreateUserName(UserCacheUtil.getUserName(vo.getCreateUserId()));
			List<ModuleListVO> listVOS = idVOMap.get(module.getModuleId());
			if (ObjectUtil.isNull(listVOS)) {
				listVOS = new ArrayList<>();
			}
			listVOS.add(vo);
			idVOMap.put(module.getModuleId(), listVOS);
		}
		List<ModuleListVO> vos = new ArrayList<>();
		for (Map.Entry<Long, List<ModuleListVO>> entry : idVOMap.entrySet()) {
			vos.addAll(entry.getValue());
		}
		return vos;
	}

	/**
	 * 删除模块
	 *
	 * @param ids ids
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteModule(List<Long> ids) {
		if (CollUtil.isEmpty(ids)) {
			return;
		}
		// 删除模块
		lambdaUpdate().in(ModuleEntity::getModuleId, ids).remove();

		// 删除BI
		ApplicationContextHolder.getBean(IBiDashboardService.class)
				.lambdaUpdate().in(BiDashboard::getModuleId, ids)
				.remove();

		ApplicationContextHolder.getBean(IBiDashboardUserService.class)
				.lambdaUpdate().in(BiDashboardUser::getModuleId, ids)
				.remove();

		ApplicationContextHolder.getBean(IBiElementService.class)
				.lambdaUpdate().in(BiElement::getModuleId, ids)
				.remove();

		ApplicationContextHolder.getBean(IBiElementFieldService.class)
				.lambdaUpdate().in(BiElementField::getModuleId, ids)
				.remove();

		//删除模块下自定义字段
		ApplicationContextHolder.getBean(IModuleFieldService.class)
				.lambdaUpdate().in(ModuleField::getModuleId, ids)
				.remove();

		//删除模块下自定义字段排序
		ApplicationContextHolder.getBean(IModuleFieldSortService.class)
				.lambdaUpdate().in(ModuleFieldSort::getModuleId, ids)
				.remove();

		//删除数据
		ApplicationContextHolder.getBean(IModuleFieldDataService.class)
				.lambdaUpdate().in(ModuleFieldData::getModuleId, ids)
				.remove();
		//删除通用字段值
		ApplicationContextHolder.getBean(IModuleFieldDataCommonService.class)
				.lambdaUpdate().in(ModuleFieldDataCommon::getModuleId, ids).remove();
		//删除默认值
		ApplicationContextHolder.getBean(IModuleFieldDefaultService.class)
				.lambdaUpdate().in(ModuleFieldDefault::getModuleId, ids).remove();
		//删除选项
		ApplicationContextHolder.getBean(IModuleFieldOptionsService.class)
				.lambdaUpdate().in(ModuleFieldOptions::getModuleId, ids).remove();
		// 删除自定义标签字段
		ApplicationContextHolder.getBean(IModuleFieldTagsService.class)
						.lambdaUpdate().in(ModuleFieldTags::getModuleId, ids).remove();
		// 删除树字段配置
		ApplicationContextHolder.getBean(IModuleFieldTreeService.class)
				.lambdaUpdate().in(ModuleFieldTree::getModuleId, ids).remove();
		// 删除自定义编码字段规则
		ApplicationContextHolder.getBean(IModuleFieldSerialNumberRulesService.class)
				.lambdaUpdate().in(ModuleFieldSerialNumberRules::getModuleId, ids).remove();

		// 删除打印模板
		ApplicationContextHolder.getBean(IModulePrintTemplateService.class)
				.lambdaUpdate().in(ModulePrintTemplate::getModuleId, ids).remove();

		//删除布局
		ApplicationContextHolder.getBean(IModuleLayoutService.class)
				.lambdaUpdate().in(ModuleLayout::getModuleId, ids).remove();
		//删除角色
		ApplicationContextHolder.getBean(IModuleRoleFieldService.class)
				.lambdaUpdate().in(ModuleRoleField::getModuleId, ids).remove();
		ApplicationContextHolder.getBean(IModuleRoleModuleService.class)
				.lambdaUpdate().in(ModuleRoleModule::getModuleId, ids).remove();
		//删除场景
		ApplicationContextHolder.getBean(IModuleSceneService.class)
				.lambdaUpdate().in(ModuleScene::getModuleId, ids).remove();
		//删除数据关联字段
		ApplicationContextHolder.getBean(IModuleFieldUnionService.class)
				.lambdaUpdate().in(ModuleFieldUnion::getModuleId, ids).remove();
		ApplicationContextHolder.getBean(IModuleFieldUnionConditionService.class)
				.lambdaUpdate().in(ModuleFieldUnionCondition::getModuleId, ids).remove();
		//删除统计字段
		ApplicationContextHolder.getBean(IModuleStatisticFieldUnionService.class)
				.lambdaUpdate().in(ModuleStatisticFieldUnion::getModuleId, ids).remove();

		//删除节点
		ApplicationContextHolder.getBean(IFlowMetadataService.class)
				.lambdaUpdate()
				.in(FlowMetadata::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(IFlowService.class)
				.lambdaUpdate()
				.in(Flow::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(IFlowConditionService.class)
				.lambdaUpdate()
				.in(FlowCondition::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(IFlowConditionDataService.class)
				.lambdaUpdate()
				.in(FlowConditionData::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(IFlowCopyService.class)
				.lambdaUpdate()
				.in(FlowCopy::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(IFlowSaveService.class)
				.lambdaUpdate()
				.in(FlowSave::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(IFlowUpdateService.class)
				.lambdaUpdate()
				.in(FlowUpdate::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(IFlowTimeLimitService.class)
				.lambdaUpdate()
				.in(FlowTimeLimit::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(IFlowExamineOptionalService.class)
				.lambdaUpdate()
				.in(FlowExamineOptional::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(IFlowExamineContinuousSuperiorService.class)
				.lambdaUpdate()
				.in(FlowExamineContinuousSuperior::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(IFlowExamineMemberService.class)
				.lambdaUpdate()
				.in(FlowExamineMember::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(IFlowExamineRoleService.class)
				.lambdaUpdate()
				.in(FlowExamineRole::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(IFlowExamineSuperiorService.class)
				.lambdaUpdate()
				.in(FlowExamineSuperior::getModuleId, ids)
				.remove();
		// 删除自定义按钮
		ApplicationContextHolder.getBean(ICustomButtonService.class)
				.lambdaUpdate()
				.in(CustomButton::getModuleId, ids)
				.remove();
		// 删除自定义提醒
		ApplicationContextHolder.getBean(ICustomNoticeService.class)
				.lambdaUpdate()
				.in(CustomNotice::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(ICustomNoticeReceiverService.class)
				.lambdaUpdate()
				.in(CustomNoticeReceiver::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(ICustomNoticeRecordService.class)
				.lambdaUpdate()
				.in(CustomNoticeRecord::getModuleId, ids)
				.remove();
		// 删除数据提交校验
		ApplicationContextHolder.getBean(IModuleDataCheckRuleService.class)
				.lambdaUpdate()
				.in(ModuleDataCheckRule::getModuleId, ids)
				.remove();
		// 删除阶段流程
		ApplicationContextHolder.getBean(IStageSettingService.class)
				.lambdaUpdate()
				.in(StageSetting::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(IStageService.class)
				.lambdaUpdate()
				.in(Stage::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(IStageTaskService.class)
				.lambdaUpdate()
				.in(StageTask::getModuleId, ids)
				.remove();
		// 删除自定义模块分类
		ApplicationContextHolder.getBean(ICustomCategoryService.class)
				.lambdaUpdate()
				.in(CustomCategory::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(ICustomCategoryRuleService.class)
				.lambdaUpdate()
				.in(CustomCategoryRule::getModuleId, ids)
				.remove();
		ApplicationContextHolder.getBean(ICustomCategoryFieldService.class)
				.lambdaUpdate()
				.in(CustomCategoryField::getModuleId, ids)
				.remove();
		// 通知MQ
		MsgBodyBO msgBody = new MsgBodyBO();
		msgBody.setMsgTag(MessageTagEnum.DELETE_MODULE);
		msgBody.setMsgKey(IdUtil.simpleUUID());
		msgBody.setModuleIds(ids);
		msgBody.setUserId(UserUtil.getUserId());
		producerUtil.sendMsgToTopicOne(msgBody);
	}

	@Override
	public Map<String, Object> queryModuleConfig(Long moduleId) {
		// 模块
		ModuleEntity module = ModuleCacheUtil.getActiveById(moduleId);
		if (ObjectUtil.isNull(module)) {
			return null;
		}
        Map<String, Object> data = new HashMap<>(16);
        data.put("module", module);
        // BI
        if (ObjectUtil.equal(ModuleType.BI.getType(), module.getModuleType())) {
            List<BiDashboard> dashboards = ApplicationContextHolder.getBean(IBiDashboardService.class).lambdaQuery().eq(BiDashboard::getModuleId, moduleId).list();
            List<BiDashboardUser> dashboardUsers = ApplicationContextHolder.getBean(IBiDashboardUserService.class).lambdaQuery().eq(BiDashboardUser::getModuleId, moduleId).list();
            List<BiElement> elements = ApplicationContextHolder.getBean(IBiElementService.class).lambdaQuery().eq(BiElement::getModuleId, moduleId).list();
            List<BiElementField> elementFields = ApplicationContextHolder.getBean(IBiElementFieldService.class).lambdaQuery().eq(BiElementField::getModuleId, moduleId).list();
            data.put("dashboards", dashboards);
            data.put("dashboardUsers", dashboardUsers);
            data.put("elements", elements);
            data.put("elementFields", elementFields);
        } else if (ObjectUtil.equal(ModuleType.MODULE.getType(), module.getModuleType())) {
            Integer version = module.getVersion();
            // 字段
            List<ModuleField> fields = ApplicationContextHolder.getBean(IModuleFieldService.class).getByModuleId(moduleId, null);
            // 字段默认值
            List<ModuleFieldDefault> fieldDefaults = ApplicationContextHolder.getBean(IModuleFieldDefaultService.class).getByModuleIdAndVersion(moduleId, version);
            // 计算公式
            List<ModuleFieldFormula> fieldFormulas = ApplicationContextHolder.getBean(IModuleFieldFormulaService.class).getByModuleIdAndVersion(moduleId, version);
            // 字段选项
            List<ModuleFieldOptions> fieldOptions = ApplicationContextHolder.getBean(IModuleFieldOptionsService.class).getByModuleIdAndVersion(moduleId, version);
            // 自定义标签
            List<ModuleFieldTags> fieldTags = ApplicationContextHolder.getBean(IModuleFieldTagsService.class).getByModuleIdAndVersion(moduleId, version);
			// 树字段
			List<ModuleFieldTree> fieldTrees = ApplicationContextHolder.getBean(IModuleFieldTreeService.class).getByModuleIdAndVersion(moduleId, version);
			// 自定义编码
            List<ModuleFieldSerialNumberRules> fieldSerialNumberRules = ApplicationContextHolder.getBean(IModuleFieldSerialNumberRulesService.class).getByModuleIdAndVersion(moduleId, version);
            // 布局
            ModuleLayout layout = ApplicationContextHolder.getBean(IModuleLayoutService.class).getByModuleIdAndVersion(moduleId, version);
            // 数据关联
            List<ModuleFieldUnion> fieldUnions = ApplicationContextHolder.getBean(IModuleFieldUnionService.class).getByModuleIdAndVersion(moduleId, version);
            List<ModuleFieldUnionCondition> fieldUnionConditions = ApplicationContextHolder.getBean(IModuleFieldUnionConditionService.class).getByModuleIdAndVersion(moduleId, version);
            // 统计字段
            List<ModuleStatisticFieldUnion> statisticFieldUnions = ApplicationContextHolder.getBean(IModuleStatisticFieldUnionService.class).getByModuleIdAndVersion(moduleId, version);
            // 节点
            List<FlowMetadata> flowMetadatas = ApplicationContextHolder.getBean(IFlowMetadataService.class).getByModuleIdAndVersion(moduleId, version);
            List<Flow> flows = ApplicationContextHolder.getBean(IFlowService.class).getByModuleIdAndVersion(moduleId, version);
            List<FlowCondition> flowConditions = ApplicationContextHolder.getBean(IFlowConditionService.class)
                    .lambdaQuery()
                    .eq(FlowCondition::getModuleId, moduleId)
                    .eq(FlowCondition::getVersion, version)
                    .list();
            List<FlowConditionData> flowConditionData = ApplicationContextHolder.getBean(IFlowConditionDataService.class)
                    .lambdaQuery()
                    .eq(FlowConditionData::getModuleId, moduleId)
                    .eq(FlowConditionData::getVersion, version)
                    .list();
            List<FlowCopy> flowCopies = ApplicationContextHolder.getBean(IFlowCopyService.class)
                    .lambdaQuery()
                    .eq(FlowCopy::getModuleId, moduleId)
                    .eq(FlowCopy::getVersion, version)
                    .list();
            List<FlowSave> flowSaves = ApplicationContextHolder.getBean(IFlowSaveService.class)
                    .lambdaQuery()
                    .eq(FlowSave::getModuleId, moduleId)
                    .eq(FlowSave::getVersion, version)
                    .list();
            List<FlowUpdate> flowUpdates = ApplicationContextHolder.getBean(IFlowUpdateService.class)
                    .lambdaQuery()
                    .eq(FlowUpdate::getModuleId, moduleId)
                    .eq(FlowUpdate::getVersion, version)
                    .list();
			List<FlowFieldAuth> flowFieldAuths = ApplicationContextHolder.getBean(IFlowFieldAuthService.class)
					.lambdaQuery()
					.eq(FlowFieldAuth::getModuleId, moduleId)
					.eq(FlowFieldAuth::getVersion, version)
					.list();
            List<FlowTimeLimit> flowTimeLimits = ApplicationContextHolder.getBean(IFlowTimeLimitService.class)
                    .lambdaQuery()
                    .eq(FlowTimeLimit::getModuleId, moduleId)
                    .eq(FlowTimeLimit::getVersion, version)
                    .list();
            List<FlowExamineOptional> flowExamineOptionals = ApplicationContextHolder.getBean(IFlowExamineOptionalService.class)
                    .lambdaQuery()
                    .eq(FlowExamineOptional::getModuleId, moduleId)
                    .eq(FlowExamineOptional::getVersion, version)
                    .list();
            List<FlowExamineContinuousSuperior> flowExamineContinuousSuperiors = ApplicationContextHolder.getBean(IFlowExamineContinuousSuperiorService.class)
                    .lambdaQuery()
                    .eq(FlowExamineContinuousSuperior::getModuleId, moduleId)
                    .eq(FlowExamineContinuousSuperior::getVersion, version)
                    .list();
            List<FlowExamineMember> flowExamineMembers = ApplicationContextHolder.getBean(IFlowExamineMemberService.class)
                    .lambdaQuery()
                    .eq(FlowExamineMember::getModuleId, moduleId)
                    .eq(FlowExamineMember::getVersion, version)
                    .list();
            List<FlowExamineRole> flowExamineRoles = ApplicationContextHolder.getBean(IFlowExamineRoleService.class)
                    .lambdaQuery()
                    .eq(FlowExamineRole::getModuleId, moduleId)
                    .eq(FlowExamineRole::getVersion, version)
                    .list();
            List<FlowExamineSuperior> flowExamineSuperiors = ApplicationContextHolder.getBean(IFlowExamineSuperiorService.class)
                    .lambdaQuery()
                    .eq(FlowExamineSuperior::getModuleId, moduleId)
                    .eq(FlowExamineSuperior::getVersion, version)
                    .list();
            // 自定义按钮
            List<CustomButton> customButtons = ApplicationContextHolder.getBean(ICustomButtonService.class).getByModuleIdAndVersion(moduleId, version);
            // 自定义提醒
            List<CustomNotice> customNotices = ApplicationContextHolder.getBean(ICustomNoticeService.class).getByModuleIdAndVersion(moduleId, version);
            List<CustomNoticeReceiver> noticeReceivers = ApplicationContextHolder.getBean(ICustomNoticeReceiverService.class).getByModuleIdAndVersion(moduleId, version);
            // 数据提交校验
            List<ModuleDataCheckRule> dataCheckRules = ApplicationContextHolder.getBean(IModuleDataCheckRuleService.class).getByModuleIdAndVersion(moduleId, version);
            // 阶段流程
            List<StageSetting> stageSettings = ApplicationContextHolder.getBean(IStageSettingService.class).getByModuleIdAndVersion(moduleId, version);
            List<Stage> stages = ApplicationContextHolder.getBean(IStageService.class).getByModuleIdAndVersion(moduleId, version);
            List<StageTask> stageTasks = ApplicationContextHolder.getBean(IStageTaskService.class).getByModuleIdAndVersion(moduleId, version);
            // 模块分类
            List<CustomCategory> categories = ApplicationContextHolder.getBean(ICustomCategoryService.class).getByModuleIdAndVersion(moduleId, version);
            List<CustomCategoryRule> categoryRules = ApplicationContextHolder.getBean(ICustomCategoryRuleService.class).getByModuleIdAndVersion(moduleId, version);
            List<CustomCategoryField> categoryFields = ApplicationContextHolder.getBean(ICustomCategoryFieldService.class).getByModuleIdAndVersion(moduleId, version);
            data.put("fields", fields);
            data.put("fieldDefaults", fieldDefaults);
            data.put("fieldFormulas", fieldFormulas);
            data.put("fieldOptions", fieldOptions);
            data.put("fieldTags", fieldTags);
            data.put("fieldTrees", fieldTrees);
            data.put("fieldSerialNumberRules", fieldSerialNumberRules);
            data.put("layout", layout);
            data.put("fieldUnions", fieldUnions);
            data.put("fieldUnionConditions", fieldUnionConditions);
            data.put("statisticFieldUnions", statisticFieldUnions);
            data.put("flowMetadatas", flowMetadatas);
            data.put("flows", flows);
            data.put("flowConditions", flowConditions);
            data.put("flowConditionData", flowConditionData);
            data.put("flowCopies", flowCopies);
            data.put("flowSaves", flowSaves);
            data.put("flowUpdates", flowUpdates);
            data.put("flowFieldAuths", flowFieldAuths);
            data.put("flowTimeLimits", flowTimeLimits);
            data.put("flowExamineOptionals", flowExamineOptionals);
            data.put("flowExamineContinuousSuperiors", flowExamineContinuousSuperiors);
            data.put("flowExamineMembers", flowExamineMembers);
            data.put("flowExamineRoles", flowExamineRoles);
            data.put("flowExamineSuperiors", flowExamineSuperiors);
            data.put("customButtons", customButtons);
            data.put("customNotices", customNotices);
            data.put("noticeReceivers", noticeReceivers);
            data.put("dataCheckRules", dataCheckRules);
            data.put("stageSettings", stageSettings);
            data.put("stages", stages);
            data.put("stageTasks", stageTasks);
            data.put("categories", categories);
            data.put("categoryRules", categoryRules);
            data.put("categoryFields", categoryFields);
        }
		return data;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void applyModule(Map<String, Object> moduleData, Map<Long, Long> oldNewModuleIdMap) {
		// region BI
		List<BiDashboard> dashboards = JSON.parseArray(MapUtil.getStr(moduleData, "dashboards"), BiDashboard.class);
		List<BiDashboardUser> dashboardUsers = JSON.parseArray(MapUtil.getStr(moduleData, "dashboardUsers"), BiDashboardUser.class);
		List<BiElement> elements = JSON.parseArray(MapUtil.getStr(moduleData, "elements"), BiElement.class);
		List<BiElementField> elementFields = JSON.parseArray(MapUtil.getStr(moduleData, "elementFields"), BiElementField.class);
		if (CollUtil.isNotEmpty(dashboards)) {
			for (BiDashboard dashboard : dashboards) {
				dashboard.setModuleId(oldNewModuleIdMap.get(dashboard.getModuleId()));
				dashboard.setCreateTime(DateUtil.date());
				dashboard.setUpdateTime(DateUtil.date());
				dashboard.setCreateUserId(UserUtil.getUserId());
			}
			ApplicationContextHolder.getBean(IBiDashboardService.class).saveBatch(dashboards);
		}
		if (CollUtil.isNotEmpty(dashboardUsers)) {
			for (BiDashboardUser dashboardUser : dashboardUsers) {
				dashboardUser.setId(null);
				dashboardUser.setModuleId(oldNewModuleIdMap.get(dashboardUser.getModuleId()));
				dashboardUser.setCreateTime(DateUtil.date());
				dashboardUser.setUpdateTime(DateUtil.date());
				dashboardUser.setCreateUserId(UserUtil.getUserId());
			}
			ApplicationContextHolder.getBean(IBiDashboardUserService.class).saveBatch(dashboardUsers);
		}
		// 新旧组件ID对应关系 旧-新
		Map<Long, Long> oldNewElementIdMap = new HashMap<>(16);
		if (CollUtil.isNotEmpty(elements)) {
			for (BiElement element : elements) {
				Long newElementId = BaseUtil.getNextId();
				oldNewElementIdMap.put(element.getElementId(), newElementId);
				element.setElementId(newElementId);
				element.setModuleId(oldNewModuleIdMap.get(element.getModuleId()));
				element.setCreateTime(DateUtil.date());
				element.setUpdateTime(DateUtil.date());
				element.setCreateUserId(UserUtil.getUserId());
			}
			ApplicationContextHolder.getBean(IBiElementService.class).saveBatch(elements);
		}
		if (CollUtil.isNotEmpty(elementFields)) {
			for (BiElementField elementField : elementFields) {
				elementField.setFieldId(null);
				elementField.setElementId(oldNewElementIdMap.get(elementField.getElementId()));
				elementField.setModuleId(oldNewModuleIdMap.get(elementField.getModuleId()));
				elementField.setCreateTime(DateUtil.date());
				elementField.setUpdateTime(DateUtil.date());
				elementField.setCreateUserId(UserUtil.getUserId());
			}
			ApplicationContextHolder.getBean(IBiElementFieldService.class).saveBatch(elementFields);
		}
		// endregion
		// region 保存字段
		// 字段
		List<ModuleField> fields = JSON.parseArray(MapUtil.getStr(moduleData, "fields"), ModuleField.class);
		int i = 0;
		if (CollUtil.isNotEmpty(fields)) {
			for (ModuleField field : fields) {
				field.setId(null);
				field.setModuleId(oldNewModuleIdMap.get(field.getModuleId()));
				field.setUpdateTime(DateUtil.date());
				field.setVersion(0);
				field.setSorting(i++);
			}
			ApplicationContextHolder.getBean(IModuleFieldService.class).saveBatch(fields);
		}
		// 字段默认值
		List<ModuleFieldDefault> fieldDefaults = JSON.parseArray(MapUtil.getStr(moduleData, "fieldDefaults"), ModuleFieldDefault.class);
		if (CollUtil.isNotEmpty(fieldDefaults)) {
			for (ModuleFieldDefault fieldDefault : fieldDefaults) {
				fieldDefault.setId(null);
				fieldDefault.setModuleId(oldNewModuleIdMap.get(fieldDefault.getModuleId()));
				fieldDefault.setVersion(0);
				fieldDefault.setTargetModuleId(Optional.ofNullable(oldNewModuleIdMap.get(fieldDefault.getTargetModuleId())).orElse(0L));
				String formula = fieldDefault.getFormula();
				if (StrUtil.isNotEmpty(formula)) {
					for (Map.Entry<Long, Long> entry : oldNewModuleIdMap.entrySet()) {
						Long oldModuleId = entry.getKey();
						Long newModuleId = entry.getValue();
						formula = formula.replace(String.valueOf(oldModuleId), String.valueOf(newModuleId));
					}
					fieldDefault.setFormula(formula);
				}
				String key = fieldDefault.getKey();
				if (StrUtil.isNotEmpty(key)) {
					for (Map.Entry<Long, Long> entry : oldNewModuleIdMap.entrySet()) {
						Long oldModuleId = entry.getKey();
						Long newModuleId = entry.getValue();
						key = key.replace(String.valueOf(oldModuleId), String.valueOf(newModuleId));
					}
					fieldDefault.setKey(key);
				}
			}
			ApplicationContextHolder.getBean(IModuleFieldDefaultService.class).saveBatch(fieldDefaults);
		}
		// 计算公式字段
		List<ModuleFieldFormula> fieldFormulas = JSON.parseArray(MapUtil.getStr(moduleData, "fieldFormulas"), ModuleFieldFormula.class);
		if (CollUtil.isNotEmpty(fieldFormulas)) {
			for (ModuleFieldFormula fieldFormula : fieldFormulas) {
				fieldFormula.setId(null);
				fieldFormula.setModuleId(oldNewModuleIdMap.get(fieldFormula.getModuleId()));
				fieldFormula.setVersion(0);
				String formula = fieldFormula.getFormula();
				if (StrUtil.isNotEmpty(formula)) {
					for (Map.Entry<Long, Long> entry : oldNewModuleIdMap.entrySet()) {
						Long oldModuleId = entry.getKey();
						Long newModuleId = entry.getValue();
						formula = formula.replace(String.valueOf(oldModuleId), String.valueOf(newModuleId));
					}
					fieldFormula.setFormula(formula);
				}
			}
			ApplicationContextHolder.getBean(IModuleFieldFormulaService.class).saveBatch(fieldFormulas);
		}
		// 字段选项
		List<ModuleFieldOptions> fieldOptionsList = JSON.parseArray(MapUtil.getStr(moduleData, "fieldOptions"), ModuleFieldOptions.class);
		if (CollUtil.isNotEmpty(fieldOptionsList)) {
			for (ModuleFieldOptions fieldOptions : fieldOptionsList) {
				fieldOptions.setId(null);
				fieldOptions.setModuleId(oldNewModuleIdMap.get(fieldOptions.getModuleId()));
				fieldOptions.setVersion(0);
				String key = fieldOptions.getKey();
				for (Map.Entry<Long, Long> entry : oldNewModuleIdMap.entrySet()) {
					Long oldModuleId = entry.getKey();
					Long newModuleId = entry.getValue();
					key = key.replace(String.valueOf(oldModuleId), String.valueOf(newModuleId));
				}
				fieldOptions.setKey(key);
			}
			ApplicationContextHolder.getBean(IModuleFieldOptionsService.class).saveBatch(fieldOptionsList);
		}
		// 自定义标签
		List<ModuleFieldTags> fieldTags = JSON.parseArray(MapUtil.getStr(moduleData, "fieldTags"), ModuleFieldTags.class);
		if (CollUtil.isNotEmpty(fieldTags)) {
			for (ModuleFieldTags fieldTag : fieldTags) {
				fieldTag.setId(null);
				fieldTag.setModuleId(oldNewModuleIdMap.get(fieldTag.getModuleId()));
				fieldTag.setVersion(0);
				String key = fieldTag.getKey();
				for (Map.Entry<Long, Long> entry : oldNewModuleIdMap.entrySet()) {
					Long oldModuleId = entry.getKey();
					Long newModuleId = entry.getValue();
					key = key.replace(String.valueOf(oldModuleId), String.valueOf(newModuleId));
				}
				fieldTag.setKey(key);
			}
			ApplicationContextHolder.getBean(IModuleFieldTagsService.class).saveBatch(fieldTags);
		}
		// 自定义树
		List<ModuleFieldTree> fieldTrees = JSON.parseArray(MapUtil.getStr(moduleData, "fieldTrees"), ModuleFieldTree.class);
		if (CollUtil.isNotEmpty(fieldTrees)) {
			for (ModuleFieldTree fieldTree : fieldTrees) {
				fieldTree.setId(null);
				fieldTree.setModuleId(oldNewModuleIdMap.get(fieldTree.getModuleId()));
				fieldTree.setVersion(0);
			}
			ApplicationContextHolder.getBean(IModuleFieldTreeService.class).saveBatch(fieldTrees);
		}
		// 自定义编码
		List<ModuleFieldSerialNumberRules> fieldSerialNumberRules = JSON.parseArray(MapUtil.getStr(moduleData, "fieldSerialNumberRules"), ModuleFieldSerialNumberRules.class);
		if (CollUtil.isNotEmpty(fieldSerialNumberRules)) {
			for (ModuleFieldSerialNumberRules serialNumberRules : fieldSerialNumberRules) {
				serialNumberRules.setId(null);
				serialNumberRules.setModuleId(oldNewModuleIdMap.get(serialNumberRules.getModuleId()));
				serialNumberRules.setVersion(0);
			}
			ApplicationContextHolder.getBean(IModuleFieldSerialNumberRulesService.class).saveBatch(fieldSerialNumberRules);
		}
		// 布局
		ModuleLayout layout = JSON.parseObject(MapUtil.getStr(moduleData, "layout"), ModuleLayout.class);
		if (ObjectUtil.isNotNull(layout)) {
			layout.setId(null);
			layout.setModuleId(oldNewModuleIdMap.get(layout.getModuleId()));
			layout.setVersion(0);
			layout.setCreateUserId(UserUtil.getUserId());
			layout.setUpdateUserId(UserUtil.getUserId());
			layout.setCreateTime(DateUtil.date());
			layout.setUpdateTime(DateUtil.date());
			ApplicationContextHolder.getBean(IModuleLayoutService.class).save(layout);
		}
		// 数据关联
		List<ModuleFieldUnion> fieldUnions = JSON.parseArray(MapUtil.getStr(moduleData, "fieldUnions"), ModuleFieldUnion.class);
		List<ModuleFieldUnionCondition> fieldUnionConditions = JSON.parseArray(MapUtil.getStr(moduleData, "fieldUnionConditions"), ModuleFieldUnionCondition.class);
		if (CollUtil.isNotEmpty(fieldUnions)) {
			for (ModuleFieldUnion fieldUnion : fieldUnions) {
				fieldUnion.setId(null);
				fieldUnion.setModuleId(oldNewModuleIdMap.get(fieldUnion.getModuleId()));
				fieldUnion.setVersion(0);
				fieldUnion.setTargetModuleId(Optional.ofNullable(oldNewModuleIdMap.get(fieldUnion.getTargetModuleId())).orElse(0L));
				fieldUnion.setCreateUserId(UserUtil.getUserId());
				fieldUnion.setCreateTime(DateUtil.date());
			}
			ApplicationContextHolder.getBean(IModuleFieldUnionService.class).saveBatch(fieldUnions);
		}
		if (CollUtil.isNotEmpty(fieldUnionConditions)) {
			for (ModuleFieldUnionCondition condition : fieldUnionConditions) {
				condition.setId(null);
				condition.setModuleId(oldNewModuleIdMap.get(condition.getModuleId()));
				condition.setVersion(0);
				condition.setTargetModuleId(Optional.ofNullable(oldNewModuleIdMap.get(condition.getTargetModuleId())).orElse(0L));
				condition.setCreateUserId(UserUtil.getUserId());
				condition.setCreateTime(DateUtil.date());
			}
			ApplicationContextHolder.getBean(IModuleFieldUnionConditionService.class).saveBatch(fieldUnionConditions);
		}
		// 统计字段
		List<ModuleStatisticFieldUnion> statisticFieldUnions = JSON.parseArray(MapUtil.getStr(moduleData, "statisticFieldUnions"), ModuleStatisticFieldUnion.class);
		if (CollUtil.isNotEmpty(statisticFieldUnions)) {
			for (ModuleStatisticFieldUnion statisticFieldUnion : statisticFieldUnions) {
				statisticFieldUnion.setId(null);
				statisticFieldUnion.setModuleId(oldNewModuleIdMap.get(statisticFieldUnion.getModuleId()));
				statisticFieldUnion.setVersion(0);
				statisticFieldUnion.setTargetModuleId(Optional.ofNullable(oldNewModuleIdMap.get(statisticFieldUnion.getTargetModuleId())).orElse(0L));
				statisticFieldUnion.setCreateUserId(UserUtil.getUserId());
				statisticFieldUnion.setCreateTime(DateUtil.date());
			}
			ApplicationContextHolder.getBean(IModuleStatisticFieldUnionService.class).saveBatch(statisticFieldUnions);
		}
		// endregion
		// region 节点
		List<FlowMetadata> flowMetadatas = JSON.parseArray(MapUtil.getStr(moduleData, "flowMetadatas"), FlowMetadata.class);
		List<Flow> flows = JSON.parseArray(MapUtil.getStr(moduleData, "flows"), Flow.class);
		List<FlowCondition> flowConditions = JSON.parseArray(MapUtil.getStr(moduleData, "flowConditions"), FlowCondition.class);
		List<FlowConditionData> flowConditionData = JSON.parseArray(MapUtil.getStr(moduleData, "flowConditionData"), FlowConditionData.class);
		List<FlowCopy> flowCopies = JSON.parseArray(MapUtil.getStr(moduleData, "flowCopies"), FlowCopy.class);
		List<FlowSave> flowSaves = JSON.parseArray(MapUtil.getStr(moduleData, "flowSaves"), FlowSave.class);
		List<FlowUpdate> flowUpdates = JSON.parseArray(MapUtil.getStr(moduleData, "flowUpdates"), FlowUpdate.class);
		List<FlowFieldAuth> flowFieldAuths = JSON.parseArray(MapUtil.getStr(moduleData, "flowFieldAuths"), FlowFieldAuth.class);
		List<FlowTimeLimit> flowTimeLimits = JSON.parseArray(MapUtil.getStr(moduleData, "flowTimeLimits"), FlowTimeLimit.class);
		List<FlowExamineOptional> flowExamineOptionals = JSON.parseArray(MapUtil.getStr(moduleData, "flowExamineOptionals"), FlowExamineOptional.class);
		List<FlowExamineContinuousSuperior> flowExamineContinuousSuperiors = JSON.parseArray(MapUtil.getStr(moduleData, "flowExamineContinuousSuperiors"), FlowExamineContinuousSuperior.class);
		List<FlowExamineMember> flowExamineMembers = JSON.parseArray(MapUtil.getStr(moduleData, "flowExamineMembers"), FlowExamineMember.class);
		List<FlowExamineRole> flowExamineRoles = JSON.parseArray(MapUtil.getStr(moduleData, "flowExamineRoles"), FlowExamineRole.class);
		List<FlowExamineSuperior> flowExamineSuperiors = JSON.parseArray(MapUtil.getStr(moduleData, "flowExamineSuperiors"), FlowExamineSuperior.class);
		Map<Long, FlowMetadata> oldNewFlowMetaDataIdMap = new HashMap<>(16);
		if (CollUtil.isNotEmpty(flowMetadatas)) {
			for (FlowMetadata flowMetadata : flowMetadatas) {
				Long newFlowMetadataId = BaseUtil.getNextId();
				Long oldFlowMetadataId = flowMetadata.getMetadataId();

				flowMetadata.setMetadataId(newFlowMetadataId);
				flowMetadata.setBatchId(IdUtil.simpleUUID());
				flowMetadata.setModuleId(oldNewModuleIdMap.get(flowMetadata.getModuleId()));
				flowMetadata.setVersion(0);
				flowMetadata.setCreateUserId(UserUtil.getUserId());
				flowMetadata.setCreateTime(DateUtil.date());

				oldNewFlowMetaDataIdMap.put(oldFlowMetadataId, flowMetadata);
			}
			ApplicationContextHolder.getBean(IFlowMetadataService.class).saveBatch(flowMetadatas);
		}
		// 新旧节点ID对应关系 旧-新
		Map<Long, Long> oldNewFlowIdMap = new HashMap<>(16);
		if (CollUtil.isNotEmpty(flows)) {
			for (Flow flow : flows) {
				Long newFlowId = BaseUtil.getNextId();
				oldNewFlowIdMap.put(flow.getFlowId(), newFlowId);
				flow.setId(null);
				flow.setFlowId(newFlowId);
				FlowMetadata flowMetadata = oldNewFlowMetaDataIdMap.get(flow.getFlowMetadataId());
				flow.setFlowMetadataId(flowMetadata.getMetadataId());
				flow.setBatchId(flowMetadata.getBatchId());
				flow.setModuleId(oldNewModuleIdMap.get(flow.getModuleId()));
				flow.setVersion(0);
				flow.setCreateTime(DateUtil.date());
			}
			ApplicationContextHolder.getBean(IFlowService.class).saveBatch(flows);
		}
		// 新旧节点条件ID对应关系 旧-新
		Map<Long, Long> oldNewConditionIdMap = new HashMap<>(16);
		if (CollUtil.isNotEmpty(flowConditions)) {
			for (FlowCondition condition : flowConditions) {
				Long newFlowId = MapUtil.getLong(oldNewFlowIdMap, condition.getFlowId());
				Long newConditionId = MapUtil.getLong(oldNewConditionIdMap, condition.getConditionId());
				if (ObjectUtil.isNull(newConditionId)) {
					newConditionId = BaseUtil.getNextId();
					oldNewConditionIdMap.put(condition.getConditionId(), newConditionId);
				}
				condition.setId(null);
				condition.setConditionId(newConditionId);
				condition.setFlowId(newFlowId);
				FlowMetadata flowMetadata = oldNewFlowMetaDataIdMap.get(condition.getFlowMetadataId());
				condition.setFlowMetadataId(flowMetadata.getMetadataId());
				condition.setBatchId(flowMetadata.getBatchId());
				condition.setModuleId(oldNewModuleIdMap.get(condition.getModuleId()));
				condition.setVersion(0);
				condition.setCreateTime(DateUtil.date());
				condition.setCreateUserId(UserUtil.getUserId());
			}
			ApplicationContextHolder.getBean(IFlowConditionService.class).saveBatch(flowConditions);
		}
		if (CollUtil.isNotEmpty(flowConditionData)) {
			for (FlowConditionData conditionData : flowConditionData) {
				Long typeId = conditionData.getTypeId();
				Long newTypeId = MapUtil.getLong(oldNewFlowIdMap, typeId);
				if (ObjectUtil.isNull(newTypeId)) {
					newTypeId = MapUtil.getLong(oldNewConditionIdMap, typeId);
				}
				FlowMetadata flowMetadata = oldNewFlowMetaDataIdMap.get(conditionData.getFlowMetadataId());
				conditionData.setId(null);
				conditionData.setTypeId(newTypeId);
				conditionData.setFlowMetadataId(flowMetadata.getMetadataId());
				conditionData.setBatchId(flowMetadata.getBatchId());
				conditionData.setModuleId(oldNewModuleIdMap.get(conditionData.getModuleId()));
				conditionData.setTargetModuleId(Optional.ofNullable(oldNewModuleIdMap.get(conditionData.getTargetModuleId())).orElse(0L));
				conditionData.setVersion(0);
				conditionData.setCreateTime(DateUtil.date());
				conditionData.setCreateUserId(UserUtil.getUserId());
				if (ObjectUtil.equal(1, conditionData.getModel())) {
					String search = conditionData.getSearch();
					for (Map.Entry<Long, Long> entry : oldNewModuleIdMap.entrySet()) {
						Long oldModuleId = entry.getKey();
						Long newModuleId = entry.getValue();
						search = search.replace(String.valueOf(oldModuleId), String.valueOf(newModuleId));
					}
					conditionData.setSearch(search);
				}
			}
			ApplicationContextHolder.getBean(IFlowConditionDataService.class).saveBatch(flowConditionData);
		}
		if (CollUtil.isNotEmpty(flowCopies)) {
			for (FlowCopy flowCopy : flowCopies) {
				Long newFlowId = MapUtil.getLong(oldNewFlowIdMap, flowCopy.getFlowId());
				FlowMetadata flowMetadata = oldNewFlowMetaDataIdMap.get(flowCopy.getFlowMetadataId());

				flowCopy.setId(null);
				flowCopy.setFlowId(newFlowId);
				flowCopy.setModuleId(oldNewModuleIdMap.get(flowCopy.getModuleId()));
				flowCopy.setBatchId(flowMetadata.getBatchId());
				flowCopy.setFlowMetadataId(flowMetadata.getMetadataId());
				flowCopy.setVersion(0);
				flowCopy.setCreateTime(DateUtil.date());
			}
			ApplicationContextHolder.getBean(IFlowCopyService.class).saveBatch(flowCopies);
		}
		if (CollUtil.isNotEmpty(flowSaves)) {
			for (FlowSave flowSave : flowSaves) {
				Long newFlowId = MapUtil.getLong(oldNewFlowIdMap, flowSave.getFlowId());
				FlowMetadata flowMetadata = oldNewFlowMetaDataIdMap.get(flowSave.getFlowMetadataId());

				flowSave.setId(null);
				flowSave.setFlowId(newFlowId);
				flowSave.setModuleId(oldNewModuleIdMap.get(flowSave.getModuleId()));
				flowSave.setTargetModuleId(Optional.ofNullable(oldNewModuleIdMap.get(flowSave.getTargetModuleId())).orElse(0L));
				flowSave.setBatchId(flowMetadata.getBatchId());
				flowSave.setFlowMetadataId(flowMetadata.getMetadataId());
				flowSave.setVersion(0);
				flowSave.setCreateTime(DateUtil.date());
				flowSave.setOwnerUserId(0L);
			}
			ApplicationContextHolder.getBean(IFlowSaveService.class).saveBatch(flowSaves);
		}
		if (CollUtil.isNotEmpty(flowUpdates)) {
			for (FlowUpdate flowUpdate : flowUpdates) {
				Long newFlowId = MapUtil.getLong(oldNewFlowIdMap, flowUpdate.getFlowId());
				FlowMetadata flowMetadata = oldNewFlowMetaDataIdMap.get(flowUpdate.getFlowMetadataId());

				flowUpdate.setId(null);
				flowUpdate.setFlowId(newFlowId);
				flowUpdate.setModuleId(oldNewModuleIdMap.get(flowUpdate.getModuleId()));
				flowUpdate.setTargetModuleId(Optional.ofNullable(oldNewModuleIdMap.get(flowUpdate.getTargetModuleId())).orElse(0L));
				flowUpdate.setBatchId(flowMetadata.getBatchId());
				flowUpdate.setFlowMetadataId(flowMetadata.getMetadataId());
				flowUpdate.setVersion(0);
				flowUpdate.setCreateTime(DateUtil.date());
			}
			ApplicationContextHolder.getBean(IFlowUpdateService.class).saveBatch(flowUpdates);
		}
		if (CollUtil.isNotEmpty(flowFieldAuths)) {
			for (FlowFieldAuth flowFieldAuth : flowFieldAuths) {
				Long newFlowId = MapUtil.getLong(oldNewFlowIdMap, flowFieldAuth.getFlowId());
				FlowMetadata flowMetadata = oldNewFlowMetaDataIdMap.get(flowFieldAuth.getFlowMetadataId());

				flowFieldAuth.setId(null);
				flowFieldAuth.setFlowId(newFlowId);
				flowFieldAuth.setModuleId(oldNewModuleIdMap.get(flowFieldAuth.getModuleId()));
				flowFieldAuth.setBatchId(flowMetadata.getBatchId());
				flowFieldAuth.setFlowMetadataId(flowMetadata.getMetadataId());
				flowFieldAuth.setVersion(0);
				flowFieldAuth.setCreateTime(DateUtil.date());
			}
			ApplicationContextHolder.getBean(IFlowUpdateService.class).saveBatch(flowUpdates);
		}
		if (CollUtil.isNotEmpty(flowTimeLimits)) {
			for (FlowTimeLimit flowTimeLimit : flowTimeLimits) {
				Long newFlowId = MapUtil.getLong(oldNewFlowIdMap, flowTimeLimit.getFlowId());
				FlowMetadata flowMetadata = oldNewFlowMetaDataIdMap.get(flowTimeLimit.getFlowMetadataId());

				flowTimeLimit.setId(null);
				flowTimeLimit.setFlowId(newFlowId);
				flowTimeLimit.setModuleId(oldNewModuleIdMap.get(flowTimeLimit.getModuleId()));
				flowTimeLimit.setBatchId(flowMetadata.getBatchId());
				flowTimeLimit.setTransferUserIds(JSON.toJSONString(Collections.emptyList()));
				flowTimeLimit.setUserIds(JSON.toJSONString(Collections.emptyList()));
				flowTimeLimit.setFlowMetadataId(flowMetadata.getMetadataId());
				flowTimeLimit.setVersion(0);
				flowTimeLimit.setCreateTime(DateUtil.date());
			}
			ApplicationContextHolder.getBean(IFlowTimeLimitService.class).saveBatch(flowTimeLimits);
		}
		if (CollUtil.isNotEmpty(flowExamineOptionals)) {
			for (FlowExamineOptional examineOptional : flowExamineOptionals) {
				Long newFlowId = MapUtil.getLong(oldNewFlowIdMap, examineOptional.getFlowId());
				FlowMetadata flowMetadata = oldNewFlowMetaDataIdMap.get(examineOptional.getFlowMetadataId());

				examineOptional.setId(null);
				examineOptional.setFlowId(newFlowId);
				examineOptional.setModuleId(oldNewModuleIdMap.get(examineOptional.getModuleId()));
				examineOptional.setBatchId(flowMetadata.getBatchId());
				examineOptional.setFlowMetadataId(flowMetadata.getMetadataId());
				examineOptional.setVersion(0);
				if (ObjectUtil.isNotNull(examineOptional.getUserId())) {
					examineOptional.setUserId(userService.querySuperUserId().getData());
				}
				if (ObjectUtil.isNotNull(examineOptional.getRoleId())) {
//					examineOptional.setRoleId(UserUtil.getSuperRole());
				}
			}
			ApplicationContextHolder.getBean(IFlowExamineOptionalService.class).saveBatch(flowExamineOptionals);
		}
		if (CollUtil.isNotEmpty(flowExamineContinuousSuperiors)) {
			for (FlowExamineContinuousSuperior continuousSuperior : flowExamineContinuousSuperiors) {
				Long newFlowId = MapUtil.getLong(oldNewFlowIdMap, continuousSuperior.getFlowId());
				FlowMetadata flowMetadata = oldNewFlowMetaDataIdMap.get(continuousSuperior.getFlowMetadataId());

				continuousSuperior.setId(null);
				continuousSuperior.setFlowId(newFlowId);
				continuousSuperior.setModuleId(oldNewModuleIdMap.get(continuousSuperior.getModuleId()));
				continuousSuperior.setBatchId(flowMetadata.getBatchId());
				continuousSuperior.setFlowMetadataId(flowMetadata.getMetadataId());
				continuousSuperior.setVersion(0);
				if (ObjectUtil.isNotNull(continuousSuperior.getRoleId())) {
//					continuousSuperior.setRoleId(UserUtil.getSuperRole()); TODO:
				}
			}
			ApplicationContextHolder.getBean(IFlowExamineContinuousSuperiorService.class).saveBatch(flowExamineContinuousSuperiors);
		}
		if (CollUtil.isNotEmpty(flowExamineMembers)) {
			for (FlowExamineMember examineMember : flowExamineMembers) {
				Long newFlowId = MapUtil.getLong(oldNewFlowIdMap, examineMember.getFlowId());
				FlowMetadata flowMetadata = oldNewFlowMetaDataIdMap.get(examineMember.getFlowMetadataId());

				examineMember.setId(null);
				examineMember.setFlowId(newFlowId);
				examineMember.setModuleId(oldNewModuleIdMap.get(examineMember.getModuleId()));
				examineMember.setBatchId(flowMetadata.getBatchId());
				examineMember.setFlowMetadataId(flowMetadata.getMetadataId());
				examineMember.setVersion(0);
				if (ObjectUtil.isNotNull(examineMember.getUserId())) {
					examineMember.setUserId(userService.querySuperUserId().getData());
				}
			}
			ApplicationContextHolder.getBean(IFlowExamineMemberService.class).saveBatch(flowExamineMembers);
		}
		if (CollUtil.isNotEmpty(flowExamineRoles)) {
			for (FlowExamineRole examineRole : flowExamineRoles) {
				Long newFlowId = MapUtil.getLong(oldNewFlowIdMap, examineRole.getFlowId());
				FlowMetadata flowMetadata = oldNewFlowMetaDataIdMap.get(examineRole.getFlowMetadataId());

				examineRole.setId(null);
				examineRole.setFlowId(newFlowId);
				examineRole.setModuleId(oldNewModuleIdMap.get(examineRole.getModuleId()));
				examineRole.setBatchId(flowMetadata.getBatchId());
				examineRole.setFlowMetadataId(flowMetadata.getMetadataId());
				examineRole.setVersion(0);
				if (ObjectUtil.isNotNull(examineRole.getRoleId())) {
//					examineRole.setRoleId(UserUtil.getSuperRole());
				}
			}
			ApplicationContextHolder.getBean(IFlowExamineRoleService.class).saveBatch(flowExamineRoles);
		}
		if (CollUtil.isNotEmpty(flowExamineSuperiors)) {
			for (FlowExamineSuperior examineSuperior : flowExamineSuperiors) {
				Long newFlowId = MapUtil.getLong(oldNewFlowIdMap, examineSuperior.getFlowId());
				FlowMetadata flowMetadata = oldNewFlowMetaDataIdMap.get(examineSuperior.getFlowMetadataId());

				examineSuperior.setId(null);
				examineSuperior.setFlowId(newFlowId);
				examineSuperior.setModuleId(oldNewModuleIdMap.get(examineSuperior.getModuleId()));
				examineSuperior.setBatchId(flowMetadata.getBatchId());
				examineSuperior.setFlowMetadataId(flowMetadata.getMetadataId());
				examineSuperior.setVersion(0);
			}
			ApplicationContextHolder.getBean(IFlowExamineSuperiorService.class).saveBatch(flowExamineSuperiors);
		}
		// endregion
		// region 拓展配置
		// 自定义按钮
		List<CustomButton> customButtons = JSON.parseArray(MapUtil.getStr(moduleData, "customButtons"), CustomButton.class);
		// 自定义提醒
		List<CustomNotice> customNotices = JSON.parseArray(MapUtil.getStr(moduleData, "customNotices"), CustomNotice.class);
		List<CustomNoticeReceiver> noticeReceivers = JSON.parseArray(MapUtil.getStr(moduleData, "noticeReceivers"), CustomNoticeReceiver.class);
		// 数据提交校验
		List<ModuleDataCheckRule> dataCheckRules = JSON.parseArray(MapUtil.getStr(moduleData, "dataCheckRules"), ModuleDataCheckRule.class);
		// 阶段流程
		List<StageSetting> stageSettings = JSON.parseArray(MapUtil.getStr(moduleData, "stageSettings"), StageSetting.class);
		List<Stage> stages = JSON.parseArray(MapUtil.getStr(moduleData, "stages"), Stage.class);
		List<StageTask> stageTasks = JSON.parseArray(MapUtil.getStr(moduleData, "stageTasks"), StageTask.class);
		// 模块分类
		List<CustomCategory> categories = JSON.parseArray(MapUtil.getStr(moduleData, "categories"), CustomCategory.class);
		List<CustomCategoryRule> categoryRules = JSON.parseArray(MapUtil.getStr(moduleData, "categoryRules"), CustomCategoryRule.class);
		List<CustomCategoryField> categoryFields = JSON.parseArray(MapUtil.getStr(moduleData, "categoryFields"), CustomCategoryField.class);
		if (CollUtil.isNotEmpty(customButtons)) {
			for (CustomButton customButton : customButtons) {
				customButton.setId(null);
				customButton.setModuleId(oldNewModuleIdMap.get(customButton.getModuleId()));
				customButton.setVersion(0);
				customButton.setCreateTime(DateUtil.date());
				customButton.setUpdateTime(DateUtil.date());
				customButton.setCreateUserId(UserUtil.getUserId());
				String effectConfig = customButton.getEffectConfig();
				if (StrUtil.isNotEmpty(effectConfig)) {
					for (Map.Entry<Long, Long> entry : oldNewModuleIdMap.entrySet()) {
						Long oldModuleId = entry.getKey();
						Long newModuleId = entry.getValue();
						effectConfig = effectConfig.replace(String.valueOf(oldModuleId), String.valueOf(newModuleId));
					}
					customButton.setEffectConfig(effectConfig);
				}
			}
			ApplicationContextHolder.getBean(ICustomButtonService.class).saveBatch(customButtons);
		}
		// 新旧消息通知ID对应关系 旧-新
		Map<Long, Long> oldNewNoticeIdMap = new HashMap<>(16);
		if (CollUtil.isNotEmpty(customNotices)) {
			for (CustomNotice customNotice : customNotices) {
				Long newNoticeId = BaseUtil.getNextId();
				oldNewNoticeIdMap.put(customNotice.getNoticeId(), newNoticeId);

				customNotice.setId(null);
				customNotice.setNoticeId(newNoticeId);
				customNotice.setModuleId(oldNewModuleIdMap.get(customNotice.getModuleId()));
				customNotice.setVersion(0);
				customNotice.setCreateTime(DateUtil.date());
				customNotice.setUpdateTime(DateUtil.date());
				customNotice.setCreateUserId(UserUtil.getUserId());
				String effectConfig = customNotice.getEffectConfig();
				if (StrUtil.isNotEmpty(effectConfig)) {
					for (Map.Entry<Long, Long> entry : oldNewModuleIdMap.entrySet()) {
						Long oldModuleId = entry.getKey();
						Long newModuleId = entry.getValue();
						effectConfig = effectConfig.replace(String.valueOf(oldModuleId), String.valueOf(newModuleId));
					}
					customNotice.setEffectConfig(effectConfig);
				}
			}
			ApplicationContextHolder.getBean(ICustomNoticeService.class).saveBatch(customNotices);
		}
		if (CollUtil.isNotEmpty(noticeReceivers)) {
			for (CustomNoticeReceiver receiver : noticeReceivers) {
				receiver.setId(null);
				receiver.setNoticeId(oldNewNoticeIdMap.get(receiver.getNoticeId()));
				receiver.setModuleId(oldNewModuleIdMap.get(receiver.getModuleId()));
				receiver.setVersion(0);
				receiver.setCreateTime(DateUtil.date());
				receiver.setUpdateTime(DateUtil.date());
				receiver.setCreateUserId(UserUtil.getUserId());
				receiver.setNoticeUser(null);
				receiver.setNoticeRole(null);
			}
			ApplicationContextHolder.getBean(ICustomNoticeReceiverService.class).saveBatch(noticeReceivers);
		}
		if (CollUtil.isNotEmpty(dataCheckRules)) {
			for (ModuleDataCheckRule checkRule : dataCheckRules) {
				checkRule.setId(null);
				checkRule.setModuleId(oldNewModuleIdMap.get(checkRule.getModuleId()));
				checkRule.setVersion(0);
				checkRule.setCreateTime(DateUtil.date());
				String formula = checkRule.getFormula();
				if (StrUtil.isNotEmpty(formula)) {
					for (Map.Entry<Long, Long> entry : oldNewModuleIdMap.entrySet()) {
						Long oldModuleId = entry.getKey();
						Long newModuleId = entry.getValue();
						formula = formula.replace(String.valueOf(oldModuleId), String.valueOf(newModuleId));
					}
					checkRule.setFormula(formula);
				}
			}
			ApplicationContextHolder.getBean(IModuleDataCheckRuleService.class).saveBatch(dataCheckRules);
		}
		if (CollUtil.isNotEmpty(stageSettings)) {
			for (StageSetting stageSetting : stageSettings) {
				stageSetting.setId(null);
				stageSetting.setModuleId(oldNewModuleIdMap.get(stageSetting.getModuleId()));
				stageSetting.setVersion(0);
				stageSetting.setCreateUserId(UserUtil.getUserId());
				stageSetting.setCreateTime(DateUtil.date());
				stageSetting.setDeptIds(JSON.toJSONString(Collections.emptyList()));
				stageSetting.setUserIds(JSON.toJSONString(Collections.emptyList()));
			}
			ApplicationContextHolder.getBean(IStageSettingService.class).saveBatch(stageSettings);
		}
		if (CollUtil.isNotEmpty(stages)) {
			for (Stage stage : stages) {
				stage.setId(null);
				stage.setModuleId(oldNewModuleIdMap.get(stage.getModuleId()));
				stage.setVersion(0);
				stage.setCreateUserId(UserUtil.getUserId());
				stage.setCreateTime(DateUtil.date());
			}
			ApplicationContextHolder.getBean(IStageService.class).saveBatch(stages);
		}
		if (CollUtil.isNotEmpty(stageTasks)) {
			for (StageTask task : stageTasks) {
				task.setId(null);
				task.setModuleId(oldNewModuleIdMap.get(task.getModuleId()));
				task.setVersion(0);
			}
			ApplicationContextHolder.getBean(IStageTaskService.class).saveBatch(stageTasks);
		}
		// 新旧分类ID对应关系 旧-新
		Map<Long, Long> oldNewCategoryIdMap = new HashMap<>(16);
		if (CollUtil.isNotEmpty(categories)) {
			for (CustomCategory category : categories) {
				Long newCategoryId = BaseUtil.getNextId();
				oldNewCategoryIdMap.put(category.getCategoryId(), newCategoryId);
				category.setId(null);
				category.setCategoryId(newCategoryId);
				category.setModuleId(oldNewModuleIdMap.get(category.getModuleId()));
				category.setVersion(0);
				category.setCreateTime(DateUtil.date());
			}
			ApplicationContextHolder.getBean(ICustomCategoryService.class).saveBatch(categories);
		}
		if (CollUtil.isNotEmpty(categoryRules)) {
			for (CustomCategoryRule rule : categoryRules) {
				rule.setId(null);
				rule.setCategoryId(oldNewCategoryIdMap.get(rule.getCategoryId()));
				rule.setModuleId(oldNewModuleIdMap.get(rule.getModuleId()));
				rule.setVersion(0);
				rule.setCreateTime(DateUtil.date());
				Long from = rule.getFrom();
				if (oldNewModuleIdMap.containsKey(from)) {
					rule.setFrom(oldNewModuleIdMap.get(from));
				}
				if (oldNewCategoryIdMap.containsKey(from)) {
					rule.setFrom(oldNewCategoryIdMap.get(from));
				}
				Long to = rule.getTo();
				if (oldNewModuleIdMap.containsKey(to)) {
					rule.setTo(oldNewModuleIdMap.get(to));
				}
				if (oldNewCategoryIdMap.containsKey(to)) {
					rule.setTo(oldNewCategoryIdMap.get(to));
				}
				String formula = rule.getFormula();
				if (StrUtil.isNotEmpty(formula)) {
					for (Map.Entry<Long, Long> entry : oldNewModuleIdMap.entrySet()) {
						Long oldModuleId = entry.getKey();
						Long newModuleId = entry.getValue();
						formula = formula.replace(String.valueOf(oldModuleId), String.valueOf(newModuleId));
					}
					rule.setFormula(formula);
				}
			}
			ApplicationContextHolder.getBean(ICustomCategoryRuleService.class).saveBatch(categoryRules);
		}
		if (CollUtil.isNotEmpty(categoryFields)) {
			for (CustomCategoryField categoryField : categoryFields) {
				categoryField.setId(null);
				categoryField.setCategoryId(oldNewCategoryIdMap.get(categoryField.getCategoryId()));
				categoryField.setModuleId(oldNewModuleIdMap.get(categoryField.getModuleId()));
				categoryField.setVersion(0);
				categoryField.setCreateTime(DateUtil.date());
			}
			ApplicationContextHolder.getBean(ICustomCategoryFieldService.class).saveBatch(categoryFields);
		}
		// endregion
	}

	@Override
    public List<ModuleEntity> getLatestModules(Long applicationId) {
        List<ModuleEntity> modules = baseMapper.getLatestModules(applicationId);
        return modules.stream().filter(m -> Arrays.asList(ModuleType.MODULE.getType(), ModuleType.INNER_MODULE.getType()).contains(m.getModuleType())).collect(Collectors.toList());
    }

	@Override
	public List<ModuleEntity> getBIModules(Long applicationId) {
		return lambdaQuery()
				.eq(ModuleEntity::getApplicationId, applicationId)
				.eq(ModuleEntity::getModuleType, ModuleType.BI.getType())
				.list();
	}

	@Override
	public List<ModuleSaveBO> getActiveModules(Long applicationId) {
        List<ModuleSaveBO> moduleSaveBOS = baseMapper.getActiveModules(applicationId);
        return moduleSaveBOS.stream().filter(m -> Arrays.asList(ModuleType.MODULE.getType(), ModuleType.INNER_MODULE.getType()).contains(m.getModuleType())).collect(Collectors.toList());
	}

	@Override
    public List<Long> getManagerUserIds(Long moduleId, Integer version) {
        ModuleEntity module = ModuleCacheUtil.getByIdAndVersion(moduleId, version);
        return JSON.parseArray(module.getManageUserId(), Long.class);
    }
}
