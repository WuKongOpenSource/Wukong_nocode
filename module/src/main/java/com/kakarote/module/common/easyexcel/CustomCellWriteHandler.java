package com.kakarote.module.common.easyexcel;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author wwl
 * @date 2022/4/16 15:41
 */
public class CustomCellWriteHandler implements CellWriteHandler, Closeable {

    private final Boolean isExport;
    private final Boolean hasTableFlag;

    public CustomCellWriteHandler(Boolean isExport, Boolean hasTableFlag) {
        this.isExport = isExport;
        this.hasTableFlag = hasTableFlag;
    }


    @Override
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row,
                                 Head head, Integer columnIndex, Integer relativeRowIndex, Boolean isHead) {
        int rowNum = row.getRowNum();
        short i = 500;
        if(isHead) {
            i = 600;
            if (!isExport && ObjectUtil.equal(0, rowNum)) {
                i = 3500;
            }
            // setHeight is useless if all cell data is null in this row
        }
        row.setHeight(i);

    }

    @Override
    public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell,
                                Head head, Integer relativeRowIndex, Boolean isHead) {
        // do nothing
    }

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        // do cellStyle
        Cell cell = context.getCell();
        if (context.getHead()) {
            Integer rowIndex = context.getRowIndex();
            if (isExport) {
                CellStyle style = CellStyleFactory.style(cell);
                cell.setCellStyle(style);
            } else {
                if (ObjectUtil.notEqual(0, rowIndex)) {
                    CellStyle style = CellStyleFactory.style(cell);
                    cell.setCellStyle(style);
                } else {
                    WriteCellStyle writeCellStyle = new WriteCellStyle();
                    writeCellStyle.setWrapped(true);
                    context.getFirstCellData().setWriteCellStyle(writeCellStyle);
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        // do close IO
        CellStyleFactory.release();
    }

}
