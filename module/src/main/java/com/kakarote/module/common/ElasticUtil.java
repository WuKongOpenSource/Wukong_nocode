package com.kakarote.module.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.result.SystemCodeEnum;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.utils.BiTimeUtil;
import com.kakarote.module.constant.FieldSearchEnum;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.ModuleSimpleFieldBO;
import com.kakarote.module.entity.BO.SearchEntityBO;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldData;
import com.kakarote.module.entity.PO.ModuleFieldDataCommon;
import com.kakarote.module.constant.ModuleType;
import com.kakarote.module.entity.BO.CommonESNestedBO;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.service.IModuleFieldDataCommonService;
import com.kakarote.module.service.IModuleFieldDataService;
import com.kakarote.module.service.IModuleFieldService;
import com.kakarote.module.service.IModuleService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author elastic的一些通用操作
 */
@Slf4j
public class ElasticUtil {

	static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(10, 20, 5L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(2048), new ThreadPoolExecutor.AbortPolicy());

    public static void init(List<ModuleFieldConfig> fieldConfigs, RestHighLevelClient client, String index, List<Long> moduleIds) {
		Map<String, Integer> typeMap = new HashMap<>();
		fieldConfigs.forEach(f -> {
			typeMap.put(f.getFieldName(), f.getFieldType());
            if (ObjectUtil.equal(4, f.getFieldType())) {
                typeMap.put(String.format("%sSize", f.getFieldName()), 1);
            }
        });
		typeMap.put("dataId", 3);
		typeMap.put("createUserId", 1);
		typeMap.put("createUserName", 1);
		typeMap.put("ownerUserId", 1);
		typeMap.put("ownerUserName", 1);
		typeMap.put("createTime", 5);
		typeMap.put("updateTime", 5);
		typeMap.put("remarks", 1);
		typeMap.put("teamMember", 1);
		typeMap.put("type", 1);
		typeMap.put("currentFlowId", 1);
		typeMap.put("flowType", 1);
		typeMap.put("flowStatus", 1);
		typeMap.put("moduleId", 1);
		typeMap.put("categoryId", 1);
		typeMap.put("stageId", 1);
		typeMap.put("stageName", 1);
		typeMap.put("stageStatus", 1);
		typeMap.put("batchId", 1);
		typeMap.put("companyId", 1);
		Map<String, Object> properties = new HashMap<>(typeMap.size());
		typeMap.forEach((k,v) -> {
			properties.put(k, parseEsType(v));
		});
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("properties", properties);
        CreateIndexRequest request = new CreateIndexRequest(index);
        HashMap<String, Object> lowercase_normalizer = new HashMap<>();
        lowercase_normalizer.put("type", "custom");
        lowercase_normalizer.put("char_filter", new ArrayList<>());
        lowercase_normalizer.put("filter", Collections.singletonList("lowercase"));
        Map<String, Object> analysisMap = Collections.singletonMap("analysis", Collections.singletonMap("normalizer", Collections.singletonMap("lowercase_normalizer", lowercase_normalizer)));

        try {
            //设置mapping参数
            request.mapping(mapping);
            request.settings(analysisMap);
            request.alias(new Alias(index + "_alias"));
            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
            boolean flag = createIndexResponse.isAcknowledged();
            if (flag) {
                log.info("创建索引库:" + index + "成功！");
            }
        } catch (IOException e) {
            log.error("创建索引错误", e);
        }
        for (Long moduleId : moduleIds) {
			int page = 0;
			int size = 10000;
			List<Future<Boolean>> futureList = new LinkedList<>();
			while (true) {
				List<ModuleFieldDataCommon> dataCommons = ApplicationContextHolder.getBean(IModuleFieldDataCommonService.class).getByModuleId(moduleId, page*size,size);
				if (CollUtil.isEmpty(dataCommons)) {
					break;
				}
				page++;
                ModuleEntity module = ApplicationContextHolder.getBean(IModuleService.class).getNormal(moduleId);
				Map<Long, ModuleFieldDataCommon> dataCommonMap = dataCommons.stream().collect(Collectors.toMap(ModuleFieldDataCommon::getDataId, d -> d));
				List<ModuleFieldData> fieldDataList = ApplicationContextHolder.getBean(IModuleFieldDataService.class)
						.lambdaQuery().eq(ModuleFieldData::getModuleId, moduleId).in(ModuleFieldData::getDataId, dataCommonMap.keySet()).list();
				// 获取所有数据的字段ID
				Set<Long> allFieldIds = fieldDataList.stream().map(ModuleFieldData::getFieldId).collect(Collectors.toSet());
				List<ModuleField> allFields = ApplicationContextHolder.getBean(IModuleFieldService.class).getByFieldIds(moduleId, new ArrayList<>(allFieldIds), module.getVersion());
				Map<Long, ModuleField> fieldMap = allFields.stream().collect(Collectors.toMap(ModuleField::getFieldId, Function.identity()));
				Map<Long, List<ModuleFieldData>> fieldDataMap = fieldDataList.stream().collect(Collectors.groupingBy(ModuleFieldData::getDataId));
				List<Map<String, Object>> valueList = new ArrayList<>(fieldDataMap.size());
				fieldDataMap.forEach((k, v) -> {
					Map<String, Object> valueMap = new HashMap<>();
					v.forEach(d -> {
						String value = d.getValue();
						if (StrUtil.isNotEmpty(value)) {
                            ModuleField field = fieldMap.get(d.getFieldId());
                            if (ObjectUtil.isNotNull(field)) {
                                if (StrUtil.isNotEmpty(field.getFieldName())) {
                                    if (ObjectUtil.equal(ModuleFieldEnum.SELECT, ModuleFieldEnum.parse(field.getType()))) {
                                        valueMap.put(field.getFieldName(), JSON.parseObject(value, JSONObject.class));
                                        valueMap.put(String.format("%sSize", field.getFieldName()), 1);
                                    } else if (Arrays.asList(ModuleFieldEnum.CHECKBOX, ModuleFieldEnum.TAG).contains(ModuleFieldEnum.parse(field.getType()))) {
                                        List<JSONObject> jsonObjectList = JSON.parseArray(value, JSONObject.class);
                                        valueMap.put(field.getFieldName(), jsonObjectList);
                                        valueMap.put(String.format("%sSize", field.getFieldName()), jsonObjectList.size());
                                    } else if (ObjectUtil.equal(ModuleFieldEnum.DATE_INTERVAL, ModuleFieldEnum.parse(field.getType()))) {
                                        valueMap.put(field.getFieldName(), JSON.parseObject(value, JSONObject.class));
                                    } else if (ObjectUtil.equal(ModuleFieldEnum.AREA_POSITION, ModuleFieldEnum.parse(field.getType()))) {
                                        List<JSONObject> jsonObjectList = JSON.parseArray(value, JSONObject.class);
											valueMap.put(field.getFieldName(), jsonObjectList);
                                        valueMap.put(String.format("%sSize", field.getFieldName()), jsonObjectList.size());
                                    } else if (ObjectUtil.equal(ModuleFieldEnum.DETAIL_TABLE, ModuleFieldEnum.parse(field.getType()))) {
										List<JSONObject> jsonObjectList = JSON.parseArray(value, JSONObject.class);
										for (JSONObject object : jsonObjectList) {
											ModuleMapUtil.removeEmptyValue(object);
										}
										valueMap.put(field.getFieldName(), jsonObjectList);
									}
                                    else {
                                        valueMap.put(field.getFieldName(), value);
                                    }
                                }
                            }
						}
					});
					ModuleFieldDataCommon dataCommon = dataCommonMap.get(k);
					valueMap.putAll(BeanUtil.beanToMap(dataCommon));
					if (ObjectUtil.isNotEmpty(dataCommon.getTeamMember())) {
						valueMap.put("teamMember", JSON.parseArray(dataCommon.getTeamMember(), Long.class));
					}
					valueList.add(valueMap);
				});
				futureList.add(THREAD_POOL.submit(new SaveES(moduleId, typeMap, valueList)));
			}
			for (Future<Boolean> future : futureList) {
				try {
					Boolean result = future.get();
					log.info("数据处理完成：{}", result);
				} catch (InterruptedException | ExecutionException e) {
					throw new BusinessException(SystemCodeEnum.SYSTEM_ERROR);
				}
			}
		}
		RefreshRequest refreshRequest = new RefreshRequest(index);
		try {
			client.indices().refresh(refreshRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class SaveES implements Callable<Boolean> {

    	private Long moduleId;

		private Map<String, Integer> typeMap;

		private List<Map<String, Object>> valueList;

		private SaveES(Long moduleId, Map<String, Integer> typeMap, List<Map<String, Object>> valueList) {
			this.moduleId = moduleId;
			this.typeMap = typeMap;
			this.valueList = valueList;
		}

		@Override
		public Boolean call() {
			BulkRequest bulkRequest = new BulkRequest();
			for (Map<String, Object> map : valueList) {
				typeMap.forEach((k,v) -> {
					if (ObjectUtil.equal(2, v)) {
						Object value = map.remove(k);
						if (value instanceof Date) {
							map.put(k, DateUtil.formatDate((Date) value));
						}
					} else if (ObjectUtil.equal(5, v)) {
						Object value = map.remove(k);
						if (value instanceof Date) {
							map.put(k, DateUtil.formatDateTime((Date) value));
						}
					} else {
						Object value = map.remove(k);
						if (!ObjectUtil.isEmpty(value)) {
							map.put(k, value);
						}
					}
				});
				IndexRequest request = new IndexRequest(getIndexName(moduleId));
				request.id(MapUtil.getStr(map, "dataId"));
				request.source(map);
				bulkRequest.add(request);
				if (bulkRequest.requests().size() >= 1000) {
					bulk(bulkRequest);
					bulkRequest = new BulkRequest();
				}
			}
			bulk(bulkRequest);
			valueList.clear();
			return true;
		}
	}

	private static void bulk(BulkRequest bulkRequest) {
		if (CollUtil.isEmpty(bulkRequest.requests())) {
			return;
		}
		ElasticsearchRestTemplate restTemplate = ApplicationContextHolder.getBean(ElasticsearchRestTemplate.class);
		BulkResponse bulk = restTemplate.execute(client -> client.bulk(bulkRequest, RequestOptions.DEFAULT));
		boolean hasFailures = bulk.hasFailures();
		log.info("bulkHasFailures:{}", bulk.hasFailures());
		if (bulk.hasFailures()) {
			log.info(JSON.toJSONString(bulk.buildFailureMessage()));
			int count = 3;
			while (count > 0 && hasFailures) {
				count--;
				bulk = restTemplate.execute(client -> client.bulk(bulkRequest, RequestOptions.DEFAULT));
				hasFailures = bulk.hasFailures();
			}
		}
	}

	/**
	 * 索引增加字段
	 *
	 * @param client   客户端
	 * @param fields   字段列表
	 * @param moduleId moduleId
	 */
	public static void addField(RestHighLevelClient client, Long moduleId, BaseField... fields) {
		if (ObjectUtil.isNull(fields) || fields.length == 0) {
			return;
		}
		String index = getIndexName(moduleId);
		if (!indexExist(index)) {
			ElasticUtil.createIndex(client, getIndexName(moduleId));
		}
		JSONObject object = new JSONObject();
		JSONObject child = new JSONObject();
		for (BaseField baseField : fields) {
			child.put(StrUtil.toCamelCase(baseField.getName()), parseEsType(baseField.getType()));
		}
		object.put("properties", child);
		PutMappingRequest request = new PutMappingRequest(getIndexName(moduleId));
		request.source(object);
		try {
			client.indices().putMapping(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("新增字段错误", e);
			throw new BusinessException(SystemCodeEnum.SYSTEM_ERROR);
		}
	}

	/**
	 * @param client    客户端
	 * @param moduleId  moduleId
	 * @param fieldName 字段名称
	 * @param type      字段类型
	 */
	public static void addField(RestHighLevelClient client, String fieldName, Integer type, Long moduleId) {
		addField(client, moduleId, new BaseField(fieldName, type));
	}

	/**
	 * 创建索引
	 *
	 * @param client
	 * @param index
	 */
	public static void createIndex(RestHighLevelClient client, String index) {
		CreateIndexRequest request = new CreateIndexRequest(index);
        HashMap<String, Object> lowercase_normalizer = new HashMap<>();
        lowercase_normalizer.put("type", "custom");
        lowercase_normalizer.put("char_filter", new ArrayList<>());
        lowercase_normalizer.put("filter", Collections.singletonList("lowercase"));
        Map<String, Object> analysisMap = Collections.singletonMap("analysis", Collections.singletonMap("normalizer", Collections.singletonMap("lowercase_normalizer", lowercase_normalizer)));
		try {
            request.settings(analysisMap);
			request.alias(new Alias(index + "_alias"));
			CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
			boolean flag = createIndexResponse.isAcknowledged();
			if (flag) {
				log.info("创建索引库:" + index + "成功！");
			}
		} catch (IOException e) {
			log.error("创建索引错误", e);
		}
	}

	/**
	 * 索引是否存在
	 *
	 * @param index
	 * @return
	 */
	public static boolean indexExist(String index){
		return ApplicationContextHolder.getBean(ElasticsearchRestTemplate.class).indexOps(IndexCoordinates.of(index)).exists();
	}

    private static Map<String, Object> parseEsType(Integer type) {
        Map<String, Object> map = new HashMap<>();
        String name;
        switch (type) {
            case 1:
                name = "keyword";
                break;
            case 2:
                name = "date";
                map.put("format", "yyyy-MM-dd HH:mm:ss || yyyy-MM-dd || yyyy-MM || yyyy");
                break;
            case 3:
                name = "scaled_float";
                map.put("scaling_factor", 100);
                break;
            case 4:
                name = "nested";
                map.put("properties", new JSONObject()
                        .fluentPut("code", new JSONObject().fluentPut("type", "keyword"))
                        .fluentPut("key", new JSONObject().fluentPut("type", "keyword"))
                        .fluentPut("name", new JSONObject().fluentPut("type", "keyword"))
                        .fluentPut("value", new JSONObject().fluentPut("type", "keyword"))
                        .fluentPut("groupId", new JSONObject().fluentPut("type", "keyword"))
                        .fluentPut("groupName", new JSONObject().fluentPut("type", "keyword"))
                        .fluentPut("fromDate", new JSONObject().fluentPut("type", "date"))
                        .fluentPut("toDate", new JSONObject().fluentPut("type", "date"))
                        .fluentPut("type", new JSONObject().fluentPut("type", "short"))
                        .fluentPut("sort", new JSONObject().fluentPut("type", "short")));
                break;
            case 5:
                name = "date";
                map.put("format", "yyyy-MM-dd HH:mm:ss");
                break;
			case 6:
				name = "nested";
				break;
            default:
                name = "keyword";
                break;
        }
        map.put("type", name);
        String keyword="keyword";
        if (keyword.equals(name)) {
            map.put("fields", new JSONObject().fluentPut("sort", new JSONObject().fluentPut("type", "icu_collation_keyword").fluentPut("language", "zh").fluentPut("country", "CN")));
        }
        return map;
    }

	/**
	 *  获取模块的索引名
	 *
	 * @param moduleId
	 * @return
	 */
	public static String getIndexName(Long moduleId) {
        return "module_2022";
    }

	public static Map<String, List<Long>> getModuleIndexMap() {
		return ApplicationContextHolder.getBean(IModuleService.class).getAll().stream()
				.filter(m -> ObjectUtil.equal(ModuleType.MODULE.getType(), m.getModuleType()))
				.collect(Collectors.groupingBy(ModuleEntity::getIndexName, Collectors.mapping(ModuleEntity::getModuleId, Collectors.toList())));
	}

	/**
	 * 普通文本类型的es搜索
	 *
	 * @param search       搜索条件
	 * @param queryBuilder 查询器
	 */
	public static void textSearch(SearchEntityBO search, BoolQueryBuilder queryBuilder) {
		ModuleSimpleFieldBO detailTableField = search.getDetailTableField();
		if (CollUtil.isEmpty(search.getValues()) &&
				!Arrays.asList(FieldSearchEnum.IS_NULL, FieldSearchEnum.IS_NOT_NULL).contains(search.getSearchEnum())) {
			isNullSearch(search, queryBuilder);
			return;
		}
		switch (search.getSearchEnum()) {
			case IS:
				if (ObjectUtil.isNotNull(detailTableField)) {
					String name = String.join(".", detailTableField.getFieldName(), search.getFieldName());
					NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), QueryBuilders.termsQuery(name, search.getValues()), ScoreMode.None);
					queryBuilder.filter(nestedQueryBuilder);
				} else {
					queryBuilder.filter(QueryBuilders.termsQuery(search.getFieldName(), search.getValues()));
				}
				break;
			case IS_NOT:
				if (ObjectUtil.isNotNull(detailTableField)) {
					String name = String.join(".", detailTableField.getFieldName(), search.getFieldName());
					NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), QueryBuilders.termsQuery(name, search.getValues()), ScoreMode.None);
					queryBuilder.mustNot(nestedQueryBuilder);
				} else {
					queryBuilder.mustNot(QueryBuilders.termsQuery(search.getFieldName(), search.getValues()));
				}
				break;
			case PREFIX:
				if (ObjectUtil.isNotNull(detailTableField)) {
					String name = String.join(".", detailTableField.getFieldName(), search.getFieldName());
					if (search.getValues().size() == 1) {
						NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), QueryBuilders.prefixQuery(name, search.getValues().get(0)), ScoreMode.None);
						queryBuilder.filter(nestedQueryBuilder);
					} else {
						BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
						for (String value : search.getValues()) {
							NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), QueryBuilders.prefixQuery(name, value), ScoreMode.None);
							boolQuery.should(nestedQueryBuilder);
						}
						queryBuilder.filter(boolQuery);
					}
				} else {
					if (search.getValues().size() == 1) {
						queryBuilder.filter(QueryBuilders.prefixQuery(search.getFieldName(), search.getValues().get(0)));
					} else {
						BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
						for (String value : search.getValues()) {
							boolQuery.should(QueryBuilders.prefixQuery(search.getFieldName(), value));
						}
						queryBuilder.filter(boolQuery);
					}
				}
				break;
			case SUFFIX:
			case CONTAINS:
				String suffix = search.getSearchEnum() == FieldSearchEnum.SUFFIX ? "" : "*";
				if (ObjectUtil.isNotNull(detailTableField)) {
					String name = String.join(".", detailTableField.getFieldName(), search.getFieldName());
					if (search.getValues().size() == 1) {
						NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), QueryBuilders.wildcardQuery(name, "*" + search.getValues().get(0) + suffix), ScoreMode.None);
						queryBuilder.filter(nestedQueryBuilder);
					} else {
						BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
						for (String value : search.getValues()) {
							NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), QueryBuilders.wildcardQuery(name, "*" + value + suffix), ScoreMode.None);
							boolQuery.should(nestedQueryBuilder);
						}
						queryBuilder.filter(boolQuery);
					}
				} else {
					if (search.getValues().size() == 1) {
						queryBuilder.filter(QueryBuilders.wildcardQuery(search.getFieldName(), "*" + search.getValues().get(0) + suffix));
					} else {
						BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
						for (String value : search.getValues()) {
							boolQuery.should(QueryBuilders.wildcardQuery(search.getFieldName(), "*" + value + suffix));
						}
						queryBuilder.filter(boolQuery);
					}
				}
				break;
			case NOT_CONTAINS:
				if (ObjectUtil.isNotNull(detailTableField)) {
					String name = String.join(".", detailTableField.getFieldName(), search.getFieldName());
					if (search.getValues().size() == 1) {
						NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), QueryBuilders.wildcardQuery(name, "*" + search.getValues().get(0) + "*"), ScoreMode.None);
						queryBuilder.mustNot(nestedQueryBuilder);
					} else {
						BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
						for (String value : search.getValues()) {
							NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), QueryBuilders.wildcardQuery(name, "*" + value + "*"), ScoreMode.None);
							boolQuery.should(nestedQueryBuilder);
						}
						queryBuilder.mustNot(boolQuery);
					}
				} else {
					if (search.getValues().size() == 1) {
						queryBuilder.mustNot(QueryBuilders.wildcardQuery(search.getFieldName(), "*" + search.getValues().get(0) + "*"));
					} else {
						BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
						for (String value : search.getValues()) {
							boolQuery.should(QueryBuilders.wildcardQuery(search.getFieldName(), "*" + value + "*"));
						}
						queryBuilder.mustNot(boolQuery);
					}
				}
				break;
			case IS_NULL:
				isNullSearch(search, queryBuilder);
				break;
			case IS_NOT_NULL:
				isNotNullSearch(search, queryBuilder);
				break;
			default:break;
		}
	}

    /**
     *  下拉选es 搜索
     *
     * @param search
     * @param queryBuilder
     */
    public static void selectSearch(SearchEntityBO search, BoolQueryBuilder queryBuilder) {
        String nestedPath = search.getFieldName();
        String name = String.join(".",nestedPath, "value");
		if (CollUtil.isEmpty(search.getValues())) {
			NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.existsQuery(name), ScoreMode.None);
			queryBuilder.mustNot(nestedQueryBuilder);
		}
        switch (search.getSearchEnum()) {
            case IS: {
                List<CommonESNestedBO> nestedBOList = search.getValues().stream().map(v -> JSON.parseObject(v, CommonESNestedBO.class)).collect(Collectors.toList());
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                for (CommonESNestedBO nestedBO : nestedBOList) {
                    NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.termQuery(name, nestedBO.getValue()), ScoreMode.None);
                    boolQuery.should(nestedQueryBuilder);
                }
                queryBuilder.filter(boolQuery);
                break;
            }
            case IS_NOT: {
                List<CommonESNestedBO> nestedBOList = search.getValues().stream().map(v -> JSON.parseObject(v, CommonESNestedBO.class)).collect(Collectors.toList());
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                for (CommonESNestedBO nestedBO : nestedBOList) {
                    NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.termQuery(name, nestedBO.getValue()), ScoreMode.None);
                    boolQuery.should(nestedQueryBuilder);
                }
                queryBuilder.mustNot(boolQuery);
                break;
            }
            case IS_NULL: {
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.existsQuery(name), ScoreMode.None);
                queryBuilder.mustNot(nestedQueryBuilder);
                break;
            }
            case IS_NOT_NULL:{
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.existsQuery(name), ScoreMode.None);
                queryBuilder.must(nestedQueryBuilder);
                break;
            }
            default:break;
        }
    }

	/**
	 * 多选类型的es搜索
	 *
	 * @param search       搜索条件
	 * @param queryBuilder 查询器
	 */
	public static void checkboxSearch(SearchEntityBO search, BoolQueryBuilder queryBuilder) {
        String nestedPath = search.getFieldName();
        String name = String.join(".",nestedPath, "value");
		if (CollUtil.isEmpty(search.getValues()) &&
				!Arrays.asList(FieldSearchEnum.IS_NULL, FieldSearchEnum.IS_NOT_NULL).contains(search.getSearchEnum())) {
			NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.existsQuery(name), ScoreMode.None);
			queryBuilder.mustNot(nestedQueryBuilder);
			return;
		}
		switch (search.getSearchEnum()) {
			case IS_NOT: {
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                List<CommonESNestedBO> nestedBOList = search.getValues().stream().map(v -> JSON.parseObject(v, CommonESNestedBO.class)).collect(Collectors.toList());
                List<String> values = nestedBOList.stream().map(CommonESNestedBO::getValue).collect(Collectors.toList());
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.termsQuery(name, values), ScoreMode.None);
                boolQuery.must(nestedQueryBuilder);
                boolQuery.must(QueryBuilders.termQuery(String.format("%sSize", search.getFieldName()), search.getValues().size()));
                queryBuilder.mustNot(boolQuery);
				break;
			}
			case IS: {
                List<CommonESNestedBO> nestedBOList = search.getValues().stream().map(v -> JSON.parseObject(v, CommonESNestedBO.class)).collect(Collectors.toList());
                List<String> values = nestedBOList.stream().map(CommonESNestedBO::getValue).collect(Collectors.toList());
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.termsQuery(name, values), ScoreMode.None);
                queryBuilder.filter(nestedQueryBuilder);
				queryBuilder.filter(QueryBuilders.termQuery(String.format("%sSize", search.getFieldName()), search.getValues().size()));
				break;
			}
			case CONTAINS: {
                List<CommonESNestedBO> nestedBOList = search.getValues().stream().map(v -> JSON.parseObject(v, CommonESNestedBO.class)).collect(Collectors.toList());
                List<String> values = nestedBOList.stream().map(CommonESNestedBO::getValue).collect(Collectors.toList());
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.termsQuery(name, values), ScoreMode.None);
                queryBuilder.filter(nestedQueryBuilder);
                break;
            }
			case NOT_CONTAINS: {
                List<CommonESNestedBO> nestedBOList = search.getValues().stream().map(v -> JSON.parseObject(v, CommonESNestedBO.class)).collect(Collectors.toList());
                List<String> values = nestedBOList.stream().map(CommonESNestedBO::getValue).collect(Collectors.toList());
                NestedQueryBuilder containsQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.termsQuery(name, values), ScoreMode.None);
                NestedQueryBuilder notNullQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.existsQuery(name), ScoreMode.None);
                queryBuilder.mustNot(containsQueryBuilder);
                queryBuilder.must(notNullQueryBuilder);
				break;
			}
			case IS_NULL: {
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.existsQuery(name), ScoreMode.None);
                queryBuilder.mustNot(nestedQueryBuilder);
                break;
            }
			case IS_NOT_NULL: {
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.existsQuery(name), ScoreMode.None);
                queryBuilder.must(nestedQueryBuilder);
                break;
            }
			default:break;
		}
	}

    /**
     * 日期区间的es搜索
     *
     * @param search       搜索条件
     * @param queryBuilder 查询器
     */
    public static void dateIntervalSearch(SearchEntityBO search, BoolQueryBuilder queryBuilder) {
        String nestedPath = search.getFieldName();
        String fromDate = String.join(".",nestedPath, "fromDate");
        String toDate = String.join(".",nestedPath, "toDate");
		if (CollUtil.isEmpty(search.getValues()) &&
				!Arrays.asList(FieldSearchEnum.IS_NULL, FieldSearchEnum.IS_NOT_NULL).contains(search.getSearchEnum())) {
			NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.existsQuery(fromDate), ScoreMode.None);
			queryBuilder.mustNot(nestedQueryBuilder);
			return;
		}
        switch (search.getSearchEnum()) {
            case IS: {
                List<CommonESNestedBO> nestedBOList = search.getValues().stream().map(v -> JSON.parseObject(v, CommonESNestedBO.class)).collect(Collectors.toList());
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                for (CommonESNestedBO nestedBO : nestedBOList) {
                    NestedQueryBuilder fromDateQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.termQuery(fromDate, nestedBO.getFromDate()), ScoreMode.None);
                    NestedQueryBuilder toDateQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.termQuery(toDate, nestedBO.getToDate()), ScoreMode.None);
                    boolQuery.filter(fromDateQueryBuilder);
                    boolQuery.filter(toDateQueryBuilder);
                }
                queryBuilder.filter(boolQuery);
                break;
            }
            case IS_NOT: {
                List<CommonESNestedBO> nestedBOList = search.getValues().stream().map(v -> JSON.parseObject(v, CommonESNestedBO.class)).collect(Collectors.toList());
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                for (CommonESNestedBO nestedBO : nestedBOList) {
                    NestedQueryBuilder fromDateQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.termQuery(fromDate, nestedBO.getFromDate()), ScoreMode.None);
                    NestedQueryBuilder toDateQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.termQuery(toDate, nestedBO.getToDate()), ScoreMode.None);
                    boolQuery.filter(fromDateQueryBuilder);
                    boolQuery.filter(toDateQueryBuilder);
                }
                queryBuilder.mustNot(boolQuery);
                break;
            }
            case CONTAINS: {
                List<CommonESNestedBO> nestedBOList = search.getValues().stream().map(v -> JSON.parseObject(v, CommonESNestedBO.class)).collect(Collectors.toList());
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                for (CommonESNestedBO nestedBO : nestedBOList) {
                    // 小于开始时间
                    RangeQueryBuilder fromDateRangeQuery = QueryBuilders.rangeQuery(fromDate);
                    fromDateRangeQuery.lte(nestedBO.getFromDate());

                    // 大于结束时间
                    RangeQueryBuilder toDateRangeQuery = QueryBuilders.rangeQuery(toDate);
                    toDateRangeQuery.gte(nestedBO.getToDate());

                    NestedQueryBuilder fromDateQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), fromDateRangeQuery, ScoreMode.None);
                    NestedQueryBuilder toDateQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), toDateRangeQuery, ScoreMode.None);

                    boolQuery.filter(fromDateQueryBuilder);
                    boolQuery.filter(toDateQueryBuilder);
                }
                queryBuilder.filter(boolQuery);
                break;
            }
            case NOT_CONTAINS: {
                List<CommonESNestedBO> nestedBOList = search.getValues().stream().map(v -> JSON.parseObject(v, CommonESNestedBO.class)).collect(Collectors.toList());
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                for (CommonESNestedBO nestedBO : nestedBOList) {
                    // 大于开始时间
                    RangeQueryBuilder fromDateRangeQuery = QueryBuilders.rangeQuery(fromDate);
                    fromDateRangeQuery.gt(nestedBO.getFromDate());

                    // 小于结束时间
                    RangeQueryBuilder toDateRangeQuery = QueryBuilders.rangeQuery(toDate);
                    toDateRangeQuery.lt(nestedBO.getToDate());

                    NestedQueryBuilder fromDateQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), fromDateRangeQuery, ScoreMode.None);
                    NestedQueryBuilder toDateQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), toDateRangeQuery, ScoreMode.None);

                    boolQuery.should(fromDateQueryBuilder);
                    boolQuery.should(toDateQueryBuilder);
                }
                queryBuilder.filter(boolQuery);
                break;
            }
            case IS_NULL: {
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.existsQuery(fromDate), ScoreMode.None);
                queryBuilder.mustNot(nestedQueryBuilder);
                break;
            }
            case IS_NOT_NULL: {
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.existsQuery(fromDate), ScoreMode.None);
                queryBuilder.must(nestedQueryBuilder);
                break;
            }
        }
    }

    /**
     * 地址字段类型es搜索
     *
     * @param search
     * @param queryBuilder
     */
    public static void addressSearch(SearchEntityBO search, BoolQueryBuilder queryBuilder) {
        String nestedPath = search.getFieldName();
        String name = String.join(".",nestedPath, "code");
		if (CollUtil.isEmpty(search.getValues()) &&
				!Arrays.asList(FieldSearchEnum.IS_NULL, FieldSearchEnum.IS_NOT_NULL).contains(search.getSearchEnum())) {
			NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.existsQuery(name), ScoreMode.None);
			queryBuilder.mustNot(nestedQueryBuilder);
			return;
		}
        switch (search.getSearchEnum()) {
            case IS: {
                List<CommonESNestedBO> nestedBOList = search.getValues().stream().map(v -> JSON.parseObject(v, CommonESNestedBO.class)).collect(Collectors.toList());
                List<String> values = nestedBOList.stream().map(CommonESNestedBO::getCode).collect(Collectors.toList());
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.termsQuery(name, values), ScoreMode.None);
                queryBuilder.filter(nestedQueryBuilder);
                queryBuilder.filter(QueryBuilders.termQuery(String.format("%sSize", search.getFieldName()), search.getValues().size()));
                break;
            }
            case IS_NOT: {
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                List<CommonESNestedBO> nestedBOList = search.getValues().stream().map(v -> JSON.parseObject(v, CommonESNestedBO.class)).collect(Collectors.toList());
                List<String> values = nestedBOList.stream().map(CommonESNestedBO::getCode).collect(Collectors.toList());
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.termsQuery(name, values), ScoreMode.None);
                boolQuery.must(nestedQueryBuilder);
                boolQuery.must(QueryBuilders.termQuery(String.format("%sSize", search.getFieldName()), search.getValues().size()));
                queryBuilder.mustNot(boolQuery);
                break;
            }
            case CONTAINS: {
                List<CommonESNestedBO> nestedBOList = search.getValues().stream().map(v -> JSON.parseObject(v, CommonESNestedBO.class)).collect(Collectors.toList());
                List<String> values = nestedBOList.stream().map(CommonESNestedBO::getCode).collect(Collectors.toList());
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.termsQuery(name, values), ScoreMode.None);
                RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(String.format("%sSize", search.getFieldName()));
                rangeQuery.gte(values.size());
                queryBuilder.filter(nestedQueryBuilder);
                queryBuilder.filter(rangeQuery);
                break;
            }
            case NOT_CONTAINS: {
                List<CommonESNestedBO> nestedBOList = search.getValues().stream().map(v -> JSON.parseObject(v, CommonESNestedBO.class)).collect(Collectors.toList());
                List<String> values = nestedBOList.stream().map(CommonESNestedBO::getCode).collect(Collectors.toList());
                NestedQueryBuilder containsQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.termsQuery(name, values), ScoreMode.None);
                NestedQueryBuilder notNullQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.existsQuery(name), ScoreMode.None);
                RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(String.format("%sSize", search.getFieldName()));
                rangeQuery.lt(values.size());
                queryBuilder.mustNot(containsQueryBuilder);
                queryBuilder.must(notNullQueryBuilder);
                queryBuilder.must(rangeQuery);
                break;
            }
            case IS_NULL: {
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.existsQuery(name), ScoreMode.None);
                queryBuilder.mustNot(nestedQueryBuilder);
                break;
            }
            case IS_NOT_NULL: {
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(search.getFieldName(), QueryBuilders.existsQuery(name), ScoreMode.None);
                queryBuilder.must(nestedQueryBuilder);
                break;
            }
        }
    }

	/**
	 * 数字类型的es搜索
	 *
	 * @param search       搜索条件
	 * @param queryBuilder 查询器
	 */
	public static void numberSearch(SearchEntityBO search, BoolQueryBuilder queryBuilder) {
		ModuleSimpleFieldBO detailTableField = search.getDetailTableField();
		if (CollUtil.isEmpty(search.getValues()) &&
				!Arrays.asList(FieldSearchEnum.IS_NULL, FieldSearchEnum.IS_NOT_NULL).contains(search.getSearchEnum())) {
			isNullSearch(search, queryBuilder);
			return;
		}
		switch (search.getSearchEnum()) {
			case IS:
				if (ObjectUtil.isNotNull(detailTableField)) {
					String name = String.join(".", detailTableField.getFieldName(), search.getFieldName());
					NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), QueryBuilders.termQuery(name, search.getValues().get(0)), ScoreMode.None);
					queryBuilder.filter(nestedQueryBuilder);
				} else {
					queryBuilder.filter(QueryBuilders.termQuery(search.getFieldName(), search.getValues().get(0)));
				}
				break;
			case IS_NOT:
				if (ObjectUtil.isNotNull(detailTableField)) {
					String name = String.join(".", detailTableField.getFieldName(), search.getFieldName());
					NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), QueryBuilders.termQuery(name, search.getValues().get(0)), ScoreMode.None);
					queryBuilder.mustNot(nestedQueryBuilder);
				} else {
					queryBuilder.mustNot(QueryBuilders.termQuery(search.getFieldName(), search.getValues().get(0)));
				}
				break;
			case GT: {
				if (ObjectUtil.isNotNull(detailTableField)) {
					String name = String.join(".", detailTableField.getFieldName(), search.getFieldName());
					RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(name);
					rangeQuery.gt(search.getValues().get(0));
					NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), rangeQuery, ScoreMode.None);
					queryBuilder.filter(nestedQueryBuilder);
				} else {
					RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(search.getFieldName());
					rangeQuery.gt(search.getValues().get(0));
					queryBuilder.filter(rangeQuery);
				}
				break;
			}
			case EGT: {
				if (ObjectUtil.isNotNull(detailTableField)) {
					String name = String.join(".", detailTableField.getFieldName(), search.getFieldName());
					RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(name);
					rangeQuery.gte(search.getValues().get(0));
					NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), rangeQuery, ScoreMode.None);
					queryBuilder.filter(nestedQueryBuilder);
				} else {
					RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(search.getFieldName());
					rangeQuery.gte(search.getValues().get(0));
					queryBuilder.filter(rangeQuery);
				}
				break;
			}
			case LT: {
				if (ObjectUtil.isNotNull(detailTableField)) {
					String name = String.join(".", detailTableField.getFieldName(), search.getFieldName());
					RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(name);
					rangeQuery.lt(search.getValues().get(0));
					NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), rangeQuery, ScoreMode.None);
					queryBuilder.filter(nestedQueryBuilder);
				} else {
					RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(search.getFieldName());
					rangeQuery.lt(search.getValues().get(0));
					queryBuilder.filter(rangeQuery);
				}
				break;
			}
			case ELT: {
				if (ObjectUtil.isNotNull(detailTableField)) {
					String name = String.join(".", detailTableField.getFieldName(), search.getFieldName());
					RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(name);
					rangeQuery.lte(search.getValues().get(0));
					NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), rangeQuery, ScoreMode.None);
					queryBuilder.filter(nestedQueryBuilder);
				} else {
					RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(search.getFieldName());
					rangeQuery.lte(search.getValues().get(0));
					queryBuilder.filter(rangeQuery);
				}
				break;
			}
			case RANGE: {
				if (ObjectUtil.isNotNull(detailTableField)) {
					String name = String.join(".", detailTableField.getFieldName(), search.getFieldName());
					RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(name);
					rangeQuery.gte(search.getValues().get(0));
					rangeQuery.lte(search.getValues().get(1));
					NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), rangeQuery, ScoreMode.None);
					queryBuilder.filter(nestedQueryBuilder);
				} else {
					RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(search.getFieldName());
					rangeQuery.gte(search.getValues().get(0));
					rangeQuery.lte(search.getValues().get(1));
					queryBuilder.filter(rangeQuery);
				}
				break;
			}
			case IS_NULL:
				isNullSearch(search, queryBuilder);
				break;
			case IS_NOT_NULL:
				isNotNullSearch(search, queryBuilder);
				break;
			default:break;
		}
	}

	/**
	 * 时间类型的es搜索
	 *
	 * @param search       搜索条件
	 * @param queryBuilder 查询器
	 */
	public static void dateSearch(SearchEntityBO search, BoolQueryBuilder queryBuilder, ModuleFieldEnum fieldEnum) {
		List<String> values = search.getValues();
		if (CollUtil.isEmpty(values)) {
			isNullSearch(search, queryBuilder);
			return;
		}
		switch (search.getSearchEnum()) {
			case IS: {
				queryBuilder.filter(QueryBuilders.termQuery(search.getFieldName(), search.getValues().get(0)));
				break;
			}
			case IS_NOT: {
				queryBuilder.mustNot(QueryBuilders.termQuery(search.getFieldName(), search.getValues().get(0)));
				break;
			}
			case IS_NULL:
				isNullSearch(search, queryBuilder);
				break;
			case IS_NOT_NULL:
				isNotNullSearch(search, queryBuilder);
				break;
			case GT: {
				RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(search.getFieldName());
				rangeQuery.gt(search.getValues().get(0));
				queryBuilder.filter(rangeQuery);
				break;
			}
			case EGT: {
				RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(search.getFieldName());
				rangeQuery.gte(search.getValues().get(0));
				queryBuilder.filter(rangeQuery);
				break;
			}
			case LT: {
				RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(search.getFieldName());
				rangeQuery.lt(search.getValues().get(0));
				queryBuilder.filter(rangeQuery);
				break;
			}
			case ELT: {
				RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(search.getFieldName());
				rangeQuery.lte(search.getValues().get(0));
				queryBuilder.filter(rangeQuery);
				break;
			}
			case RANGE: {
				BiTimeUtil.BiParams biParams = new BiTimeUtil.BiParams();
				if (search.getValues().size() > 1) {
					biParams.setStartTime(values.get(0));
					biParams.setEndTime(values.get(1));
				} else {
					biParams.setType(search.getValues().get(0));
				}
				BiTimeUtil.BiTimeEntity timeEntity = BiTimeUtil.analyzeTime(biParams);
				Date beginDate = timeEntity.getBeginDate();
				Date endDate = timeEntity.getEndDate();
				RangeQueryBuilder builder = QueryBuilders.rangeQuery(search.getFieldName());
				builder.gte(fieldEnum == ModuleFieldEnum.DATETIME ? DateUtil.formatDateTime(beginDate) : DateUtil.formatDate(beginDate));
				builder.lte(fieldEnum == ModuleFieldEnum.DATETIME ? DateUtil.formatDateTime(endDate) : DateUtil.formatDate(endDate));
				queryBuilder.filter(builder);
				break;
			}
			default:break;
		}
	}


	/**
	 * 搜索用户信息
	 *
	 * @param search       搜索条件
	 * @param queryBuilder 查询器
	 */
	public static void userSearch(SearchEntityBO search, BoolQueryBuilder queryBuilder) {
		String createUserName="createUserName";
		String updateUserName="updateUserName";
		String ownerUserName="ownerUserName";
		if (Arrays.asList(createUserName, updateUserName, ownerUserName).contains(search.getFieldName())) {
			search.setFieldName(search.getFieldName().replace("Name", "Id"));
		}
		switch (search.getSearchEnum()) {
			case CONTAINS:
				queryBuilder.filter(QueryBuilders.termsQuery(search.getFieldName(), search.getValues()));
				break;
			case NOT_CONTAINS:
				queryBuilder.mustNot(QueryBuilders.termsQuery(search.getFieldName(), search.getValues()));
				break;
			case IS_NULL:
				isNullSearch(search, queryBuilder);
				break;
			case IS_NOT_NULL:
				isNotNullSearch(search, queryBuilder);
				break;
			default:break;
		}
	}

	/**
	 * 为空搜索
	 *
	 * @param search       搜索条件
	 * @param queryBuilder 查询器
	 */
	private static void isNullSearch(SearchEntityBO search, BoolQueryBuilder queryBuilder) {
		ModuleFieldEnum fieldEnum = ModuleFieldEnum.parse(search.getFormType());
		ModuleSimpleFieldBO detailTableField = search.getDetailTableField();
		if (ObjectUtil.isNotNull(detailTableField)) {
			String name = String.join(".", detailTableField.getFieldName(), search.getFieldName());
			NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), QueryBuilders.existsQuery(name), ScoreMode.None);
			queryBuilder.mustNot(nestedQueryBuilder);
		} else {
			if (Arrays.asList(ModuleFieldEnum.DATETIME, ModuleFieldEnum.DATE, ModuleFieldEnum.NUMBER, ModuleFieldEnum.FLOATNUMBER, ModuleFieldEnum.PERCENT).contains(fieldEnum)) {
				queryBuilder.mustNot(QueryBuilders.existsQuery(search.getFieldName()));
			} else {
				BoolQueryBuilder builder = QueryBuilders.boolQuery();
				builder.should(QueryBuilders.termQuery(search.getFieldName(), ""));
				builder.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(search.getFieldName())));
				queryBuilder.filter(builder);
			}
		}
	}

	/**
	 * 不为空搜索
	 *
	 * @param search       搜索条件
	 * @param queryBuilder 查询器
	 */
	private static void isNotNullSearch(SearchEntityBO search, BoolQueryBuilder queryBuilder) {
		ModuleFieldEnum fieldEnum = ModuleFieldEnum.parse(search.getFormType());
		ModuleSimpleFieldBO detailTableField = search.getDetailTableField();
		if (ObjectUtil.isNotNull(detailTableField)) {
			String name = String.join(".", detailTableField.getFieldName(), search.getFieldName());
			NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(detailTableField.getFieldName(), QueryBuilders.existsQuery(name), ScoreMode.None);
			queryBuilder.filter(nestedQueryBuilder);
		} else {
			queryBuilder.filter(QueryBuilders.existsQuery(search.getFieldName()));
			if (!Arrays.asList(ModuleFieldEnum.DATETIME, ModuleFieldEnum.DATE, ModuleFieldEnum.NUMBER, ModuleFieldEnum.FLOATNUMBER, ModuleFieldEnum.PERCENT).contains(fieldEnum)) {
				queryBuilder.mustNot(QueryBuilders.termQuery(search.getFieldName(), ""));
			}
		}
	}

    /**
     * 初始化成elastic数据类型
     *
     * @param fieldType 字段类型
     * @return esType 1 keyword 2 date 3 number 4 nested 5 datetime 6 detailTable
     */
    public static int parseType(Integer fieldType) {
        Integer[] nested = new Integer[]{ModuleFieldEnum.SELECT.getType(), ModuleFieldEnum.CHECKBOX.getType(),
                ModuleFieldEnum.AREA_POSITION.getType(), ModuleFieldEnum.DATE_INTERVAL.getType(), ModuleFieldEnum.TAG.getType()};
        Integer[] number = new Integer[]{ModuleFieldEnum.NUMBER.getType(), ModuleFieldEnum.FLOATNUMBER.getType(), ModuleFieldEnum.PERCENT.getType()};
        Integer[] date = new Integer[]{ModuleFieldEnum.DATE.getType(), ModuleFieldEnum.DATETIME.getType()};
        if (Arrays.asList(nested).contains(fieldType)) {
            return 4;
        }
        if (Arrays.asList(date).contains(fieldType)) {
            if (Objects.equals(ModuleFieldEnum.DATE.getType(), fieldType)) {
                return 2;
            } else {
                return 5;
            }
        }
		if (ObjectUtil.equal(ModuleFieldEnum.DETAIL_TABLE.getType(), fieldType)) {
			return 6;
		}
        if (Arrays.asList(number).contains(fieldType)) {
            return 3;
        }
        return 1;
    }

	@Getter
	public static class BaseField {

		private final String name;

		private final Integer type;

		public BaseField(String name, Integer type) {
			this.name = name;
			this.type = type;
		}
	}
}
