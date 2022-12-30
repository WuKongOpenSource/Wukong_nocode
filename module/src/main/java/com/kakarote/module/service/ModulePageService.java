package com.kakarote.module.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.kakarote.common.constant.Const;
import com.kakarote.common.entity.SimpleUser;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.service.UserService;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.constant.FieldSearchEnum;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.CommonESNestedBO;
import com.kakarote.module.common.*;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.entity.VO.ModuleSceneVO;
import com.kakarote.module.mapper.ModuleFieldDataMapper;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kakarote.common.servlet.ApplicationContextHolder.getBean;


/**
 * 通用模块
 *
 * @author zjj
 * @date 2021-05-13 09:57
 */
public interface ModulePageService {

	default void savePage(Long dataId, Long moduleId, Integer version) {
		List<ModuleFieldData> fieldDataList = getBean(ModuleFieldDataMapper.class).getByDataId(dataId);
		List<ModuleField> fields = queryDefaultField(moduleId, version);
		Map<String, Object> map = new HashMap<>();
		Map<Long, String> idValueMap = fieldDataList.stream()
				.filter(d -> ObjectUtil.isNotNull(d.getValue()))
				.collect(Collectors.toMap(ModuleFieldData::getFieldId, ModuleFieldData::getValue));
		fields.forEach(d -> {
			Long fieldId = d.getFieldId();
			String name = d.getFieldName();
			Integer type = d.getType();
			String value = idValueMap.get(fieldId);
			if (StrUtil.isEmpty(value)) {
				map.put(name, null);
				return;
			}
			if (ObjectUtil.equal(ModuleFieldEnum.DATE, ModuleFieldEnum.parse(type))) {
				map.put(name, value);
			} else if (ObjectUtil.equal(ModuleFieldEnum.DATETIME, ModuleFieldEnum.parse(type))) {
				map.put(name, DateUtil.formatDateTime(DateUtil.parse(value)));
			} else if (ObjectUtil.equal(ModuleFieldEnum.DATE_INTERVAL, ModuleFieldEnum.parse(type))) {
                map.put(name, JSONObject.parseObject(value));
            } else if (ObjectUtil.equal(ModuleFieldEnum.SELECT, ModuleFieldEnum.parse(type))) {
                map.put(name, JSON.parseObject(value, JSONObject.class));
                map.put(String.format("%sSize", name), 1);
            } else if (Arrays.asList(ModuleFieldEnum.CHECKBOX, ModuleFieldEnum.TAG).contains(ModuleFieldEnum.parse(type))) {
                List<JSONObject> jsonObjectList = JSON.parseArray(value, JSONObject.class);
                map.put(name, jsonObjectList);
                map.put(String.format("%sSize", name), jsonObjectList.size());
            } else if (ObjectUtil.equal(ModuleFieldEnum.AREA_POSITION, ModuleFieldEnum.parse(type))) {
                List<JSONObject> jsonObjectList = JSON.parseArray(value, JSONObject.class);
                map.put(name, jsonObjectList);
                map.put(String.format("%sSize", name), jsonObjectList.size());
            } else if (ObjectUtil.equal(ModuleFieldEnum.DETAIL_TABLE, ModuleFieldEnum.parse(type))) {
				List<JSONObject> jsonObjectList = JSON.parseArray(value, JSONObject.class);
				for (JSONObject object : jsonObjectList) {
					ModuleMapUtil.removeEmptyValue(object);
				}
				map.put(name, jsonObjectList);
			} else {
                map.put(name, value);
            }
        });
		ModuleFieldDataCommon dataCommon = getBean(IModuleFieldDataCommonService.class).getByDataId(dataId);
		map.putAll(BeanUtil.beanToMap(dataCommon));
		if (ObjectUtil.isNotEmpty(dataCommon.getTeamMember())) {
			map.put("teamMember", JSON.parseArray(dataCommon.getTeamMember(), Long.class));
		}
		map.put("moduleId", moduleId);
		map.put("dataId", dataId);
		map.put("updateTime", DateUtil.formatDateTime(dataCommon.getUpdateTime()));
		map.put("createTime", DateUtil.formatDateTime(dataCommon.getCreateTime()));
		String indexName = ElasticUtil.getIndexName(moduleId);
		UpdateRequest request = new UpdateRequest(indexName, dataId.toString());
		request.id(dataId.toString());
		request.doc(map);
		request.docAsUpsert(true);
		request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
		getRestTemplate().execute(client -> client.update(request, RequestOptions.DEFAULT));
	}

	default BasePage<Map<String, Object>> queryPageList(SearchBO searchBO, Long moduleId) {
		SearchRequest searchRequest = new SearchRequest(ElasticUtil.getIndexName(moduleId));
		searchRequest.source(createSourceBuilder(searchBO));
		SearchResponse searchResponse = getRestTemplate().execute(client -> client.search(searchRequest, RequestOptions.DEFAULT));
		List<Map<String, Object>> mapList = new ArrayList<>();
		SearchHit[] hits = searchResponse.getHits().getHits();
		for (SearchHit hit : hits) {
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			mapList.add(sourceAsMap);
		}
		BasePage<Map<String, Object>> basePage = new BasePage<>();
		basePage.setSize(searchBO.getLimit());
		basePage.setList(mapList);
		basePage.setTotal(searchResponse.getHits().getTotalHits().value);
		basePage.setCurrent(searchBO.getPage());
		return basePage;
	}

	default BasePage<Map<String, Object>> detailData(BasePage<Map<String, Object>> data, Long moduleId) {
		ModuleEntity currentModule = getBean(IModuleService.class).getNormal(moduleId);
		List<ModuleField> moduleFields = getBean(IModuleFieldService.class).getByModuleIdAndVersion(moduleId, currentModule.getVersion(), null);
		// 人员字段
		List<String> userFieldFieldNames = moduleFields.stream()
				.filter(f -> ObjectUtil.equal(ModuleFieldEnum.USER.getType(), f.getType()))
				.map(ModuleField::getFieldName).collect(Collectors.toList());
		// 部门字段
		List<String> structureFieldNames = moduleFields.stream()
				.filter(f -> ObjectUtil.equal(ModuleFieldEnum.STRUCTURE.getType(), f.getType()))
				.map(ModuleField::getFieldName).collect(Collectors.toList());
		// 数据关联字段
		List<ModuleField> unionFields = moduleFields.stream()
				.filter(f -> Arrays.asList(ModuleFieldEnum.DATA_UNION.getType(), ModuleFieldEnum.DATA_UNION_MULTI.getType()).contains(f.getType()))
				.collect(Collectors.toList());
		// 数据关联字段ID
		List<Long> unionFieldIds = unionFields.stream().map(ModuleField::getFieldId).collect(Collectors.toList());
		Map<String, ModuleField> unionFieldNameMap = unionFields.stream().collect(Collectors.toMap(ModuleField::getFieldName, Function.identity()));
		// 数据关联配置
		Map<Long, ModuleFieldUnion> relateFieldUnionMap = new HashMap<>();
		Map<Long, ModuleEntity> moduleIdMap = new HashMap<>();
		// 目标模块
		List<ModuleEntity> targetModules = new ArrayList<>();
		if (CollUtil.isNotEmpty(unionFields)) {
			List<ModuleFieldUnion> fieldUnions = getBean(IModuleFieldUnionService.class).lambdaQuery()
					.in(ModuleFieldUnion::getRelateFieldId, unionFieldIds)
					.eq(ModuleFieldUnion::getType, 1)
					.eq(ModuleFieldUnion::getVersion, currentModule.getVersion())
					.eq(ModuleFieldUnion::getModuleId, moduleId).list();
			relateFieldUnionMap = fieldUnions.stream().collect(Collectors.toMap(ModuleFieldUnion::getRelateFieldId, Function.identity()));
			Set<Long> targetModuleIds = fieldUnions.stream().map(ModuleFieldUnion::getTargetModuleId).collect(Collectors.toSet());
			targetModules = ModuleCacheUtil.getActiveByIds(targetModuleIds);
			moduleIdMap = targetModules.stream().collect(Collectors.toMap(ModuleEntity::getModuleId, Function.identity()));
		}
		// 掩码处理
		getBean(IModuleRoleFieldService.class).replaceMaskFieldValue(data.getList(), moduleId);

		Map<Long, List<Long>> moduleDataIdsMap = new HashMap<>();

		for (Map<String, Object> map : data.getList()) {
			for (String userFieldFieldName : userFieldFieldNames) {
				String value = MapUtil.getStr(map, userFieldFieldName);
				JSONArray array = new JSONArray();
				if (StrUtil.isNotEmpty(value)) {
					for (String userId : value.split(Const.SEPARATOR)) {
						SimpleUser user = UserCacheUtil.getSimpleUser(Long.valueOf(userId));
						array.add(user);
					}
					map.put(userFieldFieldName, array);
				}
			}
			for (String structureFieldName : structureFieldNames) {
				String value = MapUtil.getStr(map, structureFieldName);
				JSONArray array = new JSONArray();
				if (StrUtil.isNotEmpty(value)) {
					for (String deptId : value.split(Const.SEPARATOR)) {
						String deptName = UserCacheUtil.getDeptName(Long.valueOf(deptId));
						JSONObject dept = new JSONObject();
						dept.fluentPut("deptId", deptId).fluentPut("deptName", deptName);
						array.add(dept);
					}
					map.put(structureFieldName, array);
				}
			}
			for (Map.Entry<String, ModuleField> entry : unionFieldNameMap.entrySet()) {
				String valueStr = MapUtil.getStr(map, entry.getKey());
				if (ObjectUtil.isNotNull(valueStr)) {
					ModuleField field = unionFieldNameMap.get(entry.getKey());
					if (ObjectUtil.isNotNull(field)) {
						ModuleFieldUnion fieldUnion = relateFieldUnionMap.get(field.getFieldId());
						if (ObjectUtil.isNotNull(fieldUnion)) {
							List<Long> dataIds = moduleDataIdsMap.get(fieldUnion.getTargetModuleId());
							if (ObjectUtil.isNull(dataIds)) {
								dataIds = new ArrayList<>();
							}
							List<Long> values = Arrays.stream(valueStr.split(",")).mapToLong(Long::valueOf).boxed().collect(Collectors.toList());
							dataIds.addAll(values);
							moduleDataIdsMap.put(fieldUnion.getTargetModuleId(), dataIds);
						}
					}
				}
			}
			// 节点数据处理
			Long currentFlowId = MapUtil.getLong(map, "currentFlowId");
			Integer version = MapUtil.getInt(map, "version");
			if (ObjectUtil.isNotNull(currentFlowId)) {
				Flow flow = FlowCacheUtil.getByIdAndVersion(currentFlowId, version);
				if (ObjectUtil.isNotNull(flow)) {
					map.put("currentFlow", flow);
				}
			}
		}
		List<ModuleFieldData> mainFieldDataList = new ArrayList<>();
		for (ModuleEntity targetModule : targetModules) {
			List<Long> dataIds = moduleDataIdsMap.get(targetModule.getModuleId());
			if (CollUtil.isNotEmpty(dataIds)) {
				List<ModuleFieldData> fieldDataList = getBean(IModuleFieldDataService.class).lambdaQuery()
						.eq(ModuleFieldData::getModuleId, targetModule.getModuleId())
						.eq(ModuleFieldData::getFieldId, targetModule.getMainFieldId())
						.in(ModuleFieldData::getDataId, dataIds).list();
				mainFieldDataList.addAll(fieldDataList);
			}
		}
		Map<Long, ModuleFieldData> dataIdDataMap = mainFieldDataList.stream().collect(Collectors.toMap(ModuleFieldData::getDataId, Function.identity()));
		if (CollUtil.isNotEmpty(mainFieldDataList)) {
			for (Map<String, Object> map : data.getList()) {
				for (Map.Entry<String, ModuleField> entry : unionFieldNameMap.entrySet()) {
					String valueStr = MapUtil.getStr(map, entry.getKey());
					if (ObjectUtil.isNotNull(valueStr)) {
						List<Long> values = Arrays.stream(valueStr.split(Const.SEPARATOR)).mapToLong(Long::valueOf).boxed().collect(Collectors.toList());
						if (CollUtil.isNotEmpty(values)) {
							JSONObject jsonObject = new JSONObject();
							JSONArray array = new JSONArray();
							for (Long value : values) {
								ModuleFieldData fieldData = dataIdDataMap.get(value);
								if (ObjectUtil.isNotNull(fieldData)) {
									ModuleEntity module = moduleIdMap.get(fieldData.getModuleId());
									jsonObject.put("module", module);
									array.add(fieldData);
								}
							}
							// 如果找不到数据，
							if (array.isEmpty()) {
								map.put(entry.getKey(), valueStr);
							} else {
								jsonObject.put("fieldData", array);
								map.put(entry.getKey(), jsonObject.toJSONString());
							}
						}
					}
				}
			}
		}
		return data;
	}

	default BasePage<Map<String, Object>> queryPageList(SearchBO searchBO, Long moduleId, Boolean detailDataFlag) {
		searchBO.setModuleId(moduleId);
		BasePage<Map<String, Object>> result = queryPageList(searchBO, moduleId);
		if (detailDataFlag) {
			this.detailData(result, moduleId);
		}
		return result;
	}

	default BasePage<Map<String, Object>> queryByDataIds(Collection<Long> dataIds, Long moduleId) {
		SearchBO searchBO = new SearchBO();
		searchBO.setLimit(dataIds.size());
		searchBO.setPage(1);
		searchBO.setModuleId(moduleId);
		SearchEntityBO searchEntityBO = new SearchEntityBO();
		searchEntityBO.setSearchEnum(FieldSearchEnum.ID);
		// wwl 增加type的赋值
		searchEntityBO.setType(FieldSearchEnum.ID.getType());
		searchEntityBO.setValues(dataIds.stream().map(String::valueOf).collect(Collectors.toList()));
		searchBO.getSearchList().add(searchEntityBO);
		return queryPageList(searchBO, moduleId);
	}

	/**
	 * 默认的type对象，不准备使用，固定值
	 *
	 * @return doc
	 */
	default String getDocType() {
		return "_doc";
	}

	/**
	 * 获取Elasticsearch对象
	 *
	 * @return restTemplate
	 */
	default ElasticsearchRestTemplate getRestTemplate() {
		return getBean(ElasticsearchRestTemplate.class);
	}

	/**
	 * 查询的字段，以及排序
	 *
	 * @param searchBO searchData
	 * @return data
	 */
	default SearchSourceBuilder createSourceBuilder(SearchBO searchBO) {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		List<ModuleField> fields = getBean(IModuleFieldService.class).getByModuleId(searchBO.getModuleId(), null);
		Integer type = null;
		if (StrUtil.isNotEmpty(searchBO.getSortField())) {
			for (ModuleField field : fields) {
				if (Objects.equals(searchBO.getSortField(), StrUtil.toCamelCase(field.getFieldName()))) {
					type = field.getType();
				}
			}
			if (type != null) {
				if (!Arrays.asList(ModuleFieldEnum.DATE.getType(), ModuleFieldEnum.NUMBER.getType(), ModuleFieldEnum.FLOATNUMBER.getType(), ModuleFieldEnum.DATETIME.getType()).contains(type)) {
					searchBO.setSortField(searchBO.getSortField() + ".sort");
				}
			}
		}
		if (searchBO.getPageType().equals(1)) {
			// 设置起止和结束
			sourceBuilder.from((searchBO.getPage() - 1) * searchBO.getLimit());
		}
		if (ObjectUtil.isNotNull(searchBO.getSearchAfterKey())) {
			sourceBuilder.searchAfter(searchBO.getSearchAfterKey());
		}
		//设置查询条数
		sourceBuilder.size(searchBO.getLimit());
		if (type == null) {
			searchBO.setOrder(1).setSortField("updateTime");
		}
		if (CollUtil.isNotEmpty(searchBO.getFetchFieldNameList())) {
			sourceBuilder.fetchSource(searchBO.getFetchFieldNameList().toArray(new String[0]), null);
		} else {
            List<String> sourceFields = fields.stream().map(f -> StrUtil.toCamelCase(f.getFieldName())).collect(Collectors.toList());
            sourceFields.add("version");
            sourceBuilder.fetchSource(sourceFields.toArray(new String[0]), null);
		}
		sourceBuilder.sort(SortBuilders.fieldSort(searchBO.getSortField()).order(Objects.equals(2, searchBO.getOrder()) ? SortOrder.ASC : SortOrder.DESC));
		sourceBuilder.sort(SortBuilders.fieldSort("_id").order(SortOrder.DESC));
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		sourceBuilder.query(createQueryBuilder(searchBO));
		return sourceBuilder;
	}

	default List<ModuleField> queryDefaultField(Long moduleId, Integer version) {
		return getBean(IModuleFieldService.class).queryDefaultField(moduleId, version);
	}

	/**
	 * 拼接查询条件
	 *
	 * @param searchBO searchBO
	 * @return BoolQueryBuilder
	 */
	default BoolQueryBuilder createQueryBuilder(SearchBO searchBO) {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		queryBuilder.filter(getModuleQueryBuilders(searchBO.getModuleId()));
		sceneQuery(searchBO, queryBuilder);
		searchBO.getSearchList().forEach(search -> search(search, queryBuilder));
		if(searchBO.getAuthFilter()) {
			authQuery(searchBO, queryBuilder);
		}
		return queryBuilder;
	}

	default TermQueryBuilder getModuleQueryBuilders(Long moduleId){
		return QueryBuilders.termQuery("moduleId", moduleId);
	}

	default void sceneQuery(SearchBO searchBO, BoolQueryBuilder queryBuilder) {
		if (ObjectUtil.isNull(searchBO.getSceneId())) {
			return;
		}
		ModuleSceneVO sceneVO = getBean(IModuleSceneService.class).getBySceneId(searchBO.getSceneId());
		if (ObjectUtil.isNotNull(sceneVO)) {
			if (ObjectUtil.equal(0, sceneVO.getIsSystem())) {
				if (StrUtil.isNotEmpty(sceneVO.getData())) {
					List<SearchEntityBO> searchEntityBOS = JSON.parseArray(sceneVO.getData(), SearchEntityBO.class);
                    searchEntityBOS.forEach(e -> search(e, queryBuilder));
				}
			}
			// 我负责的
			else if (ObjectUtil.equal(2, sceneVO.getIsSystem())) {
				queryBuilder.filter(QueryBuilders.termQuery("ownerUserId", UserUtil.getUserId()));
			}
			// 我下属负责的
			else if (ObjectUtil.equal(3, sceneVO.getIsSystem())) {
				List<Long> longList = getBean(UserService.class).queryChildUserId(UserUtil.getUserId()).getData();
				queryBuilder.filter(QueryBuilders.termsQuery("ownerUserId", longList));
			}
		}
	}

	default void search(SearchEntityBO search, BoolQueryBuilder queryBuilder) {
		if (search.getSearchEnum() == FieldSearchEnum.ID) {
			queryBuilder.filter(QueryBuilders.idsQuery().addIds(search.getValues().toArray(new String[0])));
			return;
		}
		String formType = search.getFormType();
		ModuleFieldEnum fieldEnum = ModuleFieldEnum.parse(formType);
		switch (fieldEnum) {
			case TEXTAREA:
				search.setFieldName(search.getFieldName());
			case TEXT:
			case MOBILE:
			case EMAIL:
			case WEBSITE: {
				ElasticUtil.textSearch(search, queryBuilder);
				break;
			}
            case SELECT: {
                ElasticUtil.selectSearch(search, queryBuilder);
                break;
            }
			case BOOLEAN_VALUE: {
				boolean value = TypeUtils.castToBoolean(search.getValues().get(0));
				value = (search.getSearchEnum() == FieldSearchEnum.IS) == value;
				ModuleSimpleFieldBO detailTableField = search.getDetailTableField();
				if (ObjectUtil.isNotNull(search.getDetailTableField())) {
					String name = String.join(".", detailTableField.getFieldName(), search.getFieldName());
					if (value) {
						NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), QueryBuilders.termQuery(name, "1"), ScoreMode.None);
						queryBuilder.filter(nestedQueryBuilder);
					} else {
						BoolQueryBuilder builder = QueryBuilders.boolQuery();
						NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), QueryBuilders.termQuery(name, "0"), ScoreMode.None);
						builder.should(nestedQueryBuilder);
						NestedQueryBuilder existQuery = QueryBuilders.nestedQuery(detailTableField.getFieldName(), QueryBuilders.existsQuery(name), ScoreMode.None);
						builder.should(QueryBuilders.boolQuery().mustNot(existQuery));
						queryBuilder.filter(builder);
					}
				} else {
					if (value) {
						queryBuilder.filter(QueryBuilders.termQuery(search.getFieldName(), "1"));
					} else {
						BoolQueryBuilder builder = QueryBuilders.boolQuery();
						builder.should(QueryBuilders.termQuery(search.getFieldName(), "0"));
						builder.should(QueryBuilders.termQuery(search.getFieldName(), ""));
						builder.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(search.getFieldName())));
						queryBuilder.filter(builder);
					}
				}
				break;
			}
            case TAG:
            case CHECKBOX: {
				ElasticUtil.checkboxSearch(search, queryBuilder);
				break;
			}
			case NUMBER:
			case FLOATNUMBER:
			case PERCENT:
				ElasticUtil.numberSearch(search, queryBuilder);
				break;
			case DATE_INTERVAL:
                ElasticUtil.dateIntervalSearch(search, queryBuilder);
				break;
			case DATE:
			case DATETIME:
				ElasticUtil.dateSearch(search, queryBuilder, fieldEnum);
				break;
			case AREA_POSITION:
                ElasticUtil.addressSearch(search, queryBuilder);
                break;
			case CURRENT_POSITION:
				if (search.getSearchEnum() == FieldSearchEnum.IS) {
					search.setValues(Collections.singletonList("\"" + search.getValues().get(0) + "\""));
					search.setSearchEnum(FieldSearchEnum.CONTAINS);
				}
				if (search.getSearchEnum() == FieldSearchEnum.IS_NOT) {
					search.setValues(Collections.singletonList("\"" + search.getValues().get(0) + "\""));
					search.setSearchEnum(FieldSearchEnum.NOT_CONTAINS);
				}
				ElasticUtil.textSearch(search, queryBuilder);
				break;
			case USER:
			case SINGLE_USER:
			case STRUCTURE:
				ElasticUtil.userSearch(search, queryBuilder);
				break;
			default:
				ElasticUtil.textSearch(search, queryBuilder);
				break;
		}
	}

	default void authQuery(SearchBO searchBO, BoolQueryBuilder queryBuilder){
		if (UserUtil.isAdmin()) {
			return;
		}
		Set<Long> userIds = getBean(IModuleRoleService.class).queryViewableUserIds(searchBO.getModuleId());
		userIds.add(UserUtil.getUserId());
		BoolQueryBuilder authBoolQuery = QueryBuilders.boolQuery();
		if (CollUtil.isNotEmpty(userIds)) {
			authBoolQuery.should(QueryBuilders.termsQuery("ownerUserId", userIds));
		}
		authBoolQuery.should(QueryBuilders.termQuery("teamMember", UserUtil.getUserId()));
		queryBuilder.filter(authBoolQuery);
	}

	/**
	 * 根据ID列表删除数据
	 *
	 * @param dataIds  数据ID
	 * @param moduleId 模块ID
	 */
	default void deletePage(List<Long> dataIds, Long moduleId) {
		DeleteByQueryRequest query = new DeleteByQueryRequest(ElasticUtil.getIndexName(moduleId));
		query.setQuery(QueryBuilders.idsQuery().addIds(dataIds.stream().map(Object::toString).toArray(String[]::new)));
		getRestTemplate().execute(client -> client.deleteByQuery(query, RequestOptions.DEFAULT));

	}

	/**
	 * 更新字段值
	 *
	 * @param fieldValueMap 字段值
	 * @param dataId        数据ID
	 * @param moduleId      模块ID
	 */
	default void updateField(Map<String, Object> fieldValueMap, Long dataId, Long moduleId) {
		if (ObjectUtil.isNull(fieldValueMap) || ObjectUtil.isNull(dataId) || ObjectUtil.isNull(moduleId)) {
			return;
		}
		String index = ElasticUtil.getIndexName(moduleId);
		UpdateRequest request = new UpdateRequest(index, dataId.toString());
		request.doc(fieldValueMap);
		request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
		getRestTemplate().execute(client -> client.update(request, RequestOptions.DEFAULT));
	}

	default void updateField(Map<String, Object> map, List<Long> dataIds,  Long moduleId) {
		if (ObjectUtil.isNull(map) || CollUtil.isEmpty(dataIds) || ObjectUtil.isNull(moduleId)) {
			return;
		}
		BulkRequest bulkRequest = new BulkRequest();
		String index = ElasticUtil.getIndexName(moduleId);

		dataIds.forEach(id -> {
			UpdateRequest request = new UpdateRequest(index, id.toString());
			request.doc(map);
			bulkRequest.add(request);
		});
		bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
		getRestTemplate().execute(client -> client.bulk(bulkRequest, RequestOptions.DEFAULT));
	}

	/**
	 * 设置数据为空值
	 *
	 * @param fieldNames
	 * @param moduleId
	 */
	default void setFieldEmpty(Set<String> fieldNames, Long moduleId) {
		UpdateByQueryRequest request = new UpdateByQueryRequest(ElasticUtil.getIndexName(moduleId));
		request.setConflicts("proceed");
		request.setRefresh(true);
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		queryBuilder.filter(getModuleQueryBuilders(moduleId));
		request.setQuery(queryBuilder);
		List<String> scripts = new ArrayList<>();
		for (String fieldName : fieldNames) {
			String script = String.format("ctx._source['%s']=''", fieldName);
			scripts.add(script);
		}
		String script = String.join(";", scripts);
		request.setScript(new Script(script));
		getRestTemplate().execute(client -> client.updateByQuery(request, RequestOptions.DEFAULT));
	}

	/**
	 * 删除索引
	 *
	 * @param index index
	 */
	default void deleteIndex(String index) {
		if (getRestTemplate().indexOps(IndexCoordinates.of(index)).exists()) {
			DeleteByQueryRequest queryRequest = new DeleteByQueryRequest(index);
			getRestTemplate().execute(client -> client.deleteByQuery(queryRequest, RequestOptions.DEFAULT));
		}
	}

	default void deleteByModuleId(Long moduleId){
		String indexName = ElasticUtil.getIndexName(moduleId);
		if (getRestTemplate().indexOps(IndexCoordinates.of(indexName)).exists()) {
			DeleteByQueryRequest queryRequest = new DeleteByQueryRequest(indexName);
			queryRequest.setQuery(getModuleQueryBuilders(moduleId));
			getRestTemplate().execute(client -> client.deleteByQuery(queryRequest, RequestOptions.DEFAULT));
		}
	}

	default SearchHits getSearchResult(SearchBO searchBO) {
		SearchRequest searchRequest = new SearchRequest(ElasticUtil.getIndexName(searchBO.getModuleId()));
		searchRequest.source(createSourceBuilder(searchBO));
		SearchResponse searchResponse = getRestTemplate().execute(client -> client.search(searchRequest, RequestOptions.DEFAULT));
		return searchResponse.getHits();
	}

	/**
	 * 导出数据使用到的 parse方法，直接将value转为显示的值，不是json字符串对象
	 * @param objectMap objectMap
	 * @param fieldList fieldList
	 * @return Map
	 */
	default Map<String, Object> parseExportMap(Map<String, Object> objectMap, List<ModuleFieldBO> fieldList) {
		fieldList.forEach(field -> {
			String fieldName = field.getFieldName();
            ModuleFieldEnum parse = ModuleFieldEnum.parse(field.getType());
            Object value = objectMap.get(fieldName);
            if (ObjectUtil.isNotEmpty(value)) {
                if (ModuleFieldEnum.USER == parse) {
                    List<Long> ids = Convert.toList(Long.class, value);
					value = ids.stream().map(UserCacheUtil::getUserName).collect(Collectors.joining(Const.SEPARATOR));
                }
				else if (ModuleFieldEnum.DATA_UNION_MULTI == parse || ModuleFieldEnum.DATA_UNION == parse){
					value = getBean(IModuleFieldDataService.class).queryMultipleMainFieldValue(value.toString());
				}
				else if (ModuleFieldEnum.BOOLEAN_VALUE == parse){
					value = "1".equals(value) ? "是" : "否";
				}
				else if (ModuleFieldEnum.FLOATNUMBER == parse){
					String str = value instanceof Integer ?  value + ".00" : value.toString();
					value = new BigDecimal(str);
				}
				else if (ModuleFieldEnum.PERCENT == parse){
					value = value.toString() + "%";
				}
                else if (ModuleFieldEnum.STRUCTURE == parse){
                    List<Long> ids = Convert.toList(Long.class, value);
					value = ids.stream().map(UserCacheUtil::getDeptName).collect(Collectors.joining(Const.SEPARATOR));
                }
                else if (ModuleFieldEnum.SELECT == parse){
                    ModuleOptionsBO jsonObject = JSON.parseObject(JSON.toJSONString(value), ModuleOptionsBO.class);
					value = jsonObject.getValue();
                }
                else if (ModuleFieldEnum.CHECKBOX == parse){
                    List<ModuleOptionsBO> objs = JSON.parseArray(JSON.toJSONString(value), ModuleOptionsBO.class);
					value = objs.stream().map(ModuleOptionsBO::getValue).collect(Collectors.joining(Const.SEPARATOR));
                }
                else if (ModuleFieldEnum.DATE_INTERVAL == parse) {
                    CommonESNestedBO dateFromTo = JSON.parseObject(JSON.toJSONString(value), CommonESNestedBO.class);
					value = dateFromTo.getFromDate() + "至" + dateFromTo.getToDate();
                }
                else if (ModuleFieldEnum.FILE == parse){
					value = CollUtil.join(Convert.toList(String.class, value), Const.SEPARATOR);
                }
                else if (ModuleFieldEnum.AREA_POSITION == parse){
                    List<CommonESNestedBO> address = JSON.parseArray(JSON.toJSONString(value), CommonESNestedBO.class);
					value =  address.stream().map(CommonESNestedBO::getName).collect(Collectors.joining("-"));
                }
                else if (ModuleFieldEnum.CURRENT_POSITION == parse){
                    JSONObject json = JSON.parseObject(value.toString());
					value = json.getString("address");
                }
                else if (ModuleFieldEnum.TAG == parse){
                    List<JSONObject> tagList = JSON.parseArray(JSON.toJSONString(value), JSONObject.class);
					value = tagList.stream().map(o -> o.getString("name")).collect(Collectors.joining(Const.SEPARATOR));
                }
                else {
                    try {
                        if (value instanceof List) {
							value = JSON.parse(JSON.toJSONString(value));
                        } else {
							value = JSON.parse((String) value);
                        }
                    } catch (JSONException e) {
						value = value.toString();
                    }
                }
            } else {
				value = "";
            }
			objectMap.put(fieldName, value);
		});
		return objectMap;
	}

	default void exportExcel(SearchBO searchBO, List<ModuleFieldBO> selectedFields, List<ModuleFieldBO> allFields, HttpServletResponse response, Integer isXls, ExcelParseUtil.DataFunc dataFunc ) {
		SearchHits searchResult = getSearchResult(searchBO);
		SearchHit[] hits = searchResult.getHits();
		List<Map<String, Object>> mapList = new ArrayList<>(hits.length);
		for (SearchHit hit : hits) {
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			mapList.add(parseExportMap(sourceAsMap, allFields));
		}
		if(hits.length > 0) {
			searchBO.searchAfter(hits[hits.length - 1].getSortValues());
		}
		EasyExcelParseUtil.exportExcel(mapList, new ExcelParseUtil.ExcelParseService() {
			@Override
			public ExcelParseUtil.DataFunc getFunc() {
				if(ObjectUtil.isNotNull(dataFunc)) {
					return dataFunc;
				} else {
					return (record, headMap) -> {
						for (String fieldName : headMap.keySet()) {
							record.put(fieldName, ActionRecordUtil.parseExportValue(record.get(fieldName), headMap.get(fieldName),false));
						}
					};
				}
			}
			@Override
			public boolean isXlsx() {
				return true;
			}
			//继续获取数据
			@Override
			public List<Map<String, Object>> getNextData() {
				SearchHits result = getSearchResult(searchBO);
				List<Map<String, Object>> dataList = new ArrayList<>(result.getHits().length);
				for (SearchHit hit : result.getHits()) {
					Map<String, Object> sourceAsMap = hit.getSourceAsMap();
					dataList.add(parseExportMap(sourceAsMap, allFields));
				}
				if(result.getHits().length > 0) {
					searchBO.searchAfter(result.getHits()[result.getHits().length - 1].getSortValues());
				}
				return dataList;
			}
			@Override
			public String getExcelName() {
				return getBean(IModuleService.class).getNormal(searchBO.getModuleId()).getName();
			}
		}, selectedFields, allFields, response, isXls,hits.length == searchResult.getTotalHits().value);
	}
}
