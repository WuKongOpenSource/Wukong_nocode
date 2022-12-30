package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.CustomNoticeSaveBO;
import com.kakarote.module.entity.PO.CustomNotice;

import java.util.List;

/**
 * @author zjj
 * @title: ICustomNoticeService
 * @description: 自定义通知服务接口
 * @date 2022/3/22 16:51
 */
public interface ICustomNoticeService extends BaseService<CustomNotice> {

    /**
     * 获取模块的自定义提醒
     *
     * @param moduleId
     * @param version
     * @return
     */
    List<CustomNotice> getByModuleIdAndVersion(Long moduleId, Integer version);

    /**
     * 查询自定义通知配置
     *
     * @param moduleId  模块 ID
     * @param version   版本号
     * @return  data
     */
    List<CustomNoticeSaveBO> queryList(Long moduleId, Integer version);

    /**
     * 查询自定义通知配置
     *
     * @param noticeId
     * @param version
     * @return
     */
    CustomNoticeSaveBO queryByNoticeId(Long noticeId, Integer version);

    /**
     * 获取指定的自定义提醒
     *
     * @param noticeId
     * @param version
     * @return
     */
    CustomNotice getByNoticeId(Long noticeId, Integer version);
}
