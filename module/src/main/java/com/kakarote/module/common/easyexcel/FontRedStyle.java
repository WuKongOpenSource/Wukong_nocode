package com.kakarote.module.common.easyexcel;

import org.apache.poi.ss.usermodel.*;

/**
 * @author wwl
 * @date 2022/4/17 1:02
 */
public class FontRedStyle extends AbstractThreadLocalStyle {
    @Override
    public CellStyle doCreateStyle(Cell cell) {
        Workbook workbook = cell.getSheet().getWorkbook();
        Font font = workbook.createFont();
        font.setColor(Font.COLOR_RED);
        font.setFontHeightInPoints((short) 12);
        //这玩意不能创建太多
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);

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
