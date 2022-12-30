package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.kakarote.common.constant.Const;
import com.kakarote.common.entity.SimpleUser;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.redis.Redis;
import com.kakarote.common.result.SystemCodeEnum;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.upload.entity.UploadEntity;
import com.kakarote.common.upload.service.FileService;
import com.kakarote.common.utils.BaseUtil;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.service.UserService;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.common.EasyExcelParseUtil;
import com.kakarote.module.common.ElasticUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.constant.ModuleConst;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.ModuleFieldBO;
import com.kakarote.module.entity.BO.ModuleFieldDataSaveBO;
import com.kakarote.module.entity.BO.ModuleOptionsBO;
import com.kakarote.module.entity.BO.UploadExcelBO;
import com.kakarote.module.entity.PO.Message;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleFieldData;
import com.kakarote.module.entity.PO.ModuleFieldUnion;
import com.kakarote.module.service.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wwl
 */
@Service
@Slf4j
public class ModuleUploadExcelServiceImpl implements IModuleUploadExcelService {
    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private Redis redis;

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    /**
     * 上传excel可查询时间
     */
    protected static final int UPLOAD_EXCEL_EXIST_TIME = 600;

    private static final String MAP_ADDRESS = "mapAddress";

    /**
     * 获取临时文件路径,用完记得删除
     *
     * @param file file
     * @return path
     */
    private String getFilePath(MultipartFile file) {
        String dirPath = FileUtil.getTmpDirPath();
        try {
            InputStream inputStream = file.getInputStream();
            File fromStream = FileUtil.writeFromStream(inputStream, dirPath + "/" + IdUtil.simpleUUID() + file.getOriginalFilename());
            return fromStream.getAbsolutePath();
        } catch (IOException e) {
            throw new BusinessException(SystemCodeEnum.SYSTEM_ERROR);
        }
    }

    @Override
    public Long uploadExcel(MultipartFile file, UploadExcelBO uploadExcelBO) {
        String filePath = getFilePath(file);
        uploadExcelBO.setFilePath(filePath);

        Long messageId = BaseUtil.getNextId();
        ModuleEntity module = (ModuleEntity) uploadExcelBO.getParam().get("module");
        Message message = new Message();
        message.setMessageId(messageId);
        message.setValue("");
        message.setModuleId(uploadExcelBO.getModuleId());
        message.setCreateTime(cn.hutool.core.date.DateUtil.date());
        message.setModuleName(module.getName());
        message.setCreateUserId(UserUtil.getUserId());
        message.setReceiver(UserUtil.getUserId());

        message.setType(0);
        message.setBatchId("");
        message.setExtData(null);
        ApplicationContextHolder.getBean(IMessageService.class).saveOneMessage(message);

        UploadService uploadService = new ModuleCommonUploadService();
        uploadExcelBO.setMessageId(messageId);
        uploadService.setUploadExcelBO(uploadExcelBO);
        redis.setex(ModuleConst.UPLOAD_EXCEL_MESSAGE_PREFIX + messageId, UPLOAD_EXCEL_EXIST_TIME, 0);
        TaskExecutor taskExecutor = ApplicationContextHolder.getBean(TaskExecutor.class);
        taskExecutor.execute(uploadService);
        return messageId;
    }

    public abstract class UploadService implements Runnable {

        protected List<List<Object>> errorList = new ArrayList<>();

        protected List<ModuleFieldBO> fieldList = new ArrayList<>();

        protected List<ModuleFieldBO> fixedFieldList = new ArrayList<>();

        protected List<ModuleFieldBO> uniqueList = new ArrayList<>();

        private UploadExcelBO uploadExcelBO;

        private Map<String, Long> userCacheMap;

        // 有表格标志
        protected Boolean haveTableFieldFlag = false;
        // 主字段id
        protected ModuleFieldBO mainField = new ModuleFieldBO();
        protected Integer tableNum = 0;
        protected String batchId;
        // 导入时，数据关联字段所在的列index
        protected List<Integer> unionIndex = new ArrayList<>();
        // 导入时，数据关联字段那一列的所有的值
        protected Map<Long, List<String>> unionId2StrMap = new HashMap<>();
        // 导入时，数据关联字段那一列的所有的值转换为data对象
        protected Map<Long, List<ModuleFieldData>> unionId2DataMap = new HashMap<>();

        protected Map<String, Object> mainDataErrFlag = new HashMap<>();

        /**
         * 导入数量
         */
        protected Integer num = -2;

        /**
         * 修改数量
         */
        protected Integer updateNum = 0;

        /**
         * 跳过数量
         */
        protected Integer skipNum = 0;

        /**
         * 非空字段所在列index
         */
        protected List<Integer> isNullList = new ArrayList<>();

        /**
         * 根据最新字段找到的 新非空字段所在列index的list
         */
        protected List<Integer> newIsNullList = new ArrayList<>();

        protected boolean templateErr = false;

        protected JSONObject kv = new JSONObject();

        public abstract void importExcel();

        public UploadExcelBO getUploadExcelBO() {
            return uploadExcelBO;
        }

        private void setUploadExcelBO(UploadExcelBO uploadExcelBO) {
            this.uploadExcelBO = uploadExcelBO;
            List<Long> longs = userService.queryUserList(1).getData();
            List<SimpleUser> simpleUsers = UserCacheUtil.getSimpleUsers(longs);
            userCacheMap = new HashMap<>(simpleUsers.size(), 1.0f);
            for (SimpleUser simpleUser : simpleUsers) {
                userCacheMap.put(simpleUser.getNickname(), simpleUser.getUserId());
            }
        }

        @Override
        public void run() {
            boolean exists = redis.exists(ModuleConst.UPLOAD_EXCEL_MESSAGE_PREFIX + getUploadExcelBO().getMessageId());
            if (!exists) {
                return;
            }
            try {
                UserUtil.setUser(getUploadExcelBO().getUserInfo());
                importExcel();
                int dataStartRowIndex = haveTableFieldFlag ? 3 : 2;
                Map<String, Object> param = getUploadExcelBO().getParam();
                if (errorList.size() > dataStartRowIndex) {
                    List<ModuleFieldBO> needFields = (List<ModuleFieldBO>) param.get("needFields");
                    List<ModuleFieldBO> allFields = (List<ModuleFieldBO>) param.get("allFields");
                    List<List<String>> strDataList = errorList.stream().skip(dataStartRowIndex).map(a -> a.stream().map(String::valueOf).collect(Collectors.toList())).collect(Collectors.toList());
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    EasyExcelParseUtil.exportExcel(outputStream, strDataList, needFields, allFields);
                    UploadEntity uploadEntity = new UploadEntity();
                    uploadEntity.setFileId(BaseUtil.getNextId().toString());
                    uploadEntity.setName(IdUtil.simpleUUID() + ".xlsx");
                    //上传OSS
                    fileService.uploadTempFile(new ByteArrayInputStream(outputStream.toByteArray()), uploadEntity);
                    redis.setex(ModuleConst.UPLOAD_EXCEL_MESSAGE_PREFIX + "file:" + getUploadExcelBO().getMessageId().toString(), 604800, uploadEntity.getPath());
                }
                // 导入总数据0条，覆盖0条，导入成功0条，导入失败0条。
                // msg.setValue(   Math.max(num, 0) + "," + errorCount + "," + updateNum + "," + skipNum);
                int mount = haveTableFieldFlag ? tableNum - 1 : Math.max(num, 0);
                Integer repeatHandling = uploadExcelBO.getRepeatHandling();
                // 失败数目
                int errorCount = haveTableFieldFlag ? mainDataErrFlag.size() : Math.max((errorList.size() - dataStartRowIndex), 0);

                String skipOrCoverMsg;
                if (ObjectUtil.equal(1, repeatHandling)) {
                    skipOrCoverMsg = " 覆盖数量" + updateNum + "条";
                } else {
                    skipOrCoverMsg = " 跳过数量" + skipNum + "条";
                }
                ModuleEntity module = (ModuleEntity) param.get("module");
                String msg = module.getName()
                        + "导入总数据" + mount + "条"
                        + Const.SEPARATOR + skipOrCoverMsg
                        + Const.SEPARATOR + " 导入成功" + Math.max((mount - errorCount), 0) + "条"
                        + Const.SEPARATOR + " 导入失败" + errorCount + "条";
                ApplicationContextHolder.getBean(IMessageService.class).lambdaUpdate()
                        .set(Message::getValue, msg)
                        .set(Message::getExtData, String.valueOf(errorCount))
                        .set(Message::getBatchId, batchId)
                        .eq(Message::getMessageId, getUploadExcelBO().getMessageId())
                        .update();
                redis.del(ModuleConst.UPLOAD_EXCEL_MESSAGE_PREFIX + getUploadExcelBO().getMessageId());
                FileUtil.del(getUploadExcelBO().getFilePath());
            } catch (Exception e) {
                log.error("导入出现错误", e);
            } finally {
                UserUtil.removeUser();
                redis.del(ModuleConst.UPLOAD_EXCEL_MESSAGE_PREFIX + getUploadExcelBO().getMessageId());
                FileUtil.del(getUploadExcelBO().getFilePath());
            }
        }

        /**
         * 查询导入顺序
         *
         * @param rowList 行数据
         * @param flag    是否有表格
         * @author zhangzhiwei
         */
        protected void queryExcelHead(List<Object> rowList, Boolean flag, Boolean haveUnionFlag) {
            Map<String, Object> param = getUploadExcelBO().getParam();
            List<ModuleFieldBO> needFields = (List<ModuleFieldBO>) param.get("needFields");
            List<ModuleFieldBO> allFields = (List<ModuleFieldBO>) param.get("allFields");
            List<ModuleFieldBO> haveTableFields = new ArrayList<>();
            mainField = needFields.get(0);
            // 有表格
            if (flag) {
                for (ModuleFieldBO field : needFields) {
                    if (ObjectUtil.equal(ModuleFieldEnum.DETAIL_TABLE.getType(), field.getType())) {
                        List<ModuleFieldBO> fieldsInTable = allFields.stream().filter(f -> ObjectUtil.equal(f.getGroupId(), field.getGroupId()) && ObjectUtil.notEqual(f.getFieldId(), field.getFieldId())).collect(Collectors.toList());
                        haveTableFields.addAll(fieldsInTable);
                    } else {
                        haveTableFields.add(field);
                    }
                }
                fieldList = haveTableFields;
            }
            // 无表格
            else {
                fieldList = needFields;
            }

            // 删除一些字段
            fieldList.removeIf(f -> EasyExcelParseUtil.removeFieldByType(f.getType()));
            Map<String, String> nameMap = new HashMap<>(16);
            Map<String, Integer> isNullMap = new HashMap<>(16);
            List<Integer> unionTypes = Arrays.asList(ModuleFieldEnum.DATA_UNION.getType(), ModuleFieldEnum.DATA_UNION_MULTI.getType());
            for (int i = 0; i < fieldList.size(); i++) {
                ModuleFieldBO f = fieldList.get(i);
                if (haveUnionFlag) {
                    if (unionTypes.contains(f.getType())) {
                        unionId2StrMap.put(f.getFieldId(), new ArrayList<>());
                        unionId2DataMap.put(f.getFieldId(), new ArrayList<>());
                        unionIndex.add(i);
                    }
                }

                // 如果是文本框，且不是地址
                if (Objects.equals(ModuleFieldEnum.TEXT.getType(), f.getFieldType()) && !MAP_ADDRESS.equals(f.getFieldName())) {
                    fixedFieldList.add(f);
                }
                // 字段是否唯一, 不管自定义编号字段
                if (Objects.equals(1, f.getIsUnique())) {
                    if (ObjectUtil.notEqual(f.getType(), ModuleFieldEnum.SERIAL_NUMBER.getType())) {
                        uniqueList.add(f);
                    }
                }
                // 是否必填 , 不管自定义编号字段
                boolean isNull = Objects.equals(1, f.getIsNull());
                if (isNull && !haveUnionFlag) {
                    if (ObjectUtil.notEqual(f.getType(), ModuleFieldEnum.SERIAL_NUMBER.getType())) {
                        newIsNullList.add(i);
                    }
                }
                nameMap.put((ObjectUtil.isNotNull(f.getGroupId()) ? f.getGroupId() : "") + (isNull ? "*" : "") + f.getName(), f.getFieldName());
                isNullMap.put((ObjectUtil.isNotNull(f.getGroupId()) ? f.getGroupId() : "") + (isNull ? "*" : "") + f.getName(), f.getIsNull());
            }
            if (haveUnionFlag) {
                fixedFieldList.clear();
                uniqueList.clear();
                return;
            }
            List<Object> nameList = new ArrayList<>(nameMap.keySet());
            if (ObjectUtil.notEqual(nameList.size(), rowList.size())) {
                templateErr = true;
            } else {
                for (int i = 0; i < rowList.size(); i++) {
                    String headName = rowList.get(i).toString();
                    headName = ObjectUtil.isNotNull(fieldList.get(i).getGroupId()) ? (fieldList.get(i).getGroupId() + headName) : (headName);
                    kv.put(nameMap.get(headName), i);
                    if (Objects.equals(1, isNullMap.get(rowList.get(i).toString()))) {
                        //  不管自定义编号字段
                        if (ObjectUtil.notEqual(fieldList.get(i).getType(), ModuleFieldEnum.SERIAL_NUMBER.getType())) {
                            isNullList.add(i);
                        }
                    }
                }
            }
            // 比较导入模板是否最新
            if (ObjectUtil.notEqual(isNullList.size(), newIsNullList.size())) {
                templateErr = true;
            } else {
                for (int i = 0; i < isNullList.size(); i++) {
                    if (ObjectUtil.notEqual(isNullList.get(i), newIsNullList.get(i))) {
                        templateErr = true;
                    }
                }
            }
            rowList.add(0, "错误原因");
            errorList.add(rowList);
        }

        /**
         * 查询唯一数据
         *
         * @return data
         */
        protected List<Map<String, Object>> uniqueMapList(List<ModuleFieldBO> uniqueList) {
            if (uniqueList.size() == 0) {
                return new ArrayList<>();
            }
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            sourceBuilder.size(2);
            sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            uniqueList.forEach(moduleFieldBO -> {
                Object value = moduleFieldBO.getImportValue();
                if (ObjectUtil.isNotEmpty(value)) {
                    if (Objects.equals(moduleFieldBO.getType(), ModuleFieldEnum.DATE.getType()) && value instanceof Long) {
                        Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate((Long) value);
                        moduleFieldBO.setImportValue(cn.hutool.core.date.DateUtil.formatDate(date));
                    }
                    queryBuilder.should(QueryBuilders.termQuery(moduleFieldBO.getFieldName(), moduleFieldBO.getImportValue()));
                }
            });
            queryBuilder.minimumShouldMatch(1);
            queryBuilder.filter(QueryBuilders.termQuery("moduleId", Objects.requireNonNull(getUploadExcelBO().getModuleId())));
            sourceBuilder.query(queryBuilder);
            SearchRequest searchRequest = new SearchRequest(ElasticUtil.getIndexName(getUploadExcelBO().getModuleId()));
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse = restTemplate.execute(client -> client.search(searchRequest, RequestOptions.DEFAULT));
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                mapList.add(hit.getSourceAsMap());
            }
            return mapList;
        }

        protected String getValueByCellStr(ModuleFieldBO moduleFieldBO, Object cellValue) throws Exception {
            String value = "";
            if (ObjectUtil.isNotNull(cellValue) && ObjectUtil.isNotEmpty(cellValue)) {
                ModuleFieldEnum type = ModuleFieldEnum.parse(moduleFieldBO.getType());
                switch (type) {
                    case SERIAL_NUMBER:{
                        value = "";
                        break;
                    }
                    case PERCENT:
                        value = new BigDecimal(cellValue.toString()).divide(new BigDecimal("100")).toString();
                        break;
                    case SELECT:
                        for (ModuleOptionsBO option : moduleFieldBO.getOptionsList()) {
                            if (ObjectUtil.equal(option.getValue(), cellValue.toString())) {
                                value = JSONObject.toJSONString(option);
                                break;
                            }
                        }
                        break;
                    case DATA_UNION:
                    case DATA_UNION_MULTI:
                        List<ModuleFieldData> dataList = unionId2DataMap.get(moduleFieldBO.getFieldId());
                        List<String> dataIds = new ArrayList<>();
                        String[] split = cellValue.toString().split(Const.SEPARATOR);
                        for (String val : split) {
                            List<ModuleFieldData> valList = dataList.stream().filter(d -> ObjectUtil.equal(d.getValue(), val)).collect(Collectors.toList());
                            if (CollUtil.isNotEmpty(valList)) {
                                if (ObjectUtil.equal(valList.size(), 1)) {
                                    dataIds.add(valList.get(0).getDataId().toString());
                                } else {
                                    throw new Exception("module,"+moduleFieldBO.getName() + "字段在关联表中匹配到多条数据");
                                }
                            } else {
                                throw new Exception("module,"+moduleFieldBO.getName() + "字段在关联表中不存在");
                            }
                        }
                        value = String.join(Const.SEPARATOR, dataIds);
                        break;
                    case DATE:
                        try {
                            value = DateUtil.formatDate(DateUtil.parse(cellValue.toString()));
                        }catch (Exception ex) {
                            throw new Exception("module,"+moduleFieldBO.getName() + "的值格式错误");
                        }
                        // excel已经设定了 yyyy-mm-dd格式，但是值还是自动追加了时分秒，这里匹配错误，并且生成错误信息文档时写入也进不去，会报错。
                        // boolean match = ReUtil.isMatch("^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$", cellValue.toString());
                        // if (match) {
                        //     value = cellValue.toString();
                        // } else {
                        //    throw new Exception("module,"+moduleFieldBO.getName() + "的值格式错误");
                        // }
                        break;
                    case DATETIME:
                        boolean matchTime = ReUtil.isMatch("^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\s+(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d$", cellValue.toString());
                        if (matchTime) {
                            value = cellValue.toString();
                        } else {
                            throw new Exception("module,"+moduleFieldBO.getName() + "的值格式错误");
                        }
                        break;
                    case ATTENTION:
                        value = EasyExcelParseUtil.attentionStr2Value(cellValue.toString());
                        break;
                    default:
                        value = cellValue.toString();
                        break;
                }
            }
            return value;
        }

        /**
         * 将表格的数据添加进表格字段的 json数组字符串中
         *
         * @param moduleFieldData
         * @param rowList
         */
        protected void addValue2TableField(ModuleFieldDataSaveBO moduleFieldData, List<Object> rowList) throws Exception {
            Map<String, Object> param = getUploadExcelBO().getParam();
            List<ModuleFieldBO> allFields = (List<ModuleFieldBO>) param.get("allFields");
            Map<Integer, ModuleFieldBO> groupId2TableMap = allFields.stream().filter(f -> ObjectUtil.isNotNull(f.getGroupId()) && ObjectUtil.equal(f.getType(), ModuleFieldEnum.DETAIL_TABLE.getType())).collect(Collectors.toMap(ModuleFieldBO::getGroupId, Function.identity()));
            Map<String, AbstractMap.SimpleEntry<ModuleFieldBO, Object>> tableValue = new HashMap<>();
            // 数据放进map 好拿取
            for (int i = 0; i < fieldList.size(); i++) {
                ModuleFieldBO moduleFieldBO = fieldList.get(i);
                Object cellValue = rowList.get(i);
                if (ObjectUtil.isNotNull(moduleFieldBO.getGroupId())) {
                    tableValue.put(moduleFieldBO.getGroupId() + Const.SEPARATOR + moduleFieldBO.getFieldName(), new AbstractMap.SimpleEntry<>(moduleFieldBO, cellValue));
                }
            }
            List<ModuleFieldData> fieldDataList = moduleFieldData.getFieldDataList();
            for (Map.Entry<Integer, ModuleFieldBO> tableEntry : groupId2TableMap.entrySet()) {
                for (ModuleFieldData fieldData : fieldDataList) {
                    if (ObjectUtil.equal(fieldData.getFieldId(), tableEntry.getValue().getFieldId())) {
                        String oldValue = fieldData.getValue();
                        JSONArray oldValueArray = JSONObject.parseArray(oldValue);
                        JSONObject jsonObject = new JSONObject();
                        for (Map.Entry<String, AbstractMap.SimpleEntry<ModuleFieldBO, Object>> entry : tableValue.entrySet()) {
                            String[] split = entry.getKey().split(Const.SEPARATOR);
                            if (ObjectUtil.equal(Integer.parseInt(split[0]), tableEntry.getKey())) {
                                ModuleFieldBO moduleFieldBO = entry.getValue().getKey();
                                Object cellStr = entry.getValue().getValue();
                                String value = "";
                                if (ObjectUtil.isNotNull(cellStr)) {
                                    value = getValueByCellStr(moduleFieldBO, cellStr);
                                }
                                jsonObject.put(split[1], value);
                            }
                        }
                        oldValueArray.add(jsonObject);
                        fieldData.setValue(oldValueArray.toJSONString());
                    }
                }
            }
            moduleFieldData.setFieldDataList(fieldDataList);
        }

        private ModuleFieldData createModuleFieldData(ModuleFieldBO moduleFieldBO, String value) {
            ModuleFieldData fieldData = new ModuleFieldData();
            fieldData.setFieldId(moduleFieldBO.getFieldId());
            fieldData.setFieldName(moduleFieldBO.getFieldName());
            fieldData.setValue(value);
            fieldData.setModuleId(getUploadExcelBO().getModuleId());
            fieldData.setVersion(getUploadExcelBO().getVersion());
            fieldData.setCreateTime(new Date());
            return fieldData;
        }

        /**
         * 将自定义字段组装成想要的格式
         *
         * @param rowList 行数据
         * @return 自定义字段数组
         */
        protected List<ModuleFieldData> addFieldArray(List<Object> rowList) throws Exception {
            Map<String, Object> param = getUploadExcelBO().getParam();
            List<ModuleFieldBO> allFields = (List<ModuleFieldBO>) param.get("allFields");
            Map<Integer, ModuleFieldBO> groupId2TableMap = allFields.stream().filter(f -> ObjectUtil.isNotNull(f.getGroupId()) && ObjectUtil.equal(f.getType(), ModuleFieldEnum.DETAIL_TABLE.getType())).collect(Collectors.toMap(ModuleFieldBO::getGroupId, Function.identity()));
            Map<String, AbstractMap.SimpleEntry<ModuleFieldBO, Object>> tableValue = new HashMap<>();
            List<ModuleFieldData> array = new ArrayList<>();
            // 导入的表中不允许导入的字段也需要空默认值
            allFields.stream()
                    .filter(f -> EasyExcelParseUtil.removeFieldByType(f.getType()))
                    .forEach(f -> array.add(createModuleFieldData(f, "")));

            for (int i = 0; i < fieldList.size(); i++) {
                // 一个字段对应一个值
                ModuleFieldBO moduleFieldBO = fieldList.get(i);
                Object cellValue = rowList.get(i);
                // 表格内的字段的值，可能是多条
                if (ObjectUtil.isNotNull(moduleFieldBO.getGroupId())) {
                    tableValue.put(moduleFieldBO.getGroupId() + Const.SEPARATOR + moduleFieldBO.getFieldName(), new AbstractMap.SimpleEntry<>(moduleFieldBO, cellValue));
                }
                // 表格外字段
                else {
                    String value = this.getValueByCellStr(moduleFieldBO, cellValue);
                    ModuleFieldData fieldData = createModuleFieldData(moduleFieldBO, value);
                    array.add(fieldData);
                }
            }

            // 表格
            for (Map.Entry<Integer, ModuleFieldBO> tableEntry : groupId2TableMap.entrySet()) {
                ModuleFieldData fieldData = new ModuleFieldData();
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                for (Map.Entry<String, AbstractMap.SimpleEntry<ModuleFieldBO, Object>> entry : tableValue.entrySet()) {
                    String[] split = entry.getKey().split(Const.SEPARATOR);
                    if (ObjectUtil.equal(Integer.parseInt(split[0]), tableEntry.getKey())) {
                        ModuleFieldBO moduleFieldBO = entry.getValue().getKey();
                        Object cellStr = entry.getValue().getValue();
                        String value = this.getValueByCellStr(moduleFieldBO, cellStr);
                        jsonObject.put(split[1], value);
                    }
                }
                jsonArray.add(jsonObject);
                fieldData.setFieldId(tableEntry.getValue().getFieldId());
                fieldData.setFieldName(tableEntry.getValue().getFieldName());
                fieldData.setValue(jsonArray.toJSONString());
                fieldData.setModuleId(getUploadExcelBO().getModuleId());
                fieldData.setVersion(getUploadExcelBO().getVersion());
                fieldData.setCreateTime(new Date());
                array.add(fieldData);
            }
            return array;
        }

        /**
         * 获取负责人id
         *
         * @param rowList 行数据
         * @return 最终的负责人id
         */
        Long getOwnerUserIdByRowList(List<Object> rowList) {
            Integer ownerUserName1 = kv.getInteger("ownerUserName");
            Object o = rowList.get(ownerUserName1);
            String ownerUserName = ObjectUtil.isEmpty(o) ? "" : o.toString();
            return userCacheMap.get(ownerUserName);
        }
    }

    public class ModuleCommonUploadService extends UploadService {

        /**
         * 检查是否有表格
         *
         * @return boolean 有true，无false
         */
        private boolean haveDetailTableFLag() {
            Map<String, Object> param = getUploadExcelBO().getParam();
            List<ModuleFieldBO> list = (List<ModuleFieldBO>) param.get("allFields");
            ModuleFieldBO bo = list.stream().filter(f -> ObjectUtil.equal(ModuleFieldEnum.DETAIL_TABLE.getType(), f.getType())).findFirst().orElse(null);
            return ObjectUtil.isNotNull(bo);
        }

        /**
         * 拿到所有数据关联字段
         */
        private  List<Long> getUnionIds() {
            Map<String, Object> param = getUploadExcelBO().getParam();
            List<ModuleFieldBO> list = (List<ModuleFieldBO>) param.get("allFields");
            List<Integer> unionTypes = Arrays.asList(ModuleFieldEnum.DATA_UNION.getType(), ModuleFieldEnum.DATA_UNION_MULTI.getType());
            return list.stream().filter(f -> unionTypes.contains(f.getType())).map(ModuleFieldBO::getFieldId).collect(Collectors.toList());
        }

        /**
         * map中其实仅仅只有一条完整的数据
         *
         * @param oneTotalFiledDataMap 一条完整数据
         */
        private void iterateMap2Save(Map<String, ModuleFieldDataSaveBO> oneTotalFiledDataMap) {
            tableNum++;
            IModuleFieldDataProvider fieldDataProvider = ApplicationContextHolder.getBean(IModuleFieldDataProvider.class);
            for (Map.Entry<String, ModuleFieldDataSaveBO> entry : oneTotalFiledDataMap.entrySet()) {
                ModuleFieldDataSaveBO value = entry.getValue();
                fieldDataProvider.save(value);
            }
            oneTotalFiledDataMap.clear();
        }

        /**
         * 组织数据保存需要的实体类
         *
         * @param rowList 数据
         * @return saveBO
         */
        private ModuleFieldDataSaveBO createModuleFieldDataSaveBO(List<Object> rowList) throws Exception {
            ModuleFieldDataSaveBO dataSave = new ModuleFieldDataSaveBO();
            dataSave.setModuleId(getUploadExcelBO().getModuleId());
            dataSave.setVersion(getUploadExcelBO().getVersion());
            dataSave.setCategoryId(getUploadExcelBO().getCategoryId());
            List<ModuleFieldData> fieldDataList = this.addFieldArray(rowList);
            dataSave.setFieldDataList(fieldDataList);
            return dataSave;
        }

        private void createErrMsg(Exception ex, List<Object> rowList) {
            String message = ex.getMessage();
            if (StrUtil.startWith(message, "module,")) {
                message = message.replace("module,", "");
            } else {
                message = "导入异常";
            }
            log.error("导入数据异常:", ex);
            rowList.add(0, message);
            errorList.add(rowList);
        }

        /**
         * 文件导入的执行方法
         * ExcelUtil.readBySax() 会一行一行读取数据
         * sheetIndex 工作表下标
         * rowIndex 就是行下标
         * rowList 是excel中一行的数据，会按照字段的个数num，取每行的cell数量，每行的cell个数都是num，cell没值就是空字符串
         */
        @Override
        public void importExcel() {
            // 导入的批次id
            batchId = IdUtil.simpleUUID();
            // 包含明细表格的导入与不包含的数据开始行不一样
            haveTableFieldFlag = haveDetailTableFLag();
            List<Long> unionIds = getUnionIds();
            // 数据从第几行开始
            long dataStartRowIndex = haveTableFieldFlag ? 2L : 1L;
            num = haveTableFieldFlag ? -3 : -2;
            // 用来保存一条主数据
            Map<String, ModuleFieldDataSaveBO> oneTotalFiledDataMap = new HashMap<>();
            // 数据关联配置
            if (CollUtil.isNotEmpty(unionIds)) {
                List<ModuleFieldUnion> fieldUnions = ApplicationContextHolder.getBean(IModuleFieldUnionService.class).lambdaQuery()
                        .eq(ModuleFieldUnion::getModuleId, getUploadExcelBO().getModuleId())
                        .eq(ModuleFieldUnion::getVersion, getUploadExcelBO().getVersion())
                        .eq(ModuleFieldUnion::getType, 1)
                        .in(ModuleFieldUnion::getRelateFieldId, unionIds)
                        .list();
                Map<Long, ModuleFieldUnion> relateFieldUnionMap = fieldUnions.stream().collect(Collectors.toMap(ModuleFieldUnion::getRelateFieldId, Function.identity()));
                List<Long> targetModuleIds = fieldUnions.stream().map(ModuleFieldUnion::getTargetModuleId).distinct().collect(Collectors.toList());
                // 目标模块
                List<ModuleEntity> targetModules = ModuleCacheUtil.getActiveByIds(targetModuleIds);
                Map<Long, ModuleEntity> moduleIdMap = targetModules.stream().collect(Collectors.toMap(ModuleEntity::getModuleId, Function.identity()));
                // 读取所有的数据关联的值
                ExcelUtil.readBySax(getUploadExcelBO().getFilePath(), 0, (int sheetIndex, long rowIndex, List<Object> rowList) -> {
                    if (rowIndex > dataStartRowIndex) {
                        for (Integer index : unionIndex) {
                            ModuleFieldBO fieldBO = fieldList.get(index);
                            Object o = rowList.get(index);
                            if (ObjectUtil.isNotNull(o) && ObjectUtil.isNotEmpty(o)) {
                                unionId2StrMap.get(fieldBO.getFieldId()).add(o.toString());
                            }
                        }
                    } else if (ObjectUtil.equal(rowIndex, dataStartRowIndex)) {
                        queryExcelHead(rowList, haveTableFieldFlag, true);
                    }
                });
                for (Integer index : unionIndex) {
                    ModuleFieldBO fieldBO = fieldList.get(index);
                    List<String> strList = unionId2StrMap.get(fieldBO.getFieldId());
                    List<String> oneByOneStrList = strList.stream().flatMap(val -> Arrays.stream(val.split(Const.SEPARATOR))).distinct().collect(Collectors.toList());
                    ModuleFieldUnion fieldUnion = relateFieldUnionMap.get(fieldList.get(index).getFieldId());
                    if (ObjectUtil.isNotNull(fieldUnion) && CollUtil.isNotEmpty(oneByOneStrList)) {
                        ModuleEntity targetModule = moduleIdMap.get(fieldUnion.getTargetModuleId());
                        if (ObjectUtil.isNotNull(targetModule)) {
                            List<ModuleFieldData> dataList = ApplicationContextHolder.getBean(IModuleFieldDataService.class).lambdaQuery()
                                    .eq(ModuleFieldData::getModuleId, targetModule.getModuleId())
                                    .eq(ModuleFieldData::getFieldId, targetModule.getMainFieldId())
                                    .in(ModuleFieldData::getValue, oneByOneStrList)
                                    .list();
                            unionId2DataMap.get(fieldBO.getFieldId()).addAll(dataList);
                        }
                    }
                }
            }

            ExcelUtil.readBySax(getUploadExcelBO().getFilePath(), -1, (int sheetIndex, long rowIndex, List<Object> rowList) -> {
                num++;
                redis.setex(ModuleConst.UPLOAD_EXCEL_MESSAGE_PREFIX + getUploadExcelBO().getMessageId().toString(), UPLOAD_EXCEL_EXIST_TIME, Math.max(num, 0));

                if (rowList.size() < kv.entrySet().size()) {
                    for (int i = rowList.size() - 1; i < kv.entrySet().size(); i++) {
                        rowList.add(null);
                    }
                }
                int bigNumber = 10001;
                if (num > bigNumber) {
                    rowList.add(0, "最多同时导入10000条数据");
                    errorList.add(rowList);
                    return;
                }
                if (rowIndex > dataStartRowIndex) {
                    if (templateErr) {
                        rowList.add(0, "请使用最新的模板");
                        errorList.add(rowList);
                    } else {
                        if (haveTableFieldFlag) {
                            // 一行中的第一列的数据值
                            Object first = CollUtil.getFirst(rowList);
                            if (ObjectUtil.isNotNull(first)) {
                                String mainFieldValue = first.toString();
                                if (StrUtil.isNotEmpty(mainFieldValue)) {
                                    ModuleFieldDataSaveBO moduleFieldData = oneTotalFiledDataMap.get(mainFieldValue);
                                    if (ObjectUtil.isNotNull(moduleFieldData)) {
                                        try {
                                            // 追加表格值到 saveBO
                                            this.addValue2TableField(moduleFieldData, rowList);
                                        } catch (Exception ex) {
                                            createErrMsg(ex, rowList);
                                            return;
                                        }
                                        oneTotalFiledDataMap.put(mainFieldValue, moduleFieldData);
                                    } else {
                                        Object flag = mainDataErrFlag.get(mainFieldValue);
                                        if (ObjectUtil.isNull(flag)) {
                                            this.iterateMap2Save(oneTotalFiledDataMap);
                                            // 在这里处理非空字段
                                            for (Integer integer : newIsNullList) {
                                                if (ObjectUtil.isEmpty(rowList.get(integer))) {
                                                    rowList.add(0, "必填字段未填写");
                                                    errorList.add(rowList);
                                                    return;
                                                }
                                            }
                                            for (ModuleFieldBO moduleFieldBO : uniqueList) {
                                                Object value = rowList.get(kv.getInteger(moduleFieldBO.getFieldName()));
                                                moduleFieldBO.setImportValue(value);
                                            }
                                            // TODO 根据唯一字段查重
                                            List<Map<String, Object>> uniqueCheckResult = uniqueMapList(uniqueList);
                                            boolean isUpdate = false;
                                            if (uniqueCheckResult.size() > 0) {
                                                // 2 就跳过
                                                if (Objects.equals(2, getUploadExcelBO().getRepeatHandling())) {
                                                    skipNum++;
                                                    return;
                                                }
                                                // 数据应该是只有一条，超过1就是重复
                                                if (uniqueCheckResult.size() > 1) {
                                                    rowList.add(0, "数据与多条唯一性字段重复");
                                                    errorList.add(rowList);
                                                    return;
                                                }
                                                isUpdate = true;
                                            }
                                            try {
                                                moduleFieldData = createModuleFieldDataSaveBO(rowList);
                                            } catch (Exception ex) {
                                                mainDataErrFlag.put(mainFieldValue, new Object());
                                                createErrMsg(ex, rowList);
                                                return;
                                            }
                                            moduleFieldData.setBatchId(batchId);
                                            // 查重并覆盖值
                                            if (ObjectUtil.equal(1, uniqueCheckResult.size())) {
                                                moduleFieldData.setDataId(TypeUtils.castToLong(uniqueCheckResult.get(0).get("dataId")));
                                            }
                                            oneTotalFiledDataMap.put(mainFieldValue, moduleFieldData);
                                            if (isUpdate) {
                                                updateNum++;
                                            }
                                        } else {
                                            rowList.add(0, "主数据错误");
                                            errorList.add(rowList);
                                        }
                                    }
                                }
                                else {
                                    rowList.add(0, "第一列主字段不能为空");
                                    errorList.add(rowList);
                                }
                            }
                            else {
                                rowList.add(0, "第一列主字段不能为空");
                                errorList.add(rowList);
                            }
                        }
                        // 无表格
                        else {
                            try {
                                for (Integer integer : newIsNullList) {
                                    if (ObjectUtil.isEmpty(rowList.get(integer))) {
                                        rowList.add(0, "必填字段未填写");
                                        errorList.add(rowList);
                                        return;
                                    }
                                }
                                for (ModuleFieldBO moduleFieldBO : uniqueList) {
                                    Object value = rowList.get(kv.getInteger(moduleFieldBO.getFieldName()));
                                    moduleFieldBO.setImportValue(value);
                                }
                                // TODO 根据唯一字段查重
                                List<Map<String, Object>> uniqueCheckResult = uniqueMapList(uniqueList);
                                boolean isUpdate = false;
                                if (uniqueCheckResult.size() > 0) {
                                    // 2 就跳过
                                    if (Objects.equals(2, getUploadExcelBO().getRepeatHandling())) {
                                        skipNum++;
                                        return;
                                    }
                                    // 数据应该是只有一条，超过1就是重复
                                    if (uniqueCheckResult.size() > 1) {
                                        rowList.add(0, "数据与多条唯一性字段重复");
                                        errorList.add(rowList);
                                        return;
                                    }
                                    isUpdate = true;
                                }
                                try {
                                    ModuleFieldDataSaveBO dataSaveBO = createModuleFieldDataSaveBO(rowList);
                                    dataSaveBO.setBatchId(batchId);
                                    // 查重并覆盖值
                                    if (ObjectUtil.equal(1, uniqueCheckResult.size())) {
                                        dataSaveBO.setDataId(TypeUtils.castToLong(uniqueCheckResult.get(0).get("dataId")));
                                    }
                                    ApplicationContextHolder.getBean(IModuleFieldDataProvider.class).save(dataSaveBO);
                                } catch (Exception ex) {
                                    createErrMsg(ex, rowList);
                                    return;
                                }
                                if (isUpdate) {
                                    updateNum++;
                                }
                            } catch (Exception ex) {
                                log.error("导入数据异常:", ex);
                                rowList.add(0, "导入异常");
                                errorList.add(rowList);
                            }
                        }
                    }
                } else if (ObjectUtil.equal(rowIndex, dataStartRowIndex)) {
                    queryExcelHead(rowList, haveTableFieldFlag, false);
                } else {
                    errorList.add(0, rowList);
                }
            });
            // 最后一条数据的下一行为空，监听器不再执行，所以在这里保存一下
            if (haveTableFieldFlag) {
                this.iterateMap2Save(oneTotalFiledDataMap);
            }
        }
    }
}
