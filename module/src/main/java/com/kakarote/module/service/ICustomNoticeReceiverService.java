package com.kakarote.module.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseService;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.entity.BO.CustomNoticeReceiverSaveBO;
import com.kakarote.module.entity.PO.CustomNoticeReceiver;
import com.kakarote.module.entity.PO.ModuleRoleUser;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: ICustomNoticeReceiverService
 * @description: 自定义提醒接收人
 * @date 2022/3/23 10:01
 */
public interface ICustomNoticeReceiverService extends BaseService<CustomNoticeReceiver> {

    /**
     * 获取模块的自定义提醒接收人
     *
     * @param moduleId
     * @param version
     * @return
     */
    List<CustomNoticeReceiver> getByModuleIdAndVersion(Long moduleId, Integer version);

    /**
     * 获取通知的接收人配置
     *
     * @param noticeId
     * @param version
     * @return
     */
    CustomNoticeReceiver getByNoticeId(Long noticeId, Integer version);


    /**
     * 根据通知配置获取通知接收人
     *
     * @param receiverSaveBO
     * @param dataId
     * @return
     */
    Set<Long> getReceivers(CustomNoticeReceiverSaveBO receiverSaveBO, Long dataId);


    /**
     * 获取指定角色的用户
     *
     * @param userInfos
     * @param roleId
     * @return
     */
    default List<Long> queryUserByRoleId(List<UserInfo> userInfos, Long roleId) {
        List<Long> userIdList = userInfos.stream().filter(u -> u.getRoles().contains(roleId))
                .map(UserInfo::getUserId).distinct().collect(Collectors.toList());
        // 无代码用户角色关系
        List<ModuleRoleUser> moduleRoleUsers = ApplicationContextHolder.getBean(IModuleRoleUserService.class).getByRoleId(roleId);
        List<Long> userIds = moduleRoleUsers.stream()
                .filter(m -> !CollUtil.contains(userIdList, m.getUserId()))
                .map(ModuleRoleUser::getUserId).collect(Collectors.toList());
        userIdList.addAll(userIds);
        return userIdList;
    }

    /**
     * 查询用户的指定上级
     *
     * @param userId
     * @param parentLevel
     * @return
     */
    default Long queryParentUserByLevel(Long userId, Integer parentLevel) {
        UserInfo user = UserCacheUtil.getUserInfo(userId);
        if (ObjectUtil.isNotNull(user.getParentId())) {
            parentLevel--;
            if (parentLevel > 0) {
                Long targetUserId = queryParentUserByLevel(user.getParentId(), parentLevel);
                if (ObjectUtil.isNotNull(targetUserId)) {
                    return targetUserId;
                }
            } else {
                return user.getParentId();
            }
        }
        return null;
    }

}
