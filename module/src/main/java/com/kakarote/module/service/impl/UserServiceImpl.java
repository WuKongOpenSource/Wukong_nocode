package com.kakarote.module.service.impl;

import com.kakarote.common.constant.UserStatusEnum;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.Result;
import com.kakarote.ids.provider.entity.BO.DeptQueryBO;
import com.kakarote.ids.provider.entity.BO.UserQueryBO;
import com.kakarote.ids.provider.entity.VO.IdsDeptVO;
import com.kakarote.ids.provider.entity.VO.OrganizationVO;
import com.kakarote.ids.provider.entity.VO.UserVO;
import com.kakarote.ids.provider.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author : zjj
 * @since : 2022/12/29
 */
@Service
public class UserServiceImpl implements UserService {

    @Override
    public Result<List<Long>> queryChildUserId(Long aLong) {
        return Result.ok(Arrays.asList(1L));
    }

    @Override
    public Result<BasePage<UserVO>> queryUserList(UserQueryBO userQueryBO) {
        return Result.ok(new BasePage<>());
    }

    @Override
    public Result<List<Long>> queryUserList(Integer integer) {
        return Result.ok(Arrays.asList(1L));
    }

    @Override
    public Result<List<UserInfo>> queryUserInfoList() {
        UserInfo info = new UserInfo();
        info.setUserId(1L);
        info.setMobile("18888888888");
        info.setEmail("18888888888@163.com");
        info.setUsername("测试用户");
        info.setNickname("测试用户");
        info.setDeptId(1L);
        info.setUserStatus(UserStatusEnum.NORMAL);
        return Result.ok(Arrays.asList(info));
    }

    @Override
    public Result<List<Long>> queryNormalUserByIds(Collection<Long> collection) {
        return Result.ok(Arrays.asList(1L));

    }

    @Override
    public Result<UserInfo> queryUserInfoByUserId(Long aLong) {
        UserInfo info = new UserInfo();
        info.setUserId(1L);
        info.setMobile("18888888888");
        info.setEmail("18888888888@163.com");
        info.setUsername("测试用户");
        info.setNickname("测试用户");
        info.setDeptId(1L);
        info.setUserStatus(UserStatusEnum.NORMAL);
        return Result.ok(info);

    }

    @Override
    public Result<List<Long>> queryUserByDeptIds(Collection<Long> collection) {
        return Result.ok(Arrays.asList(1L));

    }

    @Override
    public Result<List<Long>> queryUserIdByRoleId(Long aLong) {
        return Result.ok(Arrays.asList(1L));

    }

    @Override
    public Result<Long> querySuperUserId() {
        return Result.ok(1L);

    }

    @Override
    public Result<List<Map<String, Object>>> listByRoleId(List<Long> list) {
        return null;
    }

    @Override
    public Result<UserVO> queryLoginUser() {
        return null;
    }

    @Override
    public Result<OrganizationVO> queryOrganizationInfo() {
        return null;
    }
}
