package com.education.quartz;

import com.education.exception.SchedulerException;
import com.education.pojo.JobEntity;
import com.education.repository.JobEntityRepository;
import com.education.util.TaskStateEnum;
import com.education.util.TaskUtils;
import lombok.extern.log4j.Log4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Author: Jet
 * @Description: Job有状态实现类，不允许并发执行
 *  若一个方法一次执行不完下次轮转时则等待该方法执行完后才执行下一次操作
 *  主要是通过注解：@DisallowConcurrentExecution
 * @Date: 2018/3/29 15:36
 */
@Log4j
@DisallowConcurrentExecution
public class QuartzJobFactoryDisallowConcurrentExecution implements Job {

    @Autowired
    JobEntityRepository jobEntityRepository;

    @Override
    @Transactional
    public void execute(JobExecutionContext context){
        JobKey jobKey = context.getJobDetail().getKey();
        JobEntity jobEntityFind = jobEntityRepository.findByJobNameAndJobGroup(jobKey.getName(), jobKey.getGroup());
        log.info("运行任务名称 = [" + jobEntityFind.getJobName() + "]，任务group = [" + jobEntityFind.getJobGroup() + "]");
        try {
            TaskUtils.invokeMethod(jobEntityFind);
            if (context.getNextFireTime() == null) {
                jobEntityFind.setJobStatus(TaskStateEnum.COMPLETE.getIndex());
            } else {
                jobEntityFind.setJobStatus(TaskStateEnum.NORMAL.getIndex());
            }
        } catch (SchedulerException e) {
            log.error(e.getMessage());
            jobEntityFind.setJobStatus(TaskStateEnum.ERROR.getIndex());
            jobEntityFind.setJobExceptionCount(jobEntityFind.getJobExceptionCount() + 1);
        }
        jobEntityFind.setJobExecCount(jobEntityFind.getJobExecCount() + 1);
        jobEntityFind.setPreviousTime(new Date());
        jobEntityFind.setNextTime(context.getNextFireTime());
        jobEntityRepository.save(jobEntityFind);
    }       
       
}  