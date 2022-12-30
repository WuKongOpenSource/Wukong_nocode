package com.kakarote.module.common.easyexcel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.ModuleFieldBO;
import com.kakarote.module.entity.BO.ModuleOptionsBO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wwl
 * @date 2022/4/16 23:18
 */
public class CustomSheetWriteHandler implements SheetWriteHandler {

    /**
     * 用来判断哪一列需要下拉选
     */
    private final List<ModuleFieldBO> customFields;
    private final List<ModuleFieldBO> allFields;

    public CustomSheetWriteHandler(List<ModuleFieldBO> customFields,  List<ModuleFieldBO> allFields){
        this.customFields = customFields;
        this.allFields = allFields;
    }

    private void getOptions(WriteWorkbookHolder bookHolder, WriteSheetHolder writeSheetHolder) {
        // 区间设置 第一列第一行和第二行的数据。由于第一行是头，所以第一、二行的数据实际上是第二三行
        // 0行是提醒文字，如果没有表格，头就在1行，那放选项就从2开始，有表格就从3开始
        int firstRow = 2, lastRow = 10002;
        List<ModuleFieldBO> fieldsInTableList = customFields.stream().filter(f -> ObjectUtil.isNotNull(f.getGroupId())).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(fieldsInTableList)) {
            firstRow = 3;
        }
        DataValidationHelper helper = writeSheetHolder.getSheet().getDataValidationHelper();
        DataValidationConstraint constraint;

        // 获得sheet页
        Sheet sheet = writeSheetHolder.getSheet();
        // 获得Workbook 工作蒲
        Workbook workbook = bookHolder.getWorkbook();
        // 创建一个样式
        CellStyle cellDateStyle = workbook.createCellStyle();
        CellStyle cellDateTimeStyle = workbook.createCellStyle();
        // 给样式添加自定义样式 Workbook创建一个时间格式 然后获得时间格式输入自己想要的时间格式
        short dateStr = workbook.createDataFormat().getFormat("yyyy-MM-dd");
        short dateTimeStr = workbook.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss");
        cellDateStyle.setDataFormat(dateStr);
        cellDateTimeStyle.setDataFormat(dateTimeStr);

        int colIndex = 0;
        for (ModuleFieldBO field : customFields) {
            if (ObjectUtil.equal(ModuleFieldEnum.DETAIL_TABLE.getType(), field.getType())) {
                List<ModuleFieldBO> fieldsInTable = allFields.stream().filter(f -> ObjectUtil.equal(f.getGroupId(), field.getGroupId()) && ObjectUtil.notEqual(f.getFieldId(), field.getFieldId())).collect(Collectors.toList());
                for (ModuleFieldBO fieldInTable : fieldsInTable) {
                    if (CollUtil.isNotEmpty(fieldInTable.getOptionsList())) {
                        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(firstRow, lastRow, colIndex, colIndex);
                        String[] array = fieldInTable.getOptionsList().stream().map(ModuleOptionsBO::getValue).toArray(String[]::new);
                        constraint = helper.createExplicitListConstraint(array);
                        DataValidation validOption = helper.createValidation(constraint, cellRangeAddressList);
                        writeSheetHolder.getSheet().addValidationData(validOption);
                    }
                    colIndex++;
                }
            }
            else if (ObjectUtil.equal(ModuleFieldEnum.DATETIME.getType(), field.getType())) {
                sheet.setDefaultColumnStyle(colIndex, cellDateTimeStyle);
                colIndex++;
            }
            else if (ObjectUtil.equal(ModuleFieldEnum.DATE.getType(), field.getType())) {
                sheet.setDefaultColumnStyle(colIndex, cellDateStyle);
                colIndex++;
            }
            else {
                if (CollUtil.isNotEmpty(field.getOptionsList())) {
                    CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(firstRow, lastRow, colIndex, colIndex);
                    String[] array = field.getOptionsList().stream().map(ModuleOptionsBO::getValue).toArray(String[]::new);
                    constraint = helper.createExplicitListConstraint(array);
                    DataValidation validOption = helper.createValidation(constraint, cellRangeAddressList);
                    writeSheetHolder.getSheet().addValidationData(validOption);
                }
                colIndex++;
            }
        }
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        this.getOptions(writeWorkbookHolder, writeSheetHolder);
    }
}
