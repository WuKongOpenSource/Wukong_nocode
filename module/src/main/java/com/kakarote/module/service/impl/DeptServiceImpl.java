package com.kakarote.module.service.impl;

import com.kakarote.common.entity.SimpleDept;
import com.kakarote.common.result.Result;
import com.kakarote.ids.provider.entity.BO.DeptQueryBO;
import com.kakarote.ids.provider.entity.VO.IdsDeptVO;
import com.kakarote.ids.provider.service.DeptService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author : zjj
 * @since : 2022/12/29
 */
@Service
public class DeptServiceImpl implements DeptService {

    @Override
    public Result<List<Long>> queryChildDeptId(Long aLong) {
        return Result.ok(Arrays.asList(1L));
    }

    @Override
    public Result<List<SimpleDept>> queryDeptByIds(Collection<Long> collection) {
        SimpleDept dept = new SimpleDept();
        dept.setDeptId(1L);
        dept.setDeptName("admin");
        return Result.ok(Arrays.asList(dept));
    }

    @Override
    public Result<String> queryDeptNameByDeptId(Long aLong) {
        return Result.ok("admin");

    }

    @Override
    public Result<List<IdsDeptVO>> queryDeptTree(DeptQueryBO queryBO) {
        return null;
    }
}
