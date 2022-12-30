package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.MsgBodyBO;
import com.kakarote.module.entity.PO.CustomNoticeRecord;

import java.util.List;

/**
 * @author zjj
 * @title: ICustomNoticeRecordService
 * @description: 自定义提醒记录服务接口
 * @date 2022/3/23 17:11
 */
public interface ICustomNoticeRecordService extends BaseService<CustomNoticeRecord> {

    /**
     * 根据状态查询自定义提醒记录
     *
     * @param status
     * @param limit
     * @return
     */
    List<CustomNoticeRecord> queryList(Integer status, Integer limit);

    /**
     * 保存自定义提醒记录
     *
     * @param msgBodyBO
     */
    void saveNoticeRecord(MsgBodyBO msgBodyBO);

    /**
     * 删除模块的自定义提醒记录
     *
     * @param moduleId
     */
    void deleteByModuleId(Long moduleId);
}
