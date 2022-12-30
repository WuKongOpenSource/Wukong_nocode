package com.kakarote.module.common.quartz;

import com.kakarote.module.common.quartz.jobs.DealCategoryJob;
import com.kakarote.module.common.quartz.jobs.NoticeJob;
import com.kakarote.module.common.quartz.jobs.UpdateFormulaFieldValueJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zjj
 * @title: QuartzConfig
 * @description: QuartzConfig
 * @date 2022/3/23 15:43
 */
@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail noticeJobDetail() {
        return JobBuilder.newJob(NoticeJob.class).withIdentity(NoticeJob.class.getName()).storeDurably().build();
    }

    @Bean
    public Trigger noticeTrigger() {
        return TriggerBuilder.newTrigger().forJob(noticeJobDetail()).withIdentity(NoticeJob.class.getName())
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(10).repeatForever()).startNow()
                .build();
    }

    @Bean
    public JobDetail updateFormulaFieldValueJobDetail() {
        return JobBuilder.newJob(UpdateFormulaFieldValueJob.class).withIdentity(UpdateFormulaFieldValueJob.class.getName()).storeDurably().build();
    }

    @Bean
    public Trigger updateFormulaFieldValueTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(updateFormulaFieldValueJobDetail())
                .withIdentity(UpdateFormulaFieldValueJob.class.getName())
                .withSchedule(CronScheduleBuilder.cronSchedule("0 1 0 * * ? ").withMisfireHandlingInstructionFireAndProceed())
                .startNow()
                .build();
    }

    @Bean
    public JobDetail dealCategoryJobDetail() {
        return JobBuilder.newJob(DealCategoryJob.class).withIdentity(DealCategoryJob.class.getName()).storeDurably().build();
    }

    @Bean
    public Trigger dealCategoryTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(dealCategoryJobDetail())
                .withIdentity(DealCategoryJob.class.getName())
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 * * ? ").withMisfireHandlingInstructionFireAndProceed())
                .startNow()
                .build();
    }

}
