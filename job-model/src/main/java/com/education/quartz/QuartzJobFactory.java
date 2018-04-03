package com.education.quartz;

import com.education.exception.SchedulerException;
import com.education.pojo.JobEntity;
import com.education.repository.JobEntityRepository;
import com.education.util.TaskStateEnum;
import com.education.util.TaskUtils;
import lombok.extern.log4j.Log4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Author: Jet
 * @Description: Job实现类  无状态
 * 若此方法上一次还未执行完，而下一次执行时间轮到时则该方法也可并发执行
 * @Date: 2018/3/29 15:35
 */
@Log4j
public class QuartzJobFactory implements Job {

    @Autowired
    JobEntityRepository jobEntityRepository;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
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