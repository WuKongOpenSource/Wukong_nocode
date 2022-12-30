package com.kakarote.module.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.kakarote.common.result.SystemCodeEnum;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.FieldQueryBO;
import com.kakarote.module.entity.BO.ModuleFieldBO;
import com.kakarote.module.entity.BO.ModuleOptionsBO;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldData;
import com.kakarote.module.service.IModuleFieldDataService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wwl
 * @date 2022/4/8 15:00
 */
@Slf4j
public class ExcelParseUtil {

    private static final ThreadLocal<Closeable> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 统一导出数据模板
     */
    public static void exportExcel(List<Map<String, Object>> dataList, ExcelParseService excelParseService, List<ModuleField> list, HttpServletResponse response, Integer isXls) {
        exportExcel(dataList, excelParseService, list, response, isXls, true);
    }

    /**
     * 统一导出数据模板
     *
     * @param isClose 是否关闭writer流 true为关闭
     */
    public static void exportExcel(List<Map<String, Object>> dataList, ExcelParseService excelParseService, List<ModuleField> selectedFieldList, HttpServletResponse response, Integer isXls, boolean isClose) {
        try {
            if (!isClose) {
                List<Map<String, Object>> nextData = excelParseService.getNextData();
                if (nextData != null && !nextData.isEmpty()) {
                    dataList.addAll(nextData);
                    exportExcel(dataList, excelParseService, selectedFieldList, response, isXls, false);
                } else {
                    isClose = true;
                }
            }
            if (ObjectUtil.equal(1, isXls)) {
                exportExcel(dataList, excelParseService, selectedFieldList, response);
            } else {
                exportExcelCsv(dataList, excelParseService, selectedFieldList, response);
            }

            if (ObjectUtil.equal(1, isXls) && isClose) {
                //自定义标题别名
                //response为HttpServletResponse对象
                response.setContentType("application/vnd.ms-excel;charset=utf-8");
                response.setCharacterEncoding("UTF-8");
                //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(excelParseService.getExcelName() + "信息", "utf-8") + ".xls" + (excelParseService.isXlsx() ? "x" : ""));
                ServletOutputStream out = response.getOutputStream();
                ((ExcelWriter) THREAD_LOCAL.get()).flush(out);
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


    /**
     * 统一导出数据模板
     */
    public static void exportExcel(List<Map<String, Object>> dataList, ExcelParseService excelParseService, List<ModuleField> list, HttpServletResponse response) {
        try {
            ExcelWriter writer;
            if (THREAD_LOCAL.get() == null) {
                writer = excelParseService.isXlsx() ? ExcelUtil.getBigWriter(1000) : ExcelUtil.getWriter();
                THREAD_LOCAL.set(writer);
            } else {
                writer = (ExcelWriter) THREAD_LOCAL.get();
            }
            List<ExcelDataEntity> headList = excelParseService.parseData(list, false);
            Map<String, Integer> headMap = new HashMap<>(headList.size(), 1.0f);
            headList.forEach(head -> {
                writer.addHeaderAlias(head.getFieldName(), head.getName());
                if (!Objects.equals(ModuleFieldEnum.DETAIL_TABLE.getType(), head.getType())) {
                    headMap.put(head.getFieldName(), head.getType());
                }
            });
            // 取消数据的黑色边框以及数据左对齐
            CellStyle cellStyle = writer.getCellStyle();
            cellStyle.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
            cellStyle.setBorderTop(BorderStyle.NONE);
            cellStyle.setBorderBottom(BorderStyle.NONE);
            cellStyle.setBorderLeft(BorderStyle.NONE);
            cellStyle.setBorderRight(BorderStyle.NONE);
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            Font defaultFont = writer.createFont();
            defaultFont.setFontHeightInPoints((short) 11);
            cellStyle.setFont(defaultFont);
            // 取消数字格式的数据的黑色边框以及数据左对齐
            CellStyle cellStyleForNumber = writer.getStyleSet().getCellStyleForNumber();
            cellStyleForNumber.setBorderTop(BorderStyle.NONE);
            cellStyleForNumber.setBorderBottom(BorderStyle.NONE);
            cellStyleForNumber.setBorderLeft(BorderStyle.NONE);
            cellStyleForNumber.setBorderRight(BorderStyle.NONE);
            cellStyleForNumber.setAlignment(HorizontalAlignment.LEFT);
            cellStyleForNumber.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
            cellStyleForNumber.setFont(defaultFont);
            // 设置数据
            dataList.forEach(record -> excelParseService.getFunc().call(record, headMap));
            //设置行高以及列宽
            writer.setRowHeight(-1, 20);
            writer.setColumnWidth(-1, 20);
            //只保留别名中的字段
            writer.setOnlyAlias(true);
            if (dataList.size() == 0) {
                Map<String, Object> record = new HashMap<>();
                headList.forEach(head -> record.put(head.getFieldName(), ""));
                writer.write(Collections.singletonList(record), true);
            } else {
                writer.write(dataList, true);
            }
            CellStyle style = writer.getHeadCellStyle();
            style.setAlignment(HorizontalAlignment.LEFT);
            Font font = writer.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 11);
            style.setFont(font);
        } catch (Exception e) {
            log.error("导出客户错误：", e);
        }
    }


    /**
     * 统一导出数据模板
     */
    public static void exportExcelCsv(List<? extends Map<String, Object>> dataList, ExcelParseService excelParseService, List<?> list, HttpServletResponse response) {
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
            List<ExcelDataEntity> headList = excelParseService.parseData(list, false);
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

    public static AbstractMap.SimpleEntry<Boolean,  List<List<ModuleFieldBO>>>  parseHeadData(List<ModuleFieldBO> needFields, List<ModuleFieldBO> allFields) {
        needFields.removeIf(head -> removeFieldByType(head.getType()));
        boolean detailTableFlag = false;
        List<List<ModuleFieldBO>> heads = new ArrayList<>();
        for (ModuleFieldBO field : needFields) {
            List<ModuleFieldBO> head = new ArrayList<>();
            if (ObjectUtil.equal(ModuleFieldEnum.DETAIL_TABLE.getType(), field.getType())) {
                detailTableFlag = true;
                List<ModuleFieldBO> fieldsInTable = allFields.stream().filter(f -> ObjectUtil.equal(f.getGroupId(), field.getGroupId()) && ObjectUtil.notEqual(f.getFieldId(), field.getFieldId())).collect(Collectors.toList());
                for (ModuleFieldBO tableField : fieldsInTable) {
                    List<ModuleFieldBO> tableHeads = new ArrayList<>();
                    tableHeads.add(field);
                    tableHeads.add(tableField);
                    heads.add(tableHeads);
                }
            } else {
                head.add(field);
                heads.add(head);
            }
        }
        return new AbstractMap.SimpleEntry<>(detailTableFlag, heads);
    }

    public static void moduleImportExcel2(ExcelParseService excelParseService, List<ModuleFieldBO> list , List<ModuleFieldBO> allFields, HttpServletResponse response, String module, FieldQueryBO queryBO) {
        // 过滤一些不允许导入的字段
        List<ModuleFieldBO> dataEntities = excelParseService.parseModuleData(list, true);

        AbstractMap.SimpleEntry<Boolean, List<List<ModuleFieldBO>>> headList = parseHeadData(list, allFields);

        try (ExcelWriter writer = ExcelUtil.getWriter(excelParseService.isXlsx())) {
            // 因为重复合并单元格会导致样式丢失，所以先获取全部字段一次合并
            int sum = dataEntities.stream().mapToInt(data -> excelParseService.addCell(null, 0, 0, data.getFieldName())).sum();
            writer.renameSheet(excelParseService.getExcelName() + "导入模板");
            writer.merge(dataEntities.size() - 1 + sum, excelParseService.getMergeContent(module), true);
            writer.getHeadCellStyle().setAlignment(HorizontalAlignment.LEFT);
            writer.getHeadCellStyle().setWrapText(true);
            Font headFont = writer.createFont();
            headFont.setFontHeightInPoints((short) 11);
            writer.getHeadCellStyle().setFont(headFont);
            writer.getHeadCellStyle().setFillPattern(FillPatternType.NO_FILL);
            if (!"subject".equals(module)) {
                writer.getOrCreateRow(0).setHeightInPoints(120);
            } else {
                writer.getOrCreateRow(0).setHeightInPoints(60);
            }
            writer.setRowHeight(-1, 20);

            Boolean tableFlag = headList.getKey();
            List<List<ModuleFieldBO>> headListValue = headList.getValue();
            for (int i = 0, z = 0, k = headListValue.size(); i < k; i++) {
                List<ModuleFieldBO> moduleFieldBOS = headListValue.get(i);
                CellStyle columnStyle = writer.getOrCreateColumnStyle(z);
                //设置统一字体
                columnStyle.setFont(headFont);
                DataFormat dateFormat = writer.getWorkbook().createDataFormat();
                if (Objects.equals(dataEntities.get(i).getType(), ModuleFieldEnum.DATE.getType())) {
                    columnStyle.setDataFormat(dateFormat.getFormat(DatePattern.NORM_DATE_PATTERN));
                } else if (Objects.equals(dataEntities.get(i).getType(), ModuleFieldEnum.DATETIME.getType())) {
                    columnStyle.setDataFormat(dateFormat.getFormat(DatePattern.NORM_DATETIME_PATTERN));
                } else {
                    columnStyle.setDataFormat(dateFormat.getFormat("@"));
                }
            }


            //设置样式
            for (int i = 0, k = dataEntities.size(), z = 0; i < k; i++, z++) {
                ModuleFieldBO dataEntity = dataEntities.get(i);
                //会新增cell或者对当前cell做调整，直接跳过默认处理
                int n = excelParseService.addCell(writer, z, 1, dataEntity.getFieldName());
                if (n > 0) {
                    z += n;
                    continue;
                }
                CellStyle columnStyle = writer.getOrCreateColumnStyle(z);
                //设置统一字体
                columnStyle.setFont(headFont);
                DataFormat dateFormat = writer.getWorkbook().createDataFormat();
                if (Objects.equals(dataEntities.get(i).getType(), ModuleFieldEnum.DATE.getType())) {
                    columnStyle.setDataFormat(dateFormat.getFormat(DatePattern.NORM_DATE_PATTERN));
                } else if (Objects.equals(dataEntities.get(i).getType(), ModuleFieldEnum.DATETIME.getType())) {
                    columnStyle.setDataFormat(dateFormat.getFormat(DatePattern.NORM_DATETIME_PATTERN));
                } else {
                    columnStyle.setDataFormat(dateFormat.getFormat("@"));
                }
                writer.setColumnWidth(z, 20);
                Cell cell = writer.getOrCreateCell(z, 1);
                //必填字段的特殊处理
                if (Objects.equals(1, dataEntity.getIsNull())) {
                    cell.setCellValue("*" + dataEntity.getName());
                    CellStyle cellStyle = writer.getOrCreateCellStyle(z, 1);
                    Font cellFont = writer.createFont();
                    cellFont.setFontHeightInPoints((short) 11);
                    cellFont.setColor(Font.COLOR_RED);
                    cellStyle.setFont(cellFont);
                    cell.setCellStyle(cellStyle);
                } else {
                    cell.setCellValue(dataEntity.getName());
                }
                // 选择类型增加下拉框
                if (CollUtil.isNotEmpty(dataEntity.getOptionsList())) {
                    String[] array = dataEntity.getOptionsList().stream().map(ModuleOptionsBO::getValue).toArray(String[]::new);
                    writer.addSelect(new CellRangeAddressList(2, 10002, z, z), array);
                }
                // 数据关联字段,直接将对应模块的主字段找到并设置为下拉选
                else if (ObjectUtil.equal(ModuleFieldEnum.DATA_UNION.getType(), dataEntity.getType())) {
                    List<ModuleFieldData> values = ApplicationContextHolder.getBean(IModuleFieldDataService.class).getTargetFieldValuesByUnionFieldId(dataEntity.getFieldId(), queryBO);
                    if (CollUtil.isNotEmpty(values)) {
                        String[] array = values.stream().map(ModuleFieldData::getValue).toArray(String[]::new);
                        writer.addSelect(new CellRangeAddressList(2, 10002, z, z), array);
                    }
                }
                else if (ModuleFieldEnum.ATTENTION.getType().equals(dataEntity.getType())) {
                    writer.addSelect(new CellRangeAddressList(2, 10002, z, z), "一星", "二星", "三星", "四星", "五星");
                }
            }
            //自定义标题别名
            //response为HttpServletResponse对象
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(excelParseService.getExcelName() + "导入模板", "utf-8") + ".xls" + (excelParseService.isXlsx() ? "x" : ""));
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            log.error("下载" + excelParseService.getExcelName() + "导入模板错误", e);
        }

    }


    /**
     * 模块 统一下载导入模板
     */
    public static void moduleImportExcel(ExcelParseService excelParseService, List<ModuleFieldBO> list , List<ModuleFieldBO> allFields, HttpServletResponse response, String module, FieldQueryBO queryBO) {
        // 过滤一些不允许导入的字段
        List<ModuleFieldBO> dataEntities = excelParseService.parseModuleData(list, true);

        try (ExcelWriter writer = ExcelUtil.getWriter(excelParseService.isXlsx())) {
            // 因为重复合并单元格会导致样式丢失，所以先获取全部字段一次合并
            int sum = dataEntities.stream().mapToInt(data -> excelParseService.addCell(null, 0, 0, data.getFieldName())).sum();
            writer.renameSheet(excelParseService.getExcelName() + "导入模板");
            writer.merge(dataEntities.size() - 1 + sum, excelParseService.getMergeContent(module), true);
            writer.getHeadCellStyle().setAlignment(HorizontalAlignment.LEFT);
            writer.getHeadCellStyle().setWrapText(true);
            Font headFont = writer.createFont();
            headFont.setFontHeightInPoints((short) 11);
            writer.getHeadCellStyle().setFont(headFont);
            writer.getHeadCellStyle().setFillPattern(FillPatternType.NO_FILL);
            if (!"subject".equals(module)) {
                writer.getOrCreateRow(0).setHeightInPoints(120);
            } else {
                writer.getOrCreateRow(0).setHeightInPoints(60);
            }
            writer.setRowHeight(-1, 20);
            //设置样式
            for (int i = 0, k = dataEntities.size(), z = 0; i < k; i++, z++) {
                ModuleFieldBO dataEntity = dataEntities.get(i);
                //会新增cell或者对当前cell做调整，直接跳过默认处理
                int n = excelParseService.addCell(writer, z, 1, dataEntity.getFieldName());
                if (n > 0) {
                    z += n;
                    continue;
                }
                CellStyle columnStyle = writer.getOrCreateColumnStyle(z);
                //设置统一字体
                columnStyle.setFont(headFont);
                DataFormat dateFormat = writer.getWorkbook().createDataFormat();
                if (Objects.equals(dataEntities.get(i).getType(), ModuleFieldEnum.DATE.getType())) {
                    columnStyle.setDataFormat(dateFormat.getFormat(DatePattern.NORM_DATE_PATTERN));
                } else if (Objects.equals(dataEntities.get(i).getType(), ModuleFieldEnum.DATETIME.getType())) {
                    columnStyle.setDataFormat(dateFormat.getFormat(DatePattern.NORM_DATETIME_PATTERN));
                } else {
                    columnStyle.setDataFormat(dateFormat.getFormat("@"));
                }
                writer.setColumnWidth(z, 20);
                Cell cell = writer.getOrCreateCell(z, 1);
                //必填字段的特殊处理
                if (Objects.equals(1, dataEntity.getIsNull())) {
                    cell.setCellValue("*" + dataEntity.getName());
                    CellStyle cellStyle = writer.getOrCreateCellStyle(z, 1);
                    Font cellFont = writer.createFont();
                    cellFont.setFontHeightInPoints((short) 11);
                    cellFont.setColor(Font.COLOR_RED);
                    cellStyle.setFont(cellFont);
                    cell.setCellStyle(cellStyle);
                } else {
                    cell.setCellValue(dataEntity.getName());
                }
                // 选择类型增加下拉框
                if (CollUtil.isNotEmpty(dataEntity.getOptionsList())) {
                    String[] array = dataEntity.getOptionsList().stream().map(ModuleOptionsBO::getValue).toArray(String[]::new);
                    writer.addSelect(new CellRangeAddressList(2, 10002, z, z), array);
                }
                // 数据关联字段,直接将对应模块的主字段找到并设置为下拉选
                else if (ObjectUtil.equal(ModuleFieldEnum.DATA_UNION.getType(), dataEntity.getType())) {
                    List<ModuleFieldData> values = ApplicationContextHolder.getBean(IModuleFieldDataService.class).getTargetFieldValuesByUnionFieldId(dataEntity.getFieldId(), queryBO);
                    if (CollUtil.isNotEmpty(values)) {
                        String[] array = values.stream().map(ModuleFieldData::getValue).toArray(String[]::new);
                        writer.addSelect(new CellRangeAddressList(2, 10002, z, z), array);
                    }
                }
                else if (ModuleFieldEnum.ATTENTION.getType().equals(dataEntity.getType())) {
                    writer.addSelect(new CellRangeAddressList(2, 10002, z, z), "一星", "二星", "三星", "四星", "五星");
                }
            }
            //自定义标题别名
            //response为HttpServletResponse对象
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(excelParseService.getExcelName() + "导入模板", "utf-8") + ".xls" + (excelParseService.isXlsx() ? "x" : ""));
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            log.error("下载" + excelParseService.getExcelName() + "导入模板错误", e);
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
        public DataFunc getFunc() {
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
        public List<ExcelDataEntity> parseData(List<?> list, boolean importExcel) {
            List<ExcelDataEntity> entities = list.stream().map(obj -> {
                if (obj instanceof ExcelDataEntity) {
                    return (ExcelDataEntity) obj;
                }
                return BeanUtil.copyProperties(obj, ExcelDataEntity.class);
            }).collect(Collectors.toList());
            if (importExcel) {
                entities.removeIf(head -> removeFieldByType(head.getType()));
            } else {
                entities.removeIf(head -> ModuleFieldEnum.HANDWRITING_SIGN.getType().equals(head.getType()));
            }
            return entities;
        }
        public List<List<String>> parseHeadData(List<?> list, boolean importExcel) {
            List<ExcelDataEntity> entities = list.stream().map(obj -> {
                if (obj instanceof ExcelDataEntity) {
                    return (ExcelDataEntity) obj;
                }
                return BeanUtil.copyProperties(obj, ExcelDataEntity.class);
            }).collect(Collectors.toList());
            List<ExcelDataEntity> tableHead = entities.stream().filter(e -> ObjectUtil.equal(e.getType(), ModuleFieldEnum.DETAIL_TABLE.getType())).collect(Collectors.toList());
            List<List<String>> headList = new ArrayList<>();
            for (Object o : list) {
                List<String> head = new ArrayList<>();

                headList.add(head);
            }
            return null;
        }

        public List<ModuleFieldBO> parseModuleData(List<ModuleFieldBO> list, boolean importExcel) {
            if (importExcel) {
                list.removeIf(head -> removeFieldByType(head.getType()));
            } else {
                list.removeIf(head -> ModuleFieldEnum.HANDWRITING_SIGN.getType().equals(head.getType()));
            }
            return list;
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
            ModuleFieldEnum.AREA_POSITION.getType()
            , ModuleFieldEnum.ATTENTION.getType()
            , ModuleFieldEnum.BOOLEAN_VALUE.getType()
            , ModuleFieldEnum.CHECKBOX.getType()
            , ModuleFieldEnum.CURRENT_POSITION.getType()
            , ModuleFieldEnum.DESC_TEXT.getType()
            , ModuleFieldEnum.DATE_INTERVAL.getType()
            , ModuleFieldEnum.FIELD_GROUP.getType()
            , ModuleFieldEnum.FILE.getType()
            , ModuleFieldEnum.HANDWRITING_SIGN.getType()
            , ModuleFieldEnum.STRUCTURE.getType()
            , ModuleFieldEnum.SERIAL_NUMBER.getType()
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
