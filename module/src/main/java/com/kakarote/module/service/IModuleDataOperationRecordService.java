package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.constant.ActionTypeEnum;

import com.kakarote.module.entity.BO.ModuleFieldValueBO;
import com.kakarote.module.entity.PO.ModuleDataOperationRecord;
import com.kakarote.module.entity.VO.ModuleDataOperationRecordVO;

import java.util.List;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-10 10:41
 */
public interface IModuleDataOperationRecordService extends BaseService<ModuleDataOperationRecord> {

    /**
     * 构建操作记录 entity
     *
     * @param moduleId    模块ID
     * @param version     版本号
     * @param dataId      数据ID
     * @param value       主字段值
     * @param oldData     历史数据
     * @param currentData 当前数据
     * @param actionType  操作类型
     * @return
     */
    ModuleDataOperationRecord initEntity(Long moduleId, Integer version, Long dataId, String value,
                                         List<ModuleFieldValueBO> oldData,
                                         List<ModuleFieldValueBO> currentData,
                                         ActionTypeEnum actionType);

    /**
     * 构建操作记录 entity
     *
     * @param moduleId   模块ID
     * @param version    版本号
     * @param dataId     数据ID
     * @param value      主字段值
     * @param userId     团队成员ID
     * @param actionType 操作类型
     * @return
     */
    ModuleDataOperationRecord initTeamUserEntity(Long moduleId, Integer version, Long dataId, String value, Long userId, ActionTypeEnum actionType);

    /**
     * 构建操作记录 entity
     *
     * @param moduleId   模块ID
     * @param version    版本号
     * @param dataId     数据ID
     * @param value      主字段值
     * @param fromUserId 原负责人ID
     * @param toUserId   现负责人ID
     * @param actionType 操作类型
     * @return
     */
    ModuleDataOperationRecord initTransferEntity(Long moduleId, Integer version, Long dataId, String value, Long fromUserId, Long toUserId, ActionTypeEnum actionType);

    /**
     * 查询字段值的操作记录
     *
     * @param moduleId
     * @param dataId
     * @return
     */
    List<ModuleDataOperationRecordVO> queryRecord(Long moduleId, Long dataId);

}
