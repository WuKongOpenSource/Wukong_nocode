package com.kakarote.module.common.easyexcel;

import org.apache.poi.ss.usermodel.*;

/**
 * @author wwl
 * @date 2022/4/17 1:02
 */
public class DefaultStyle extends AbstractThreadLocalStyle {
    @Override
    public CellStyle doCreateStyle(Cell cell) {
        Workbook workbook = cell.getSheet().getWorkbook();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        DataFormat dataFormat = workbook.createDataFormat();
        cellStyle.setDataFormat(dataFormat.getFormat("@"));
        cellStyle.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFillPattern(FillPatternType.NO_FILL);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        return cellStyle;
    }
}
