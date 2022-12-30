package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.constant.FlowTypeEnum;
import com.kakarote.module.entity.BO.ToDoBO;
import com.kakarote.module.entity.BO.ToDoListQueryBO;
import com.kakarote.module.entity.BO.ToDoSaveBO;
import com.kakarote.module.entity.BO.ToDoUpdateBO;
import com.kakarote.module.entity.PO.ToDo;
import com.kakarote.module.mapper.ToDoMapper;
import com.kakarote.module.service.IToDoService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ToDoServiceImpl extends BaseServiceImpl<ToDoMapper, ToDo> implements IToDoService {

    @Override
    public BasePage<ToDoBO> queryList(ToDoListQueryBO queryBO) {
        if (ObjectUtil.isNull(queryBO)) {
            return null;
        }
        LambdaQueryWrapper<ToDo> wrapper = new LambdaQueryWrapper<>();
        // 查询类型 0 待我处理、1 我发起的、2 抄送我的、3 已完成
        if (ObjectUtil.equal(0, queryBO.getQueryType())) {
            wrapper.eq(ToDo::getStatus, 0);
            wrapper.eq(ToDo::getOwnerUserId, UserUtil.getUserId());
        } else if (ObjectUtil.equal(1, queryBO.getQueryType())) {
            wrapper.eq(ToDo::getCreateUserId, UserUtil.getUserId());
            wrapper.eq(ToDo::getFlowType, FlowTypeEnum.START.getType());
        } else if (ObjectUtil.equal(2, queryBO.getQueryType())) {
            wrapper.eq(ToDo::getOwnerUserId, UserUtil.getUserId());
            wrapper.eq(ToDo::getFlowType, FlowTypeEnum.COPY.getType());
        } else if (ObjectUtil.equal(3, queryBO.getQueryType())) {
            wrapper.eq(ToDo::getStatus, 1);
            wrapper.eq(ToDo::getOwnerUserId, UserUtil.getUserId());
        }
        // 应用筛选
        if (ObjectUtil.isNotNull(queryBO.getApplicationId())) {
            wrapper.eq(ToDo::getApplicationId, queryBO.getApplicationId());
        }
        // 模块筛选
        if (ObjectUtil.isNotNull(queryBO.getModuleId())) {
            wrapper.eq(ToDo::getModuleId, queryBO.getModuleId());
        }
        // 发起人筛选
        if (CollUtil.isNotEmpty(queryBO.getCreateUserIds())) {
            wrapper.in(ToDo::getCreateUserId, queryBO.getCreateUserIds());
        }
        // 日期筛选
        if (ObjectUtil.isNotNull(queryBO.getFromDate()) && ObjectUtil.isNotNull(queryBO.getToDate())) {
            wrapper.between(ToDo::getCreateTime, queryBO.getFromDate(), queryBO.getToDate());
        }
        wrapper.orderByDesc(ToDo::getCreateTime);
        BasePage<ToDo> page = baseMapper.selectPage(queryBO.parse(), wrapper);
        List<ToDo> list = page.getList();

        List<ToDoBO> toDoBOList = new ArrayList<>();
        for (ToDo toDo : list) {
            ToDoBO toDoBO = BeanUtil.copyProperties(toDo, ToDoBO.class);
            toDoBO.setCreateUser(UserCacheUtil.getSimpleUser(toDoBO.getCreateUserId()));
            toDoBOList.add(toDoBO);
        }
        BasePage<ToDoBO> result = new BasePage<>();
        result.setPageNumber(page.getPageNumber());
        result.setTotal(page.getTotal());
        result.setCurrent(page.getCurrent());
        result.setList(toDoBOList);
        return result;
    }

    @Override
    public void save(ToDoSaveBO saveBO) {
        ToDo toDo = BeanUtil.copyProperties(saveBO, ToDo.class);
        toDo.setCreateTime(DateUtil.date());
        save(toDo);
    }

    @Override
    public void setViewed(Long id) {
        lambdaUpdate()
                .set(ToDo::getViewed, true)
                .set(ToDo::getViewTime, DateUtil.date())
                .eq(ToDo::getId, id)
                .update();
    }

    @Override
    public void update(ToDoUpdateBO updateBO) {
        lambdaUpdate()
                .set(ToDo::getUpdateTime, DateUtil.date())
                .set(ToDo::getStatus, updateBO.getStatus())
                .eq(ToDo::getId, updateBO.getId())
                .update();
    }
}
