package com.kakarote.module.service;

import com.kakarote.module.entity.PO.CustomNoticeRecord;

/**
 * @author : zjj
 * @since : 2022/12/28
 */
public interface ICustomNoticeProvider {

    /**
     * 处理自定义通知
     *
     * @param record
     */
    void dealNoticeRecord(CustomNoticeRecord record);
}
