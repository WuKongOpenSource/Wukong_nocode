package com.kakarote.module.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.constant.Const;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.result.SystemCodeEnum;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.common.easyexcel.CustomCellWriteHandler;
import com.kakarote.module.common.easyexcel.CustomSheetWriteHandler;
import com.kakarote.module.constant.FlowTypeEnum;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.CommonESNestedBO;
import com.kakarote.module.entity.BO.FieldQueryBO;
import com.kakarote.module.entity.BO.ModuleFieldBO;
import com.kakarote.module.entity.BO.ModuleOptionsBO;
import com.kakarote.module.entity.PO.Flow;
import com.kakarote.module.service.IModuleFieldDataService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wwl
 * @date 2022/4/13 15:02
 */
@Slf4j
public class EasyExcelParseUtil {

    private static final ThreadLocal<Closeable> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 统一导出数据模板
     *
     * @param isClose 是否关闭writer流 true为关闭
     */
    public static void exportExcel(List<Map<String, Object>> dataList, ExcelParseUtil.ExcelParseService excelParseService, List<ModuleFieldBO> selectFields, List<ModuleFieldBO> allFields, HttpServletResponse response, Integer isXls, boolean isClose) {
        try {
            if (!isClose) {
                // 连续获取数据
                List<Map<String, Object>> nextData = excelParseService.getNextData();
                if (ObjectUtil.isNotNull(nextData) && CollUtil.isNotEmpty(nextData)) {
                    dataList.addAll(nextData);
                    exportExcel(dataList, excelParseService, selectFields, allFields, response, isXls, false);
                } else {
                    isClose = true;
                }
            }
            if (ObjectUtil.equal(1, isXls)) {
                response.setContentType("application/vnd.ms-excel;charset=utf-8");
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(excelParseService.getExcelName() + "信息", "utf-8") + ".xls" + (excelParseService.isXlsx() ? "x" : ""));
                exportExcel(dataList, excelParseService, selectFields, allFields, response);
            } else {
                exportExcelCsv(dataList, excelParseService, selectFields, response);
            }
        } catch (Exception ex) {
            isClose = true;
            log.error("导出数据错误", ex);
        } finally {
            if (isClose) {
                IoUtil.close(THREAD_LOCAL.get());
                THREAD_LOCAL.remove();
            }
        }

    }

    private static List<List<String>> createHeadList(List<ModuleFieldBO> selectedList, List<ModuleFieldBO> allFields) {
        List<List<String>> heads = new ArrayList<>();
        for (ModuleFieldBO field : selectedList) {
            List<String> head = new ArrayList<>();
            if (ObjectUtil.equal(ModuleFieldEnum.DETAIL_TABLE.getType(), field.getType())) {
                List<ModuleFieldBO> fieldsInTable = allFields.stream()
                        .filter(f -> ObjectUtil.equal(f.getGroupId(), field.getGroupId()) && ObjectUtil.notEqual(f.getFieldId(), field.getFieldId()))
                        .collect(Collectors.toList());
                for (ModuleFieldBO tableField : fieldsInTable) {
                    List<String> tableHeads = new ArrayList<>();
                    tableHeads.add(field.getName());
                    tableHeads.add(tableField.getName());
                    heads.add(tableHeads);
                }
            } else {
                head.add(field.getName());
                heads.add(head);
            }
        }
        return heads;
    }

    private static String getValue(ModuleFieldBO field, Map<String, Object> objMap, Flow flow, Object o) {
        String resultStr = "";
        if (ObjectUtil.isNotNull(o)) {
            // 当前节点
            if (ObjectUtil.equal("currentFlowId", field.getFieldName())) {
                if (ObjectUtil.isNotNull(flow)) {
                    resultStr = flow.getFlowName();
                }
            }
            // 节点状态
            else if (ObjectUtil.equal("flowStatus", field.getFieldName())) {
                if (ObjectUtil.isNotNull(flow)) {
                    // 审批节点
                    if (ObjectUtil.equal(FlowTypeEnum.EXAMINE.getType(), flow.getFlowType())) {
                        Integer status = MapUtil.getInt(objMap, "status");
                        resultStr = getNameByStatus(status);
                    }
                    // 其它节点
                    else {
                        Integer flowStatus = MapUtil.getInt(objMap, "flowStatus");
                        resultStr = getNameByStatus(flowStatus);
                    }
                }
            }
            else {
                resultStr = o.toString();
            }
        }
        return resultStr;
    }

    private static String getNameByStatus(Integer status) {
        switch (status) {
            case 0:
                return "待处理";
            case 1:
                return "已处理";
            case 2:
                return "审批拒绝";
            case 3:
                return "处理中";
            default:
                return "";
        }
    }

    public static String attentionStr2Value(String str) {
        switch (str) {
            case "一星":
                return "1";
            case "二星":
                return "2";
            case "三星":
                return "3";
            case "四星":
                return "4";
            case "五星":
                return "5";
            default:
                return "";
        }
    }
    public static String attentionValue2Str(String value) {
        switch (value) {
            case "1":
                return "一星";
            case "2":
                return "二星";
            case "3":
                return "三星";
            case "4":
                return "四星";
            case "5":
                return "五星";
            default:
                return "";
        }
    }

    public static String parseValueByType(ModuleFieldEnum fieldEnum, String value) {
        if (ObjectUtil.isEmpty(value)) {
            return "";
        }
        switch (fieldEnum) {
            case DATA_UNION:
            case DATA_UNION_MULTI:
                String multiMainValue = ApplicationContextHolder.getBean(IModuleFieldDataService.class).queryMultipleMainFieldValue(value);
                return multiMainValue;
            case USER:
                List<Long> ids = Convert.toList(Long.class, value);
                value = ids.stream().map(UserCacheUtil::getUserName).collect(Collectors.joining(Const.SEPARATOR));
                return value;
            case SELECT:
                ModuleOptionsBO option = JSON.parseObject(value, ModuleOptionsBO.class);
                return option.getValue();
            // case TAG:
            // 	List<JSONObject> tagList = JSON.parseArray(value, JSONObject.class);
            // 	return tagList.stream().map(o -> o.getString("name")).collect(Collectors.joining(Const.SEPARATOR));
            case TAG:
            case CHECKBOX:
                List<ModuleOptionsBO> objs = JSON.parseArray(value, ModuleOptionsBO.class);
                return objs.stream().map(ModuleOptionsBO::getValue).collect(Collectors.joining(Const.SEPARATOR));
            case PERCENT:
                BigDecimal multiply = new BigDecimal(value).multiply(new BigDecimal("100"));
                return multiply + "%";
            case DATETIME:
                return DateUtil.formatDateTime(DateUtil.parse(value));
            case DATE_INTERVAL:
                CommonESNestedBO dateFromTo = JSON.parseObject(value, CommonESNestedBO.class);
                return dateFromTo.getFromDate() + "至" + dateFromTo.getToDate();
            case AREA_POSITION:
                List<CommonESNestedBO> address = JSON.parseArray(value, CommonESNestedBO.class);
                return address.stream().map(CommonESNestedBO::getName).collect(Collectors.joining("-"));
            case CURRENT_POSITION:
                JSONObject json = JSON.parseObject(value);
                return json.getString("address");
            case STRUCTURE:
                List<Long> deptIds = Convert.toList(Long.class, value);
                value = deptIds.stream().map(UserCacheUtil::getDeptName).collect(Collectors.joining(Const.SEPARATOR));
                return value;
            case ATTENTION:
                return attentionValue2Str(value);
            default:
                return value;
        }
    }

    private static Flow getFlow(Map<String, Object> objMap) {
        Long currentFlowId = MapUtil.getLong(objMap, "currentFlowId");
        Flow flow = null;
        if (ObjectUtil.isNotNull(currentFlowId)) {
            Integer version = MapUtil.getInt(objMap, "version");
            flow = FlowCacheUtil.getByIdAndVersion(currentFlowId, version);
        }
        return flow;
    }

    private static List<List<String>> createDataList(List<ModuleFieldBO> selectedList, List<Map<String, Object>> dataList, List<ModuleFieldBO> allFields) {
        List<List<String>> results = new ArrayList<>();
        // 主字段
        ModuleFieldBO mainField = selectedList.get(0);
        List<Map<String, Object>> newDataList = new ArrayList<>();
        for (Map<String, Object> map : dataList) {
            for (ModuleFieldBO field : selectedList) {
                Object o = map.get(field.getFieldName());
                if (ObjectUtil.equal(ModuleFieldEnum.DETAIL_TABLE.getType(), field.getType())) {
                    if (ObjectUtil.isNotNull(o) && StrUtil.isNotEmpty(o.toString())) {
                        JSONArray tableValues = (JSONArray) o;
                        for (int i = 0; i < tableValues.size(); i++) {
                            JSONObject jsonObject = (JSONObject) tableValues.get(i);
                            if (ObjectUtil.equal(0, i)) {
                                map.putAll(jsonObject);
                                newDataList.add(map);
                            } else {
                                Map<String, Object> objectHashMap = new HashMap<>(jsonObject);
                                // 放主字段
                                objectHashMap.put(mainField.getFieldName(), map.get(mainField.getFieldName()));
                                newDataList.add(objectHashMap);
                            }
                        }
                    }
                }
            }
        }
        // 有表格
        if (CollUtil.isNotEmpty(newDataList)) {
            for (Map<String, Object> objMap : newDataList) {
                Flow flow = getFlow(objMap);
                List<String> res = new ArrayList<>();
                for (ModuleFieldBO field : selectedList) {
                    Object o = objMap.get(field.getFieldName());
                    // 表格内字段
                    if (ObjectUtil.equal(ModuleFieldEnum.DETAIL_TABLE.getType(), field.getType())) {
                        List<ModuleFieldBO> fieldsInTable = allFields.stream()
                                .filter(f -> ObjectUtil.equal(f.getGroupId(), field.getGroupId()) && ObjectUtil.notEqual(f.getFieldId(), field.getFieldId()))
                                .collect(Collectors.toList());
                        for (ModuleFieldBO tableField : fieldsInTable) {
                            Object str = objMap.get(tableField.getFieldName());
                            ModuleFieldEnum parse = ModuleFieldEnum.parse(tableField.getType());
                            if (ObjectUtil.isNotNull(str)) {
                                String value = parseValueByType(parse, str.toString());
                                res.add(value);
                            }
                            else {
                                res.add("");
                            }
                        }
                    }
                    // 表格外字段
                    else {
                        res.add(getValue(field, objMap, flow, o));
                    }
                }
                results.add(res);
            }
        }
        // 无表格
        else {
            for (Map<String, Object> objMap : dataList) {
                Flow flow = getFlow(objMap);
                List<String> res = new ArrayList<>();
                for (ModuleFieldBO field : selectedList) {
                    Object o = objMap.get(field.getFieldName());
                    res.add(getValue(field, objMap, flow, o));
                }
                results.add(res);
            }
        }
        return results;
    }

    /**
     * 统一导出数据模板
     */
    public static void exportExcel(List<Map<String, Object>> dataList, ExcelParseUtil.ExcelParseService excelParseService, List<ModuleFieldBO> selectFields, List<ModuleFieldBO> allFields, HttpServletResponse response) {
        ModuleFieldBO bo = selectFields.stream().filter(f -> ObjectUtil.equal(f.getType(), ModuleFieldEnum.DETAIL_TABLE.getType())).findFirst().orElse(null);
        try  (CustomCellWriteHandler cellWriteHandler = new CustomCellWriteHandler(true, ObjectUtil.isNotNull(bo))){
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream())
                    .useDefaultStyle(false)
                    .registerWriteHandler(cellWriteHandler)
                    .registerWriteHandler(new AbstractColumnWidthStyleStrategy() {
                        @Override
                        protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> list, Cell cell, Head head, Integer integer, Boolean aBoolean) {
                            Sheet sheet = writeSheetHolder.getSheet();
                            int columnIndex = cell.getColumnIndex();
                            // 列宽40
                            sheet.setColumnWidth(columnIndex, 4000);
                        }
                    })
                    .build();
            WriteSheet sheet = EasyExcel.writerSheet(0, "sheet1")
                    .needHead(Boolean.FALSE)
                    .head(createHeadList(selectFields, allFields))
                    .build();
            WriteTable writeTable0 = EasyExcel.writerTable(0).needHead(Boolean.TRUE).build();
            // 往excel文件中写入数据
            List<List<String>> dataLists = createDataList(selectFields, dataList, allFields);
            excelWriter.write(dataLists, sheet, writeTable0);
            excelWriter.finish();
        } catch (Exception e) {
            log.error("导出错误：", e);
        }
    }

    /**
     * 导入错误信息文件专用
     * @param dataList d
     * @param selectFields s
     * @param allFields a
     */
    public static void exportExcel(OutputStream stream, List<List<String>> dataList, List<ModuleFieldBO> selectFields, List<ModuleFieldBO> allFields) {
        ModuleFieldBO bo = selectFields.stream().filter(f -> ObjectUtil.equal(f.getType(), ModuleFieldEnum.DETAIL_TABLE.getType())).findFirst().orElse(null);
        try  (CustomCellWriteHandler cellWriteHandler = new CustomCellWriteHandler(true, ObjectUtil.isNotNull(bo))){
            ExcelWriter excelWriter = EasyExcel.write(stream)
                    .useDefaultStyle(false)
                    .registerWriteHandler(cellWriteHandler)
                    .registerWriteHandler(new AbstractColumnWidthStyleStrategy() {
                        @Override
                        protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> list, Cell cell, Head head, Integer integer, Boolean aBoolean) {
                            Sheet sheet = writeSheetHolder.getSheet();
                            int columnIndex = cell.getColumnIndex();
                            if (ObjectUtil.equal(1, columnIndex)) {
                                sheet.setColumnWidth(columnIndex, 7000);
                            } else {
                                sheet.setColumnWidth(columnIndex, 4000);
                            }

                            // 行高
                            sheet.setDefaultRowHeight((short) 500);
                        }
                    })
                    .build();
            List<List<String>> headList = new ArrayList<>();
            List<String> firstHeadCell = new ArrayList<>();
            firstHeadCell.add(getMergeContent());
            firstHeadCell.add("错误原因");
            headList.add(firstHeadCell);
            // 去掉不需要的字段
            selectFields.removeIf(f -> removeFieldByType(f.getType()));
            allFields.removeIf(f -> removeFieldByType(f.getType()));
            headList.addAll(createImportExcelHeadList(selectFields, allFields));
            WriteSheet sheet = EasyExcel.writerSheet(0, "sheet1")
                    .needHead(Boolean.FALSE)
                    .head(headList)
                    .build();
            WriteTable writeTable0 = EasyExcel.writerTable(0).needHead(Boolean.TRUE).build();
            excelWriter.write(dataList, sheet, writeTable0);
            excelWriter.finish();
        } catch (Exception e) {
            log.error("导入错误文件：", e);
        }
    }



    /**
     * 统一导出数据模板
     */
    public static void exportExcelCsv(List<? extends Map<String, Object>> dataList, ExcelParseUtil.ExcelParseService excelParseService, List<?> list, HttpServletResponse response) {
        try {
            response.setContentType("application/csv;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(excelParseService.getExcelName() + "信息" + ".csv", "UTF-8"));
            response.setCharacterEncoding("UTF-8");
            CsvWriter writer;
            if (THREAD_LOCAL.get() == null) {
                writer = CsvUtil.getWriter(response.getWriter());
                response.getWriter().write(new String(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}));
                THREAD_LOCAL.set(writer);
            } else {
                writer = (CsvWriter) THREAD_LOCAL.get();
            }
            List<ExcelParseUtil.ExcelDataEntity> headList = excelParseService.parseData(list, false);
            Map<String, Integer> headMap = new HashMap<>(headList.size(), 1.0f);
            List<Object> names = new ArrayList<>();
            List<String> fieldNames = new LinkedList<>();
            headList.forEach(head -> {
                names.add(head.getName());
                fieldNames.add(head.getFieldName());
                // if (!Arrays.asList(FieldEnum.AREA.getType(), FieldEnum.AREA_POSITION.getType(), FieldEnum.CURRENT_POSITION.getType(), FieldEnum.DETAIL_TABLE.getType()).contains(head.getType())) {
                //     headMap.put(head.getFieldName(), head.getType());
                // }
            });
            List<List<Object>> writerList = new ArrayList<>();
            writerList.add(names);
            // 设置数据
            dataList.forEach(record -> excelParseService.getFunc().call(record, headMap));
            for (Map<String, Object> data : dataList) {
                List<Object> values = new ArrayList<>();
                for (String fieldName : fieldNames) {
                    values.add(data.get(fieldName));
                }
                writerList.add(values);
            }
            writer.write(writerList);
        } catch (IOException e) {
            log.error("导出csv文件错误", e);
            throw new BusinessException(SystemCodeEnum.SYSTEM_ERROR);
        }

    }

    private static String getMergeContent() {
        return "注意事项：\n" +
                "1、表头标“*”的红色字体为必填项\n" +
                "2、日期时间：推荐格式为2022-02-02 13:13:13\n" +
                "3、日期：推荐格式为2022-02-02\n" +
                "4、百分数：请输入整数，如百分百即输入100\n" +
                "5、手机号：支持6-15位数字（包含国外手机号格式）\n" +
                "6、邮箱：只支持邮箱格式\n" +
                "7、多行文本：字数限制为800字\n" +
                "8、主字段不能为空\n" +
                "9、自定义编号字段的值将被忽略\n" +
                "10、若自定义编号字段为主字段，且导入有表格字段，请在主字段处填写值以区分不同表格的数据所属。"
                ;
    }
    private static List<List<String>> createImportExcelHeadList(List<ModuleFieldBO> needFields, List<ModuleFieldBO> allFields) {
        List<List<String>> heads = new ArrayList<>();
        String noticeStr = getMergeContent();
        for (ModuleFieldBO field : needFields) {
            List<String> head = new ArrayList<>();
            if (ObjectUtil.equal(ModuleFieldEnum.DETAIL_TABLE.getType(), field.getType())) {
                List<ModuleFieldBO> fieldsInTable = allFields.stream().filter(f -> ObjectUtil.equal(f.getGroupId(), field.getGroupId()) && ObjectUtil.notEqual(f.getFieldId(), field.getFieldId())).collect(Collectors.toList());
                for (ModuleFieldBO tableField : fieldsInTable) {
                    List<String> tableHeads = new ArrayList<>();
                    tableHeads.add(noticeStr);
                    tableHeads.add(field.getName());
                    tableHeads.add(tableField.getName());
                    heads.add(tableHeads);
                }
            } else {
                String headStr = field.getName();
                // 不能为空字段
                if (ObjectUtil.equal(1, field.getIsNull())) {
                    headStr = "*"  + headStr;
                }
                head.add(noticeStr);
                head.add(headStr);
                heads.add(head);
            }
        }
        return heads;
    }
    public static void moduleImportExcel(String moduleName, List<ModuleFieldBO> customFields, List<ModuleFieldBO> allFields, FieldQueryBO queryBO, HttpServletResponse response) {
        ModuleFieldBO bo = customFields.stream().filter(f -> ObjectUtil.equal(f.getType(), ModuleFieldEnum.DETAIL_TABLE.getType())).findFirst().orElse(null);
        try (CustomCellWriteHandler cellWriteHandler = new CustomCellWriteHandler(false, ObjectUtil.isNotNull(bo))){
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode(moduleName + "导入模板", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

            // 去掉不需要的字段
            customFields.removeIf(f -> removeFieldByType(f.getType()));
            allFields.removeIf(f -> removeFieldByType(f.getType()));
            CustomSheetWriteHandler sheetWriteHandler = new CustomSheetWriteHandler(customFields, allFields);
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream())
                    .useDefaultStyle(false)
                    .registerWriteHandler(sheetWriteHandler)
                    .registerWriteHandler(cellWriteHandler)
                    .registerWriteHandler(new AbstractColumnWidthStyleStrategy() {
                          @Override
                          protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> list, Cell cell, Head head, Integer integer, Boolean aBoolean) {
                              Sheet sheet = writeSheetHolder.getSheet();
                              int columnIndex = cell.getColumnIndex();
                              // 列宽40
                              sheet.setColumnWidth(columnIndex, 4000);
                              // 行高7
                              sheet.setDefaultRowHeight((short) 500);
                          }
                    })
                    .build();
            WriteSheet sheet = EasyExcel.writerSheet(0, "sheet1")
                    .needHead(Boolean.FALSE)
                    .head(createImportExcelHeadList(customFields, allFields))
                    .build();
            WriteTable writeTable0 = EasyExcel.writerTable(0).needHead(Boolean.TRUE).build();
            List<List<String>> dataLists = new ArrayList<>();
            excelWriter.write(dataLists, sheet, writeTable0);
            excelWriter.finish();
        } catch (Exception e) {
            log.error("下载" + moduleName + "导入模板错误", e);
        }
    }

    @Data
    public static class ExcelDataEntity {

        /* 字段名称 */
        private String fieldName;

        /* 展示名称 */
        private String name;

        /* 字段类型 */
        private Integer type;

        /* 是否必填 1 是 0 否 */
        private Integer isNull;

        /* 设置列表 */
        private List<Object> setting;

        public ExcelDataEntity() {
        }

        public ExcelDataEntity(String fieldName, String name, Integer type) {
            this.fieldName = fieldName;
            this.name = name;
            this.type = type;
        }
    }

    public static abstract class ExcelParseService {

        /**
         * 设置自定义数据处理方法
         *
         * @return func
         */
        public ExcelParseUtil.DataFunc getFunc() {
            return (record, headMap) -> {
            };
        }

        /**
         * 统一处理数据
         *
         * @param list        请求头数据
         * @param importExcel 是否是导入模板
         * @return 转化后的请求头数据
         */
        public List<ExcelParseUtil.ExcelDataEntity> parseData(List<?> list, boolean importExcel) {
            List<ExcelParseUtil.ExcelDataEntity> entities = list.stream().map(obj -> {
                if (obj instanceof ExcelParseUtil.ExcelDataEntity) {
                    return (ExcelParseUtil.ExcelDataEntity) obj;
                }
                return BeanUtil.copyProperties(obj, ExcelParseUtil.ExcelDataEntity.class);
            }).collect(Collectors.toList());
            if (importExcel) {
                entities.removeIf(head -> removeFieldByType(head.getType()));
            } else {
                entities.removeIf(head -> ModuleFieldEnum.HANDWRITING_SIGN.getType().equals(head.getType()));
            }
            return entities;
        }

        /**
         * 如果需要执行全部数据导出，分批次的获取数据
         *
         * @return data
         */
        public List<Map<String, Object>> getNextData() {
            return null;
        }

        /**
         * 获取excel表格名称
         *
         * @return 表格名称
         */
        public abstract String getExcelName();

        /**
         * 导入的时候需要的可能需要新增字段场景
         *
         * @param writer    writer
         * @param x         – X坐标，从0计数，即列号
         * @param y         – Y坐标，从0计数，即行号
         * @param fieldName 字段名称
         * @return 新增的行数
         */
        public int addCell(ExcelWriter writer, Integer x, Integer y, String fieldName) {
            return 0;
        }

        /**
         * 是否是xlsx格式，xlsx导出会比xlx3倍左右，谨慎使用
         *
         * @return isXlsx
         */
        public boolean isXlsx() {
            return false;
        }

        public String getMergeContent(String module) {
            if ("user".equals(module)) {
                return "注意事项：\n" +
                        "1、表头标“*”的红色字体为必填项\n" +
                        "2、手机号：目前只支持中国大陆的11位手机号码；且手机号不允许重复\n" +
                        "3、登录密码：密码由6-20位字母、数字组成\n" +
                        "4、部门：上下级部门间用\"/\"隔开，且从最上级部门开始，例如“上海分公司/市场部/市场一部”。如出现相同的部门，则默认导入组织架构中顺序靠前的部门\n";
            } else if ("finance".equals(module)) {
                return "注意事项：\n" +
                        "1、表头标“*”的红色字体为必填项\n" +
                        "2、凭证字要与系统保持一致\n" +
                        "3、同一天同一个凭证的凭证号要保持一致\n" +
                        "4、科目编码要与系统保持一致\n" +
                        "5、日期：推荐格式为2020-02-02";
            } else if ("subject".equals(module)) {
                return "注意事项：\n" +
                        "1、表头标“*”的红色字体为必填项\n" +
                        "2、若导入的为一级科目，则上级科目编号填‘0’";
            } else if ("achievement".equals(module)) {
                return "注意事项：\n" +
                        "1、表头标“*”的红色字体为必填项\n" +
                        "2、业绩目标只能填写数字\n" +
                        "3、年份只填数字 例：2021";
            } else {
                return "注意事项：\n" +
                        "1、表头标“*”的红色字体为必填项\n" +
                        "2、日期时间：推荐格式为2020-02-02 13:13:13\n" +
                        "3、日期：推荐格式为2020-02-02\n" +
                        "4、手机号：支持6-15位数字（包含国外手机号格式）\n" +
                        "5、邮箱：只支持邮箱格式\n" +
                        "6、多行文本：字数限制为800字";
            }
        }


    }

    /**
     * 数据格式化方法
     */
    @FunctionalInterface
    public interface DataFunc {
        /**
         * 数据格式化方法
         *
         * @param record 记录
         * @param headMap 标题
         */
        void call(Map<String, Object> record, Map<String, Integer> headMap);
    }

    /**
     * 不支持导入的字段
     */
    private static final List<Integer> TYPE_LIST = Arrays.asList(
            // FieldEnum.AREA.getType()
            ModuleFieldEnum.AREA_POSITION.getType()
            , ModuleFieldEnum.ATTENTION.getType()
            , ModuleFieldEnum.BOOLEAN_VALUE.getType()
            // , FieldEnum.CALCULATION_FUNCTION.getType()
            , ModuleFieldEnum.CHECKBOX.getType()
            , ModuleFieldEnum.CURRENT_POSITION.getType()
            , ModuleFieldEnum.DESC_TEXT.getType()
            // , FieldEnum.DETAIL_TABLE.getType()
            , ModuleFieldEnum.DATE_INTERVAL.getType()
            , ModuleFieldEnum.FIELD_GROUP.getType()
            , ModuleFieldEnum.FILE.getType()
            , ModuleFieldEnum.HANDWRITING_SIGN.getType()
            , ModuleFieldEnum.STRUCTURE.getType()
            // 有的模块主子段是 自定义编号 所以放开，在导入数据时唯一、非空判断排除不加这个字段
            // 有值用来区分表格导入数据，创建save对象时给这个值置空
            // , FieldEnum.SERIAL_NUMBER.getType()
            , ModuleFieldEnum.TAG.getType()
            , ModuleFieldEnum.USER.getType()
    );


    /**
     * 删除不支持导入的字段
     *
     * @return true为不支持导入
     */
    public static boolean removeFieldByType(Integer type) {
        return TYPE_LIST.contains(type);
    }

}
