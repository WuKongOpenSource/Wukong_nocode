package com.kakarote.module.common.quartz.jobs;

import cn.hutool.core.util.ObjectUtil;
import com.kakarote.module.constant.ModuleType;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.service.ICustomCategoryService;
import com.kakarote.module.service.IModuleService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: DealCategoryJob
 * @description: DealCategoryJob
 * @date 2022/4/26 16:40
 */
@Slf4j
@Component
public class DealCategoryJob implements Job {

    @Autowired
    private IModuleService moduleService;

    @Autowired
    private ICustomCategoryService categoryService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        log.info("开始处理模块分类");
        List<ModuleEntity> modules = moduleService.getAll().stream()
                .filter(m -> ObjectUtil.equal(ModuleType.MODULE.getType(), m.getModuleType()))
                .collect(Collectors.toList());
        for (ModuleEntity module : modules) {
            categoryService.dealDataCategory(module.getModuleId());
        }
        log.info("理完成模块分类");

    }
}
