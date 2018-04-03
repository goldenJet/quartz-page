package com.education.service;

import com.education.quartz.ScheduleJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

/**
 * Created by leon.huang on 2017/5/19.
 */
@Service
public class QuartzService {


    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;



    public void addScheduleJob(ScheduleJob job) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getObject();
        if (scheduler != null) {
            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getGroupName());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                // 新建
                JobDetail jobDetail = JobBuilder.newJob(job.getClass()).withDescription(job.getDescription()).withIdentity(job.getJobName(), job.getGroupName()).build();
                if(jobDetail.getJobDataMap()!=null && job.getParamentMap()!=null){
                    jobDetail.getJobDataMap().putAll(job.getParamentMap());
                }
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getGroupName()).withSchedule(scheduleBuilder).build();
                scheduler.scheduleJob(jobDetail, trigger);
            } else {
                // 修改时间表达式
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
                scheduler.rescheduleJob(triggerKey, trigger);
            }
        }
    }

    public void removeScheduleJob(ScheduleJob scheduleJob) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getObject();
        TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getGroupName());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if(trigger !=null){
            scheduler.deleteJob(trigger.getJobKey());
        }
    }


    public void removeScheduleJobByKey(JobKey jobKey) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getObject();
        scheduler.deleteJob(jobKey);
    }

    public void pauseScheduler(ScheduleJob scheduleJob) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getObject();
        TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getGroupName());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if(trigger !=null){
            scheduler.pauseTrigger(triggerKey);
        }
    }

    public void resumeScheduler(ScheduleJob scheduleJob) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getObject();
        TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getGroupName());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if(trigger !=null){
            scheduler.resumeTrigger(triggerKey);
        }
    }

    /**
     * 修改参数
     * @param scheduleJob
     */
    public void editParam(ScheduleJob scheduleJob){

    }

    /**
     * 立即执行一次
     * @param scheduleJob
     */
    public void triggerJob(ScheduleJob scheduleJob) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getObject();
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getGroupName());
        if (jobKey != null){
            scheduler.triggerJob(jobKey);
        }
    }

}
