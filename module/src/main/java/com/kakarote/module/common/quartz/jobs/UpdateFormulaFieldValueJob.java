package com.kakarote.module.common.quartz.jobs;

import cn.hutool.core.util.ObjectUtil;
import com.kakarote.module.constant.ModuleType;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.service.IModuleFieldFormulaProvider;
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
 * @title: UpdateFormulaFieldValueJob
 * @description: UpdateFormulaFieldValueJob
 * @date 2022/4/21 9:55
 */
@Slf4j
@Component
public class UpdateFormulaFieldValueJob implements Job {

    @Autowired
    private IModuleService moduleService;

    @Autowired
    private IModuleFieldFormulaProvider fieldFormulaProvider;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("开始处理计算公式字段值");
        List<ModuleEntity> modules = moduleService.getAll().stream()
                .filter(m -> ObjectUtil.equal(ModuleType.MODULE.getType(), m.getModuleType()))
                .collect(Collectors.toList());
        fieldFormulaProvider.updateFormulaFieldValue(modules);
        log.info("处理完成计算公式字段值");
    }
}
