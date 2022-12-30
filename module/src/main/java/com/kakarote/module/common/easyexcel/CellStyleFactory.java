package com.kakarote.module.common.easyexcel;


import cn.hutool.core.util.StrUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * @author wwl
 * @date 2022/4/17 1:01
 */

public class CellStyleFactory {
    private static final FontRedStyle FONT_RED_STYLE = new FontRedStyle();
    private static final DefaultStyle NORMAL_STYLE_BUILD = new DefaultStyle();

    public static CellStyle style(Cell cell) {
        String value = cell.getStringCellValue();
        if (StrUtil.startWith(value, "*")) {
            return FONT_RED_STYLE.createStyle(cell);
        } else {
            return NORMAL_STYLE_BUILD.createStyle(cell);
        }
    }

    public static void release() {
        FONT_RED_STYLE.releaseStyle();
        NORMAL_STYLE_BUILD.releaseStyle();
    }
}