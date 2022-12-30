package com.kakarote.module.common.quartz.jobs;


import com.kakarote.common.redis.Redis;
import com.kakarote.module.entity.PO.CustomNoticeRecord;
import com.kakarote.module.service.*;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author zjj
 * @title: NoticeJob
 * @description: NoticeJob
 * @date 2022/3/23 15:46
 */
@Slf4j
@Component
public class NoticeJob implements Job {

    @Autowired
    private ICustomNoticeProvider noticeProvider;

    @Autowired
    private ICustomNoticeRecordService recordService;

    @Autowired
    private Redis redis;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<CustomNoticeRecord> records = recordService.queryList(0, 20000);
        log.info("开始处理自定义通知:{}条", records.size());
        for (CustomNoticeRecord record : records) {
            String key = String.format("%s-%s-%s-%s", "NOTICE_RECORD", record.getModuleId(), record.getNoticeId(), record.getDataId());
            if (!redis.setNx(key, 10L, record.getBatchId())) {
                continue;
            }
            try {
                noticeProvider.dealNoticeRecord(record);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
