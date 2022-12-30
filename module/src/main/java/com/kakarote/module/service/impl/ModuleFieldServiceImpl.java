package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.common.ElasticUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.CustomCategoryField;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldSerialNumberRules;
import com.kakarote.module.entity.VO.ModuleFieldSortVO;
import com.kakarote.module.mapper.ModuleFieldMapper;
import com.kakarote.module.service.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 自定义字段表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
@Service
@Slf4j
public class ModuleFieldServiceImpl extends BaseServiceImpl<ModuleFieldMapper, ModuleField> implements IModuleFieldService {

	@Autowired
	private ElasticsearchRestTemplate restTemplate;

	@Autowired
	private IModuleFieldOptionsService fieldOptionsService;

    @Autowired
    private IModuleFieldTagsService fieldTagsService;

	@Autowired
	private IModuleFieldSerialNumberRulesService fieldSerialNumberRulesService;

    @Autowired
    private IModuleFieldFormulaService fieldFormulaService;

	@Autowired
	private IModuleFieldTreeService fieldTreeService;

	@Autowired
	private ICustomCategoryFieldService categoryFieldService;

	@Override
	public List<ModuleField> getByModuleIdAndVersion(Long moduleId, Integer version, Integer fieldType) {
		LambdaQueryWrapper<ModuleField> queryWrapper = new LambdaQueryWrapper();
		queryWrapper.eq(ModuleField::getModuleId, moduleId);
		queryWrapper.eq(ModuleField::getVersion, version);
		if (ObjectUtil.isNotNull(fieldType)) {
			queryWrapper.eq(ModuleField::getFieldType, fieldType);
		}
		queryWrapper.orderByDesc(ModuleField::getFieldType);
		queryWrapper.orderByAsc(ModuleField::getSorting);
		List<ModuleField> fields = list(queryWrapper);
		fields.forEach(f -> {
			f.setFormType(ModuleFieldEnum.parse(f.getType()).getFormType());
		});
		return fields;
	}

	@Override
	public List<ModuleField> getByFieldIds(Long moduleId, List<Long> fieldIds, Integer version) {
		if (CollUtil.isEmpty(fieldIds)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
				.eq(ModuleField::getModuleId, moduleId)
				.eq(ModuleField::getVersion, version)
				.in(ModuleField::getFieldId, fieldIds).list();
	}

	@Override
	public List<ModuleField> getByModuleId(Long moduleId, Integer fieldType) {
		ModuleEntity module = ModuleCacheUtil.getActiveById(moduleId);
		if (ObjectUtil.isNull(module)) {
			return Collections.emptyList();
		}
		return this.getByModuleIdAndVersion(moduleId, module.getVersion(), fieldType);
	}

	/**
	 * 查询自定义字段列表
	 *
	 * @param queryBO
	 * @return
	 */
	@Override
	public List<ModuleFieldBO> queryList(FieldQueryBO queryBO) {
		Long moduleId = queryBO.getModuleId();
		Integer version = queryBO.getVersion();
		Long categoryId = queryBO.getCategoryId();
		List<ModuleField> moduleFields = this.getByModuleIdAndVersion(moduleId, version, null);
		moduleFields = moduleFields.stream()
				.filter(f -> !CollUtil.contains(Arrays.asList("dataId", "moduleId"), f.getFieldName()))
				.collect(Collectors.toList());
		List<CustomCategoryField> categoryFields = categoryFieldService.getByCategoryIdAndVersion(categoryId, version);
		Map<Long, CustomCategoryField> fieldIdCategoryMap = categoryFields.stream().collect(Collectors.toMap(CustomCategoryField::getFieldId, Function.identity()));
		return moduleFields.stream().map(field -> {
			ModuleFieldBO fieldBO = BeanUtil.copyProperties(field, ModuleFieldBO.class);
			fieldBO.setFormType(ModuleFieldEnum.parse(fieldBO.getType()).getFormType());
			if (Arrays.asList(ModuleFieldEnum.SELECT.getType(), ModuleFieldEnum.CHECKBOX.getType()).contains(field.getType())) {
				List<ModuleOptionsBO> optionsBOS = fieldOptionsService.queryOptionsList(moduleId, field.getFieldId(), version);
				fieldBO.setOptionsList(optionsBOS);
			}
            if (ObjectUtil.equal(ModuleFieldEnum.TAG.getType(), field.getType())) {
                List<ModuleTagsBO> tagList = fieldTagsService.queryTagList(moduleId, field.getFieldId(), version);
                fieldBO.setTagList(tagList);
            }
			if (ObjectUtil.equal(ModuleFieldEnum.SERIAL_NUMBER.getType(), field.getType())) {
				List<ModuleFieldSerialNumberRules> ruleList = fieldSerialNumberRulesService.querySerialNumberRuleList(moduleId, field.getFieldId(), version);
				fieldBO.setSerialNumberRules(ruleList);
			}
            if (ObjectUtil.equal(ModuleFieldEnum.FORMULA.getType(), field.getType())) {
                ModuleFieldFormulaBO formulaBO = fieldFormulaService.queryFormulaList(moduleId, field.getFieldId(), version);
                fieldBO.setFormulaBO(formulaBO);
            }
			if (ObjectUtil.equal(ModuleFieldEnum.TREE.getType(), field.getType())) {
				List<ModuleTreeBO> treeList = fieldTreeService.queryTreeList(moduleId, field.getFieldId(), version);
				fieldBO.setTreeBO(treeList);
			}

			CustomCategoryField categoryField = fieldIdCategoryMap.get(fieldBO.getFieldId());
			if (ObjectUtil.isNotNull(categoryField)) {
				fieldBO.setName(categoryField.getName());
				fieldBO.setIsNull(categoryField.getIsNull());
				fieldBO.setIsHidden(categoryField.getIsHide());
			}
			return fieldBO;
		}).collect(Collectors.toList());
	}

	/**
	 * 查询自定义字段列表(二维数组)
	 *
	 * @param queryBO
	 * @return
	 */
	@Override
	public List<List<ModuleField>> formList(FieldFormQueryBO queryBO) {
		List<ModuleField> moduleFields = this.getByModuleIdAndVersion(queryBO.getModuleId(), queryBO.getVersion(), 1);
        if (queryBO.getFilterHidden()) {
            moduleFields = moduleFields.stream().filter(f -> ObjectUtil.equal(0, f.getIsHidden())).collect(Collectors.toList());
        }
		if (ObjectUtil.isNotNull(queryBO.getCategoryId())) {
			List<CustomCategoryField> categoryFields = categoryFieldService.getByCategoryIdAndVersion(queryBO.getCategoryId(), queryBO.getVersion());
			Map<Long, CustomCategoryField> fieldIdCategoryMap = categoryFields.stream().collect(Collectors.toMap(CustomCategoryField::getFieldId, Function.identity()));
			for (ModuleField moduleField : moduleFields) {
				CustomCategoryField categoryField = fieldIdCategoryMap.get(moduleField.getFieldId());
				if (ObjectUtil.isNotNull(categoryField)) {
					moduleField.setName(categoryField.getName());
					moduleField.setIsNull(categoryField.getIsNull());
					moduleField.setIsHidden(categoryField.getIsHide());
				}
			}
		}
		return this.convertFormPositionFieldList(moduleFields, ModuleField::getXAxis, ModuleField::getYAxis,
				ModuleField::getSorting);
	}

	@Override
	public List<ModuleField> queryDefaultField(Long moduleId, Integer version) {
		List<ModuleField> moduleFields = this.getByModuleIdAndVersion(moduleId, version, null)
				.stream().filter(f -> ObjectUtil.isNull(f.getGroupId())
						|| ObjectUtil.equal(ModuleFieldEnum.DETAIL_TABLE.getType(), f.getType()))
				.collect(Collectors.toList());
		return moduleFields;
	}

	/**
	 * 验证唯一字段是否存在
	 *
	 * @param verifyBO 字段信息
	 * @return data
	 */
	@Override
	public ModuleFieldVerifyBO verify(ModuleFieldVerifyBO verifyBO) {
        ModuleField field = this.getByFieldId(verifyBO.getModuleId(), verifyBO.getFieldId(), verifyBO.getVersion());
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		queryBuilder.filter(QueryBuilders.termQuery(StrUtil.toCamelCase(field.getFieldName()), verifyBO.getValue().trim()));
		if (StrUtil.isNotEmpty(verifyBO.getDataId())) {
			queryBuilder.mustNot(QueryBuilders.termQuery("dataId", verifyBO.getDataId()));
		}
		String indexName = ElasticUtil.getIndexName(field.getModuleId());
		NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withQuery(queryBuilder)
				.withSearchType(SearchType.DEFAULT)
				.build();
		long count = restTemplate.count(searchQuery, IndexCoordinates.of(indexName));
		if (count < 1) {
			verifyBO.setStatus(1);
		}
		return verifyBO;
	}

	@Override
	public <T> List<List<T>> convertFormPositionFieldList(List<T> fieldList, Function<T, Integer> groupMapper,
														  Function<T, Integer> sortMapper, Function<T, Integer> defaultSortMapper) {
		List<List<T>> list = new ArrayList<>();
		Map<Integer, List<T>> fieldGroupMap = fieldList.stream().collect(Collectors.groupingBy(groupMapper));
		if (fieldGroupMap.size() > 0) {
			Map<Integer, List<T>> resultMap = new LinkedHashMap<>();
			fieldGroupMap.entrySet().stream().sorted(Map.Entry.comparingByKey())
					.forEachOrdered(e -> resultMap.put(e.getKey(), e.getValue()));

			resultMap.forEach((key, value) -> {
				if (key != -1) {
					value.sort(Comparator.comparing(sortMapper));
					list.add(value);
				}
			});
			List<T> fields = resultMap.get(-1);
			if (CollUtil.isNotEmpty(fields)) {
				fields.sort(Comparator.comparing(defaultSortMapper));
				int size = fields.size();
				boolean isWithResidue = size % 2 != 0;
				List<T> temporaryList = new ArrayList<>();
				for (int i = 0; i < size; i++) {
					T crmField = fields.get(i);
					temporaryList.add(crmField);
					if (i % 2 == 1) {
						list.add(temporaryList);
						temporaryList = new ArrayList<>();
						continue;
					}
					if (isWithResidue && i == size - 1) {
						list.add(temporaryList);
					}
				}
			}
		}
		return list;
	}

	@Override
	public ModuleField getByFieldId(Long moduleId, Long fieldId, Integer version) {
		return lambdaQuery()
                .eq(ModuleField::getModuleId, moduleId)
                .eq(ModuleField::getFieldId, fieldId)
                .eq(ModuleField::getVersion, version).one();
	}

	@Override
	public ModuleField getByFieldName(Long moduleId, String fieldName) {
		ModuleEntity module = ModuleCacheUtil.getActiveById(moduleId);
		return lambdaQuery()
				.eq(ModuleField::getModuleId, moduleId)
				.eq(ModuleField::getFieldName, fieldName)
				.eq(ModuleField::getVersion, module.getVersion()).one();
	}

	@Override
	public List<ModuleField> getFieldByGroupId(Long moduleId, Integer groupId, Integer version) {
		return lambdaQuery()
				.eq(ModuleField::getModuleId, moduleId)
				.eq(ModuleField::getGroupId, groupId)
				.eq(ModuleField::getVersion, version).list();
	}

	@Override
	public ModuleField getDetailTableFieldByGroupId(Long moduleId, Integer groupId, Integer version) {
		return lambdaQuery()
				.eq(ModuleField::getModuleId, moduleId)
				.eq(ModuleField::getGroupId, groupId)
				.eq(ModuleField::getType, ModuleFieldEnum.DETAIL_TABLE.getType())
				.eq(ModuleField::getVersion, version).one();
	}

	@Override
	public List<ModuleFieldSortVO> queryExportHeadList(FieldQueryBO queryBO) {
		List<ModuleField> moduleFields = this.getByModuleIdAndVersion(queryBO.getModuleId(), queryBO.getVersion(), null).stream()
				.filter(f -> ObjectUtil.equal(0, f.getIsHidden()))
				.filter(f -> ObjectUtil.isNull(f.getGroupId()) || ObjectUtil.equal(ModuleFieldEnum.DETAIL_TABLE.getType(), f.getType()))
				.filter(f -> !CollUtil.contains(Arrays.asList("dataId", "moduleId"), f.getFieldName()))
				.collect(Collectors.toList());
		List<ModuleFieldSortVO> collect = moduleFields.stream().map(d -> {
			ModuleFieldSortVO vo = new ModuleFieldSortVO();
			vo.setFieldId(d.getFieldId());
			vo.setFieldName(d.getFieldName());
			vo.setName(d.getName());
			vo.setType(d.getType());
			vo.setIsHide(d.getIsHidden());
			vo.setIsNull(d.getIsNull());
			vo.setFormType(ModuleFieldEnum.parse(d.getType()).getFormType());
			return vo;
		}).collect(Collectors.toList());
		return collect;
	}
}
