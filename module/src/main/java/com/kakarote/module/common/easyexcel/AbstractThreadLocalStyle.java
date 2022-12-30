package com.kakarote.module.common.easyexcel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * @author wwl
 * @date 2022/4/17 0:59
 */

public abstract class AbstractThreadLocalStyle implements CellStyleCreator {

    private final ThreadLocal<CellStyle> THREAD_LOCAL_STYLE = new ThreadLocal<>();

    @Override
    public CellStyle createStyle(Cell cell) {
        CellStyle cellStyle = THREAD_LOCAL_STYLE.get();
        if (cellStyle == null) {
            cellStyle = doCreateStyle(cell);
            THREAD_LOCAL_STYLE.set(cellStyle);
        }
        return cellStyle;
    }

    public abstract CellStyle doCreateStyle(Cell cell);

    public void releaseStyle() {
        THREAD_LOCAL_STYLE.remove();
    }
}
