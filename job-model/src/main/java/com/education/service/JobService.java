package com.education.service;

import com.alibaba.fastjson.JSON;
import com.education.pojo.JobEntity;
import com.education.pojo.PageResultForBootstrap;
import com.education.quartz.QuartzJobFactory;
import com.education.quartz.QuartzJobFactoryDisallowConcurrentExecution;
import com.education.repository.JobEntityRepository;
import com.education.util.TaskStateEnum;
import com.education.util.TaskUtils;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: Jet
 * @Description: job service
 * @Date: 2018/3/29 15:24
 */
@Service
@Log4j
public class JobService {
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    JobEntityRepository jobEntityRepository;

    /**
     * 获取单个任务
     * @param jobName
     * @param jobGroup
     * @return
     * @throws SchedulerException
     */
    /*public JobEntity getJob(String jobName, String jobGroup) throws SchedulerException {
        JobEntity job = null;
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if (null != trigger) {
            job = new JobEntity();
            job.setJobName(jobName);
            job.setJobGroup(jobGroup);
            job.setDescription("触发器:" + trigger.getKey());
            job.setNextTime(trigger.getNextFireTime()); //下次触发时间
            job.setPreviousTime(trigger.getPreviousFireTime());//上次触发时间
            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            job.setJobStatus(triggerState.name());
            if (trigger instanceof CronTrigger) {
                CronTrigger cronTrigger = (CronTrigger) trigger;
                String cronExpression = cronTrigger.getCronExpression();
                job.setCronExpression(cronExpression);
            }
        }
        return job;
    }*/

    /**
     * 获取所有任务
     * @return
     * @throws SchedulerException
     */
    public PageResultForBootstrap<JobEntity> getAllJobs(PageRequest pageRequest, Map requestValue) throws SchedulerException{
        /*Scheduler scheduler = schedulerFactoryBean.getScheduler();
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        List<JobEntity> jobList = new ArrayList<JobEntity>();
        for (JobKey jobKey : jobKeys) {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggers) {
                jobList.add(new JobEntity(scheduler.getJobDetail(jobKey), trigger, scheduler));
            }
        }*/
        Specification specification = (Specification<JobEntity>)(root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();


            if (!org.springframework.util.StringUtils.isEmpty(requestValue.get("status"))) {
                predicates.add(cb.equal(root.get("jobStatus").as(String.class), requestValue.get("status").toString()));
            } else {
                predicates.add(cb.notEqual(root.get("jobStatus").as(String.class), TaskStateEnum.COMPLETE.getIndex()));
            }
            predicates.add(cb.equal(root.get("isDelete").as(Boolean.class), Boolean.FALSE));

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        Page<JobEntity> page = jobEntityRepository.findAll(specification, pageRequest);
        PageResultForBootstrap<JobEntity> result = new PageResultForBootstrap<>(page);
        page.getContent().forEach(val -> {
            if (StringUtils.isNotBlank(val.getJobDataMapString())) {
                val.setJobDataMap(JSON.parseObject(val.getJobDataMapString(), Map.class));
            }
        });
        result.setRows(page.getContent());
        return result;
    }

    /**
     * 所有正在运行的job
     *
     * @return
     * @throws SchedulerException
     */
    /*public List<JobEntity> getRunningJob() throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
        List<JobEntity> jobList = new ArrayList<JobEntity>(executingJobs.size());
        for (JobExecutionContext executingJob : executingJobs) {
            jobList.add(new JobEntity(executingJob.getJobDetail(), executingJob.getTrigger(), scheduler));
        }
        return jobList;
    }*/

    /**
     * 添加任务

     */
    @Transactional
    public boolean addJob(JobEntity job) throws SchedulerException {
        if (job == null) {
            return false;
        }
        if(!TaskUtils.isValidExpression(job.getCronExpression())){
            log.error("时间表达式错误（"+job.getJobName()+","+job.getJobGroup()+"）,"+job.getCronExpression());
            return false;
        }else{
            JobEntity job2Save;
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            // 任务名称和任务组设置规则：    // 名称：task_1 ..    // 组 ：group_1 ..
            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            // 不存在，创建一个
            if (null == trigger) {
                //是否允许并发执行
                Class<? extends Job> clazz = job.getIsConcurrentDisallowed() != null && JobEntity.CONCURRENT_DISALLOWED.equals(job.getIsConcurrentDisallowed()) ? QuartzJobFactoryDisallowConcurrentExecution.class : QuartzJobFactory.class;
                JobBuilder jobBuilder = JobBuilder.newJob(clazz).withIdentity(job.getJobName(), job.getJobGroup());
                if (StringUtils.isNotBlank(job.getDescription())) {
                    // 备注配置
                    jobBuilder.withDescription(job.getDescription());
                }
                JobDetail jobDetail = jobBuilder.build();
                if (job.getJobDataMap() != null) {
                    // 参数配置
                    jobDetail.getJobDataMap().putAll(job.getJobDataMap());
                }
                // 表达式调度构建器
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                // 按新的表达式构建一个新的trigger
                trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
                scheduler.scheduleJob(jobDetail, trigger);

                job2Save = job;
                if (job2Save.getJobDataMap() != null){
                    job2Save.setJobDataMapString(JSON.toJSONString(job2Save.getJobDataMap()));
                }

            } else {
                // trigger已存在，则更新相应的定时设置
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                // 按新的cronExpression表达式重新构建trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
                // 按新的trigger重新设置job执行
                scheduler.rescheduleJob(triggerKey, trigger);

                job2Save = jobEntityRepository.findByJobNameAndJobGroup(job.getJobName(), job.getJobGroup());
                if (job.getJobDataMap() != null){
                    job2Save.setJobDataMapString(JSON.toJSONString(job.getJobDataMap()));
                } else {
                    job2Save.setJobDataMapString(null);
                }
                job2Save.setSpringId(job.getSpringId());
                job2Save.setJobClass(job.getJobClass());
                job2Save.setMethodName(job.getMethodName());
                job2Save.setDescription(job.getDescription());
                job2Save.setCronExpression(job.getCronExpression());

            }

            // 自定义实体类存入数据库
            if (job2Save.getJobStatus() == null) {
                job2Save.setJobStatus(TaskStateEnum.NORMAL.getIndex());
            }
            job2Save.setIsDelete(false);
            job2Save.setStartTime(trigger.getStartTime());
            job2Save.setNextTime(trigger.getNextFireTime());
            job2Save.setTriggerName(trigger.getKey().getName());
            job2Save.setTriggerGroup(trigger.getKey().getGroup());
            job2Save.setTriggerTimeZone(trigger.getTimeZone().getID());
            jobEntityRepository.save(job2Save);
        }
        return true;
    }
    /**
     * 暂停任务
     * @param jobEntity
     * @return
     */
    @Transactional
    public boolean pauseJob(JobEntity jobEntity){
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey(jobEntity.getJobName(), jobEntity.getJobGroup());
        try {
            JobEntity jobFind = jobEntityRepository.findByJobNameAndJobGroup(jobEntity.getJobName(), jobEntity.getJobGroup());
            jobFind.setJobStatus(TaskStateEnum.PAUSED.getIndex());
            jobEntityRepository.save(jobFind);

            scheduler.pauseJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("暂停任务异常: " + jobEntity.toString(), e);
        }
        return false;
    }

    /**
     * 恢复任务
     * @param jobEntity
     * @return
     */
    @Transactional
    public boolean resumeJob(JobEntity jobEntity){
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey(jobEntity.getJobName(), jobEntity.getJobGroup());
        try {
            JobEntity jobFind = jobEntityRepository.findByJobNameAndJobGroup(jobEntity.getJobName(), jobEntity.getJobGroup());
            jobFind.setJobStatus(TaskStateEnum.NORMAL.getIndex());
            jobEntityRepository.save(jobFind);

            scheduler.resumeJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("恢复任务异常: " + jobEntity.toString(), e);
        }
        return false;
    }

    /**
     * 删除任务
     */
    @Transactional
    public boolean deleteJob(JobEntity jobEntity){
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey(jobEntity.getJobName(), jobEntity.getJobGroup());
        try{
            JobEntity jobFind = jobEntityRepository.findByJobNameAndJobGroup(jobEntity.getJobName(), jobEntity.getJobGroup());
            jobFind.setJobStatus(TaskStateEnum.DELETE.getIndex());
            jobFind.setIsDelete(true);
            jobEntityRepository.save(jobFind);

            scheduler.deleteJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("删除任务异常: " + jobEntity.toString(), e);
        }
        return false;
    }

    /**
     * 立即执行一个任务
     * @param jobEntity
     * @throws SchedulerException
     */
    public void executeOnce(JobEntity jobEntity) throws SchedulerException{
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey(jobEntity.getJobName(), jobEntity.getJobGroup());
        scheduler.triggerJob(jobKey);
    }

    /**
     * 更新任务时间表达式
     * @param jobEntity
     * @throws SchedulerException
     */
    /*public void updateCronExpression(JobEntity jobEntity) throws SchedulerException{
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(jobEntity.getJobName(), jobEntity.getJobGroup());
        //获取trigger，即在spring配置文件中定义的 bean id="myTrigger"
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        //表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(jobEntity.getCronExpression());
        //按新的cronExpression表达式重新构建trigger
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        //按新的trigger重新设置job执行
        scheduler.rescheduleJob(triggerKey, trigger);
    }*/
}
