package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.kakarote.common.constant.Const;
import com.kakarote.common.entity.SimpleUser;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.redis.Redis;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.SystemCodeEnum;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.BaseUtil;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.common.EasyExcelParseUtil;
import com.kakarote.module.constant.ModuleCodeEnum;
import com.kakarote.module.constant.ModuleConst;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.entity.VO.ModuleFieldVO;
import com.kakarote.module.mapper.ModulePrintTemplateMapper;
import com.kakarote.module.service.*;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wwl
 * @date 2022/3/9 14:11
 */
@Service
public class ModulePrintTemplateServiceImpl extends BaseServiceImpl<ModulePrintTemplateMapper, ModulePrintTemplate> implements IModulePrintTemplateService, IFlowCommonService {

    @Autowired
    private Redis redis;
    @Autowired
    private IModulePrintRecordService modulePrintRecordService;

    @Override
    public BasePage<ModulePrintTemplate> queryPrintTemplateList(Long moduleId, Integer version, PageEntity templateBO) {
        LambdaQueryChainWrapper<ModulePrintTemplate> wrapper = lambdaQuery();
        wrapper.eq(ModulePrintTemplate::getModuleId, moduleId);
        wrapper.eq(ModulePrintTemplate::getVersion, version);
        wrapper.orderByAsc(ModulePrintTemplate::getCreateTime);
        BasePage<ModulePrintTemplate> page = wrapper.page(templateBO.parse());
        page.getList().forEach(template -> {
            template.setCreateUserName(UserCacheUtil.getUserName(template.getCreateUserId()));
            template.setUpdateUserName(ObjectUtil.isNotNull(template.getUpdateUserId()) ? UserCacheUtil.getUserName(template.getUpdateUserId()) : null);
        });
        return page;
    }

    private StringBuilder getValueByFieldId(Map<Long, ModuleFieldData> dataMap, ModuleFieldBO field) {
        Integer type = field.getType();
        ModuleFieldEnum parse = ModuleFieldEnum.parse(type);
        StringBuilder result = new StringBuilder();
        ModuleFieldData moduleFieldData = dataMap.get(field.getFieldId());
        if (ObjectUtil.isNotNull(moduleFieldData)) {
            if (StrUtil.isNotEmpty(moduleFieldData.getValue())) {
                String value = moduleFieldData.getValue();
                switch (parse) {
                    case DATA_UNION:
                    case DATA_UNION_MULTI:
                        JSONArray fieldData = (JSONArray) JSONObject.parseObject(value).get("fieldData");
                        result = new StringBuilder(fieldData.stream().map(o -> ((JSONObject) o).getString("value")).collect(Collectors.joining(Const.SEPARATOR)));
                        break;
                    case SELECT:
                        ModuleOptionsBO option = JSON.parseObject(value, ModuleOptionsBO.class);
                        result = new StringBuilder(option.getValue());
                        break;
                    case TAG:
                        // List<JSONObject> tagList = JSON.parseArray(value, JSONObject.class);
                        // result = new StringBuilder(tagList.stream().map(o -> o.getString("name")).collect(Collectors.joining(Const.SEPARATOR)));
                        // break;
                    case CHECKBOX:
                        List<ModuleOptionsBO> objs = JSON.parseArray(value, ModuleOptionsBO.class);
                        result = new StringBuilder(objs.stream().map(ModuleOptionsBO::getValue).collect(Collectors.joining(Const.SEPARATOR)));
                        break;
                    case FLOATNUMBER:
                        result = new StringBuilder(value);
                        break;
                    case PERCENT:
                        result = new StringBuilder(new BigDecimal(value).multiply(new BigDecimal("100")) + "%");
                        break;
                    case DATE_INTERVAL:
                        CommonESNestedBO dateFromTo = JSON.parseObject(value, CommonESNestedBO.class);
                        result = new StringBuilder(dateFromTo.getFromDate() + "至" + dateFromTo.getToDate());
                        break;
                    case AREA_POSITION:
                        List<CommonESNestedBO> address = JSON.parseArray(value, CommonESNestedBO.class);
                        result = new StringBuilder(address.stream().map(CommonESNestedBO::getName).collect(Collectors.joining("-")));
                        break;
                    case CURRENT_POSITION:
                        JSONObject json = JSON.parseObject(value);
                        result = new StringBuilder(json.getString("address"));
                        break;
                    case USER:
                        List<SimpleUser> simpleUsers = JSON.parseArray(value, SimpleUser.class);
                        result = new StringBuilder(simpleUsers.stream().map(SimpleUser::getNickname).collect(Collectors.joining(Const.SEPARATOR)));
                        break;
                    case STRUCTURE:
                        List<String> departments = JSON.parseArray(value, String.class);
                        result = new StringBuilder(String.join(",", departments));
                        break;
                    case HANDWRITING_SIGN:
//                        TODO:
//                        Result<FileEntity> queryOne = ApplicationContextHolder.getBean(AdminFileService.class).queryOne(value);
//                        String img = "<img src=\"" + queryOne.getData().getUrl() + "\" alt=\"\" width=\"200\" height=\"200\" />";
//                        result = new StringBuilder(img);
                        break;
                    case ATTENTION:
                        result = new StringBuilder(EasyExcelParseUtil.attentionValue2Str(value));
                        break;
                    default:
                        result = new StringBuilder(value);
                        break;
                }
            }
        }
        return result;
    }

    /**
     * @param dataMap             所有数据值，明细表格数据也在里边，是一个json数组字符串
     * @param content             是打印模板内容
     * @param tableHtmlList       内容中的table标签
     * @param moduleId2FieldVoMap 模块id -> fields
     * @return 替换后的content
     */
    private String appendTableContent(Map<Long, ModuleFieldData> dataMap
            , String content
            , List<String> tableHtmlList
            , Map<Long, ModuleFieldVO> moduleId2FieldVoMap
            , Long moduleId
            , List<String> unionFieldValues
            , IModuleFieldDataService fieldDataService
            , IModuleFieldDataProvider fieldDataProvider
    ) {
        for (String tableHtml : tableHtmlList) {
            String tableMid = ReUtil.getGroup0("<tr((?!header).)*data-wk-table-tr-tag=\"value\">.*?</tr>?", tableHtml);
            List<String> spanList = ReUtil.findAllGroup0("<span.*?data-wk-table-value-tag=\".*?\".*?</span>", tableMid);
            StringBuilder tableContent = new StringBuilder();
            String key = ReUtil.getGroup1("<table.*?data-wk-table-value-tag=\"(.*?)\".*?<tbody>", tableHtml);
            String groupId = ReUtil.getGroup1("<table.*?data-wk-table-groupid=\"(.*?)\".*?<tbody>", tableHtml);
            if (ObjectUtil.isNull(key) || ObjectUtil.isNull(groupId)) {
                throw new BusinessException(ModuleCodeEnum.PRINT_TEMPLATE_NOT_LATEST);
            }
            String[] moduleIdTableId = key.split("\\.");
            ModuleFieldVO moduleFieldVO = moduleId2FieldVoMap.get(TypeUtils.castToLong(moduleIdTableId[0]));
            List<ModuleFieldBO> fieldList = moduleFieldVO.getFieldList();
            Map<Long, AbstractMap.SimpleEntry<String, Integer>> fieldId2FieldNameTypeMap = new HashMap<>();
            fieldList.stream()
                    .filter(f -> ObjectUtil.equal(Integer.parseInt(groupId), f.getGroupId()))
                    .forEach(f -> {
                        fieldId2FieldNameTypeMap.put(f.getFieldId(), new AbstractMap.SimpleEntry<>(f.getFieldName(), f.getType()));
                    });
            String tableValue = "";
            // 本模块表格
            if (ObjectUtil.equal(TypeUtils.castToLong(moduleIdTableId[0]), moduleId)) {
                ModuleFieldData tableData = dataMap.get(TypeUtils.castToLong(moduleIdTableId[1]));
                tableValue = tableData.getValue();
            }
            // 其它模块表格
            else {
                if (CollUtil.isNotEmpty(unionFieldValues)) {
                    for (String dataId : unionFieldValues) {
                        ModuleFieldData one = fieldDataService.lambdaQuery().select(ModuleFieldData::getModuleId).eq(ModuleFieldData::getDataId, dataId).one();
                        if (ObjectUtil.equal(one.getModuleId().toString(), moduleIdTableId[0])) {
                            ModuleFieldDataResponseBO targetFieldData = fieldDataProvider.queryById(TypeUtils.castToLong(dataId), true);
                            Map<Long, ModuleFieldData> otherDataMap = targetFieldData.getFieldDataList().stream().collect(Collectors.toMap(ModuleFieldData::getFieldId, Function.identity()));
                            ModuleFieldData tableData = otherDataMap.get(TypeUtils.castToLong(moduleIdTableId[1]));
                            tableValue = tableData.getValue();
                            break;
                        }
                    }
                }
            }
            if (ObjectUtil.isNotEmpty(tableValue)) {
                JSONArray valueArray = JSON.parseArray(tableValue);
                for (Object obj : valueArray) {
                    JSONObject jsonObj = (JSONObject) obj;
                    Map<String, String> tableDataListMap = new HashMap<>();
                    for (String span : spanList) {
                        String fieldKey = ReUtil.getGroup1("<span.*?data-wk-table-value-tag=\"(.*?)\".*?</span>", span);
                        String[] moduleIdTableFieldId = fieldKey.split("\\.");
                        AbstractMap.SimpleEntry<String, Integer> fieldNameType = fieldId2FieldNameTypeMap.get(TypeUtils.castToLong(moduleIdTableFieldId[1]));
                        if (ObjectUtil.isNull(fieldNameType)) {
                            throw new BusinessException(ModuleCodeEnum.PRINT_TEMPLATE_NOT_LATEST);
                        }
                        String fieldName = fieldNameType.getKey();
                        Integer type = fieldNameType.getValue();
                        ModuleFieldEnum fieldEnum = ModuleFieldEnum.parse(type);
                        String fieldData = jsonObj.getString(fieldName);
                        String replaceValue;
                        if (fieldEnum == ModuleFieldEnum.DATA_UNION || fieldEnum == ModuleFieldEnum.DATA_UNION_MULTI) {
                            JSONObject jsonObject = JSONObject.parseObject(fieldData);
                            List<JSONObject> dataList = (List<JSONObject>) jsonObject.get("fieldData");
                            List<ModuleFieldData> moduleFieldData = dataList.stream().map(JSONAware::toJSONString).map(s -> JSON.parseObject(s, ModuleFieldData.class)).collect(Collectors.toList());
                            replaceValue = moduleFieldData.stream().map(ModuleFieldData::getValue).collect(Collectors.joining(Const.SEPARATOR));
                        } else {
                            replaceValue = parseValue2StringByType(fieldEnum, fieldData, true, null);
                        }
                        tableDataListMap.put(fieldKey, replaceValue);
                    }
                    String newTableMid = tableMid;
                    for (Map.Entry<String, String> entry : tableDataListMap.entrySet()) {
                        String k = entry.getKey();
                        String v = StrUtil.isNotEmpty(entry.getValue()) ? entry.getValue() : "";
                        newTableMid = newTableMid.replaceAll("(<span((?!/span>).)*data-wk-table-value-tag=\"" + k + "\".*?)(\\{.*?\\})(</span>)", "$1" + v + "$4");
                    }
                    tableContent.append(newTableMid);
                }
            }
            tableContent = new StringBuilder(tableHtml.replaceAll("<tr.*?data-wk-table-tr-tag=\"value\">[\\s\\S]*</tr>?", tableContent.toString()));
            content = content.replace(tableHtml, tableContent.toString());
        }
        return content;
    }

    private String replaceContent(Long dataId, String content, Long moduleId, Integer version) throws IllegalAccessException {
        List<String> spanList = ReUtil.findAllGroup0("<span((?!/span>).)*data-wk-tag=\".*?\".*?</span>", content);
        List<String> tableStyleList = ReUtil.findAllGroup0("<table[^(<|>)]*>", content);
        for (String tableStyle : tableStyleList) {
            String newStyle;
            if (tableStyle.contains("float: right") || tableStyle.contains("margin-left: auto; margin-right: auto;")) {
                newStyle = tableStyle.replace(">", " align=\"right\">");
            } else {
                newStyle = tableStyle.replace(">", " align=\"left\">");
            }
            content = content.replaceAll(tableStyle, newStyle);
        }
        List<String> tableList = ReUtil.findAllGroup0("<table.*?data-wk-table-tag=\"table\".*?table>", content);
        IModuleFieldDataService fieldDataService = ApplicationContextHolder.getBean(IModuleFieldDataService.class);
        IModuleFieldDataProvider fieldDataProvider = ApplicationContextHolder.getBean(IModuleFieldDataProvider.class);
        // <moduleId.fieldId, value> like <1509460649287970816.1509460649287970816, xxx>的数据结构
        Map<String, String> map = new HashMap<>(32);
        ModuleFieldDataResponseBO fieldData = fieldDataProvider.queryById(dataId, true);
        ModuleFieldDataCommonBO fieldDataCommon = fieldData.getFieldDataCommon();
        Class<? extends ModuleFieldDataCommonBO> fieldDataCommonClass = fieldDataCommon.getClass();
        Field[] commonFields = fieldDataCommonClass.getDeclaredFields();

        Map<Long, ModuleFieldData> dataMap = fieldData.getFieldDataList().stream().collect(Collectors.toMap(ModuleFieldData::getFieldId, Function.identity()));

        // region dataId对应的各个自定义字段的值
        // dataId对应的数据List
        List<ModuleFieldData> fieldDataList = fieldDataService
                .lambdaQuery()
                .eq(ModuleFieldData::getDataId, dataId)
                .list();
        // 组装成 k(fieldId) v(data)
        Map<Long, ModuleFieldData> fieldId2DataMap = fieldDataList.stream().collect(Collectors.toMap(ModuleFieldData::getFieldId, Function.identity()));
        // endregion

        // region 这个模板相关的模块与其字段
        // 查询关联模块与其字段
        List<ModuleFieldVO> modulesAndFields = this.getFields(moduleId, version);
        // 模块id，字段List
        Map<Long, ModuleFieldVO> moduleId2FieldVoMap = modulesAndFields.stream().collect(Collectors.toMap(ModuleFieldVO::getModuleId, Function.identity()));
        // 本模块字段信息
        ModuleFieldVO thisModule = moduleId2FieldVoMap.get(moduleId);
        Map<Long, ModuleFieldBO> fieldId2FieldMap = thisModule.getFieldList().stream().collect(Collectors.toMap(ModuleFieldBO::getFieldId, Function.identity()));
        // 本模块数据关联字段
        List<ModuleFieldBO> unionList = thisModule.getFieldList().stream()
                .filter(f -> Arrays.asList(ModuleFieldEnum.DATA_UNION.getType(), ModuleFieldEnum.DATA_UNION_MULTI.getType()).contains(f.getType()))
                .filter(f -> ObjectUtil.isNull(f.getGroupId()))
                .collect(Collectors.toList());
        // endregion

        // region 数据关联 关联到的数据
        List<String> unionFieldValues = new ArrayList<>();
        for (ModuleFieldBO unionField : unionList) {
            ModuleFieldData moduleFieldData = fieldId2DataMap.get(unionField.getFieldId());
            unionFieldValues.addAll(Arrays.asList(moduleFieldData.getValue().split(Const.SEPARATOR)));
        }
        // 单关联字段关联的值
        List<String> singleUnionFieldValues = unionList.stream()
                .filter(f -> ObjectUtil.equal(f.getType(), ModuleFieldEnum.DATA_UNION.getType()))
                .filter(f -> ObjectUtil.isNull(f.getGroupId()))
                .map(f -> fieldId2DataMap.get(f.getFieldId()).getValue())
                .collect(Collectors.toList());
        // endregion

        for (String span : spanList) {
            // key：  moduleId.fieldId, like '1509460649287970816.1509460649287970816'
            String key = ReUtil.getGroup1("<span.*?data-wk-tag=\"(.*?)\".*?</span>", span);
            String[] moduleIdFieldId = key.split("\\.");
            StringBuilder value = new StringBuilder("");
            // 本模块字段
            if (ObjectUtil.equal(TypeUtils.castToLong(moduleIdFieldId[0]), moduleId)) {
                ModuleFieldBO fieldBO = fieldId2FieldMap.get(TypeUtils.castToLong(moduleIdFieldId[1]));
                if (ObjectUtil.isNull(fieldBO)) {
                    throw new BusinessException(ModuleCodeEnum.PRINT_TEMPLATE_NOT_LATEST);
                }
                // common字段
                if (ObjectUtil.equal(0, fieldBO.getFieldType())) {
                    for (Field field : commonFields) {
                        field.setAccessible(true);
                        if (ObjectUtil.equal(fieldBO.getFieldName(), field.getName())) {
                            Object o = field.get(fieldDataCommon);
                            if (ObjectUtil.isNotNull(o)) {
                                if (o instanceof Date) {
                                    value = new StringBuilder(DateUtil.formatDateTime((Date) o));
                                } else {
                                    value = new StringBuilder(o.toString());
                                }
                                break;
                            }
                        }
                    }
                }
                // 自定义字段
                else if (ObjectUtil.equal(1, fieldBO.getFieldType())) {
                    value = this.getValueByFieldId(dataMap, fieldBO);
                }
            }
            // 其它模块字段
            else {
                ModuleFieldVO targetModule = moduleId2FieldVoMap.get(TypeUtils.castToLong(moduleIdFieldId[0]));
                ModuleFieldBO targetFieldBO = targetModule.getFieldList().stream().filter(f -> ObjectUtil.equal(TypeUtils.castToLong(moduleIdFieldId[1]), f.getFieldId())).findFirst().orElse(null);
                if (ObjectUtil.isNull(targetFieldBO)) {
                    throw new BusinessException(ModuleCodeEnum.PRINT_TEMPLATE_NOT_LATEST);
                }
                breakFlag:
                for (String valeOfDataId : unionFieldValues) {
                    if (StrUtil.isNotEmpty(valeOfDataId)) {
                        ModuleFieldDataResponseBO targetFieldData = fieldDataProvider.queryById(TypeUtils.castToLong(valeOfDataId), true);
                        // common字段
                        if (ObjectUtil.equal(0, targetFieldBO.getFieldType())) {
                            ModuleFieldDataCommonBO targetFieldDataCommon = targetFieldData.getFieldDataCommon();
                            for (Field field : commonFields) {
                                field.setAccessible(true);
                                if (ObjectUtil.equal(targetFieldBO.getFieldName(), field.getName())) {
                                    Object o = field.get(targetFieldDataCommon);
                                    if (ObjectUtil.isNotNull(o)) {
                                        if (o instanceof Date) {
                                            value = new StringBuilder(DateUtil.formatDateTime((Date) o));
                                        } else {
                                            value = new StringBuilder(o.toString());
                                        }
                                        break breakFlag;
                                    }
                                }
                            }
                        }
                        // 自定义字段
                        else if (ObjectUtil.equal(1, targetFieldBO.getFieldType())) {
                            for (ModuleFieldData moduleFieldData : targetFieldData.getFieldDataList()) {
                                if (ObjectUtil.equal(moduleFieldData.getFieldId(), TypeUtils.castToLong(moduleIdFieldId[1]))) {
                                    value = new StringBuilder(moduleFieldData.getValue());
                                    break breakFlag;
                                }
                            }
                        }
                    }
                }
            }
            map.put(key, value.toString());
        }
        // TODO 打印模板涉及到 表格
        content = appendTableContent(dataMap, content, tableList, moduleId2FieldVoMap, moduleId, singleUnionFieldValues, fieldDataService, fieldDataProvider);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String k = entry.getKey();
            String v = StrUtil.isNotEmpty(entry.getValue()) ? entry.getValue() : "";
            content = content.replaceAll("(<span((?!/span>).)*data-wk-tag=\"" + k + "\".*?)(\\{.*?\\})()", "$1" + v + "$4");
        }
        return content;
    }

    @Override
    public String print(Long templateId, Long dataId) {
        ModulePrintTemplate printTemplate = getById(templateId);
        if (StrUtil.isEmpty(printTemplate.getContent())) {
            throw new BusinessException(ModuleCodeEnum.MODULE_PRINT_TEMPLATE_CONTENT_EMPTY_ERROR);
        }
        String content = null;
        try {
            content = replaceContent(dataId, printTemplate.getContent(), printTemplate.getModuleId(), printTemplate.getVersion());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return content;
    }


    private void writePDFContent(String content, String distPath) {
        log.info("准备转换pdf");
        String toPdfTool;
        if (BaseUtil.isWindows()) {
            toPdfTool = ModuleConst.WIN_TO_PDF_TOOL;
        } else {
            toPdfTool = ModuleConst.LINUX_TO_PDF_TOOL;
        }
        StringBuilder cmd = new StringBuilder();
        log.info("设置插件地址 ： " + toPdfTool);
        cmd.append(toPdfTool);
        cmd.append(" ");
        cmd.append(" --log-level none ");
        cmd.append(content);
        cmd.append(" ");
        cmd.append(distPath);
        try {
            log.info("进行转换,html地址 ： " + content);
            log.info("进行转换,转换后pdf地址 ： " + distPath);
            Process process = Runtime.getRuntime().exec(cmd.toString());
            process.waitFor();
            log.info("转换成功 ");
            process.destroy();
        } catch (Exception e) {
            log.info("转换失败，失败原因" + e);
        }
    }

    @Override
    public String preview(String content, String type) {
        String fileTypeP = "pdf";
        String fileTypeW = "word";
        if (!Arrays.asList(fileTypeP, fileTypeW).contains(type)) {
            throw new BusinessException(ModuleCodeEnum.MODULE_PRINT_PRE_VIEW_ERROR);
        }
        String slash = BaseUtil.isWindows() ? "\\" : "/";
        String date = DateUtil.format(new Date(), "yyyyMMdd");
        String folderPath = FileUtil.getTmpDirPath() + slash + "print" + slash + date;
        String UUID = IdUtil.simpleUUID();
        String fileName = UUID + ".pdf";
        FileUtil.mkdir(folderPath + slash);
        String path = folderPath + slash + fileName;
        JSONObject object = new JSONObject();
        String html = "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "/**\n" +
                "* Copyright (c) Tiny Technologies, Inc. All rights reserved.\n" +
                "* Licensed under the LGPL or a commercial license.\n" +
                "* For LGPL see License.txt in the project root for license information.\n" +
                "* For commercial licenses see https://www.tiny.cloud/\n" +
                "*/\n" +
                "body {\n" +
                "  font-family:  simsun, serif,-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;\n" +
                "  line-height: 1.4;\n" +
                "  padding: 60px;\n" +
                "  width: 595px;\n" +
                "  margin: 0 auto;\n" +
                "  border-radius: 4px;\n" +
                "  background: white;\n" +
                "  min-height: 100%;\n" +
                "}\n" +
                "p { margin: 5px 0;\n" +
                "  line-height: 1.5;\n" +
                "}\n" +
                "table {\n" +
                "  border-collapse: collapse;\n" +
                "}\n" +
                "table th,\n" +
                "table td {\n" +
                "  border: 1px solid #ccc;\n" +
                "  padding: 0.4rem;\n" +
                "}\n" +
                "*{\n" +
                "     font-family:  simsun, serif,-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;\n" +
                "}\n" +
                "figure {\n" +
                "  display: table;\n" +
                "  margin: 1rem auto;\n" +
                "}\n" +
                "figure figcaption {\n" +
                "  color: #999;\n" +
                "  display: block;\n" +
                "  margin-top: 0.25rem;\n" +
                "  text-align: center;\n" +
                "}\n" +
                "hr {\n" +
                "  border-color: #ccc;\n" +
                "  border-style: solid;\n" +
                "  border-width: 1px 0 0 0;\n" +
                "}\n" +
                "code {\n" +
                "  background-color: #e8e8e8;\n" +
                "  border-radius: 3px;\n" +
                "  padding: 0.1rem 0.2rem;\n" +
                "}\n" +
                ".mce-content-body:not([dir=rtl]) blockquote {\n" +
                "  border-left: 2px solid #ccc;\n" +
                "  margin-left: 1.5rem;\n" +
                "  padding-left: 1rem;\n" +
                "}\n" +
                ".mce-content-body[dir=rtl] blockquote {\n" +
                "  border-right: 2px solid #ccc;\n" +
                "  margin-right: 1.5rem;\n" +
                "  padding-right: 1rem;\n" +
                "}\n" +
                "\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                content +
                "</body>\n" +
                "</html>";
        if (fileTypeP.equals(type)) {
            writePDFContent(html, path);
        } else if (fileTypeW.equals(type)) {
            String filePath = folderPath + slash + UUID + ".doc";
            File file = FileUtil.file(filePath);
            try (
                    ByteArrayInputStream byteArrayInputStream = IoUtil.toUtf8Stream(html);
                    FileOutputStream outputStream = new FileOutputStream(file);
                    POIFSFileSystem poifsFileSystem = new POIFSFileSystem();
            ) {
                DirectoryEntry directoryEntry = poifsFileSystem.getRoot();
                directoryEntry.createDocument("WordDocument", byteArrayInputStream);
                object.put("word", file.getAbsolutePath());
                poifsFileSystem.writeFilesystem(outputStream);
            } catch (Exception e) {
                log.error("打印预览转换word失败", e);
                throw new BusinessException(SystemCodeEnum.SYSTEM_ERROR);
            }
            writePDFContent(html, path);
        }
        String uuid = IdUtil.simpleUUID();
        object.put("pdf", path);
        redis.setex(ModuleConst.CRM_PRINT_TEMPLATE_CACHE_KEY + uuid, 3600 * 24, object.toJSONString());
        return uuid;
    }

    @Override
    public void savePrintRecord(ModulePrintRecord printRecord) {
        printRecord.setCreateTime(LocalDateTimeUtil.now()).setCreateUserId(UserUtil.getUserId());
        modulePrintRecordService.save(printRecord);
    }

    @Override
    public List<ModulePrintRecord> queryPrintRecord(Long moduleId) {
        List<ModulePrintTemplate> list = this.lambdaQuery().eq(ModulePrintTemplate::getModuleId, moduleId).list();
        List<Long> collect = list.stream().map(ModulePrintTemplate::getId).collect(Collectors.toList());
        List<ModulePrintRecord> records = modulePrintRecordService.lambdaQuery().in(ModulePrintRecord::getTemplateId, collect).list();
        records.forEach(record -> record.setCreateUserName(UserCacheUtil.getUserName(record.getCreateUserId())));
        return records;
    }

    @Override
    public ModulePrintRecord queryPrintRecordById(Long recordId) {
        return modulePrintRecordService.getById(recordId);
    }

    /**
     * 查到当前模块的字段，如果有关联字段，再查到关联字段的关联模块的字段
     *
     * @param moduleId 模块id
     * @param version  版本号
     * @return list
     */
    @Override
    public List<ModuleFieldVO> getFields(Long moduleId, Integer version) {
        List<ModuleFieldVO> voList = new LinkedList<>();
        IModuleService moduleService = ApplicationContextHolder.getBean(IModuleService.class);
        IModuleFieldService moduleFieldService = ApplicationContextHolder.getBean(IModuleFieldService.class);
        ModuleEntity currentModule = moduleService.getByModuleIdAndVersion(moduleId, version);
        // TODO:待修改
        FieldQueryBO queryBO = new FieldQueryBO();
        queryBO.setModuleId(moduleId);
        queryBO.setVersion(version);
        queryBO.setCategoryId(null);
        List<ModuleFieldBO> moduleFields = moduleFieldService.queryList(queryBO);
        ModuleFieldVO vo = new ModuleFieldVO();
        vo.setModuleId(currentModule.getModuleId());
        vo.setName(currentModule.getName());
        vo.setFieldList(moduleFields);
        voList.add(vo);
        // 查字段的 关联模块 配置
        Set<Long> targetModuleIds = ApplicationContextHolder
                .getBean(IModuleFieldUnionService.class)
                .lambdaQuery()
                .eq(ModuleFieldUnion::getModuleId, moduleId)
                .eq(ModuleFieldUnion::getVersion, version)
                .eq(ModuleFieldUnion::getType, 1)
                .ne(ModuleFieldUnion::getTargetModuleId, moduleId)
                .list()
                .stream()
                .map(ModuleFieldUnion::getTargetModuleId)
                .collect(Collectors.toSet());
        for (Long targetModuleId : targetModuleIds) {
            ModuleEntity normal = moduleService.getNormal(targetModuleId);
            if (ObjectUtil.isNotNull(normal)) {
                ModuleFieldVO unionVo = new ModuleFieldVO();
                //TODO:待修改
                queryBO.setModuleId(normal.getModuleId());
                queryBO.setVersion(normal.getVersion());
                queryBO.setCategoryId(null);
                List<ModuleFieldBO> moduleFieldBOS = moduleFieldService.queryList(queryBO);
                unionVo.setModuleId(normal.getModuleId());
                unionVo.setName(normal.getName());
                unionVo.setFieldList(moduleFieldBOS);
                voList.add(unionVo);
            }
        }
        return voList;
    }

    @Override
    public List<ModulePrintTemplate> getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery()
                .eq(ModulePrintTemplate::getModuleId, moduleId)
                .eq(ModulePrintTemplate::getVersion, version)
                .list();
    }
}
