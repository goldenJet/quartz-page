package com.education.controller;

import com.education.pojo.JobEntity;
import com.education.pojo.PageResultForBootstrap;
import com.education.service.JobService;
import com.education.util.PageConfigService;
import lombok.extern.log4j.Log4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: Jet
 * @Description: spring quartz controller
 * @Date: 2018/3/29 11:47
 */
@RequestMapping("/job")
@RestController
@Log4j
public class JobController {

    @Autowired
    JobService jobService;

    @PostMapping("/listAllJobs")
    public PageResultForBootstrap<JobEntity> listAllJobs(@RequestBody Map requestValue) {
        PageRequest pageRequest = PageConfigService.genericPageRequestByRequest(requestValue);
        try {
            return jobService.getAllJobs(pageRequest, requestValue);
        } catch (SchedulerException e) {
            log.error("查询所有job异常");
            return null;
        }
    }

    /*@GetMapping("/listAllRunningJobs")
    public List<JobEntity> listAllRunningJobs(){
        try {
            return jobService.getRunningJob();
        } catch (SchedulerException e) {
            log.error("查询所有running job异常");
            return null;
        }
    }*/

    @PostMapping(value = "/addJob", produces = "text/plain; charset=UTF-8")
    public String addJob(@RequestBody JobEntity jobEntity){
        String result = "success";
        try {
            result = jobService.addJob(jobEntity) ? "success" : "defeat";
        } catch (SchedulerException e) {
            log.error("添加job异常");
            result = "defeat";
        }
        return result;
    }

    /**
     * @Author: Jet
     * @Description: 暂停job
     * @Date: 2018/3/29 15:53
     */
    @PostMapping(value = "/pauseJob", produces = "text/plain; charset=UTF-8")
    public String pauseJob(@RequestBody JobEntity jobEntity){
        return jobService.pauseJob(jobEntity) ? "success" : "defeat";
    }


    /**
     * @Author: Jet
     * @Description: 恢复job
     * @Date: 2018/3/29 15:53
     */
    @PostMapping(value = "/resumeJob", produces = "text/plain; charset=UTF-8")
    public String resumeJob(@RequestBody JobEntity jobEntity){
        return jobService.resumeJob(jobEntity) ? "success" : "defeat";
    }

    /**
     * @Author: Jet
     * @Description: 删除job
     * @Date: 2018/3/29 15:53
     */
    @PostMapping(value = "/deleteJob", produces = "text/plain; charset=UTF-8")
    public String deleteJob(@RequestBody JobEntity jobEntity){
        return jobService.deleteJob(jobEntity) ? "success" : "defeat";
    }

    /**
     * @Author: Jet
     * @Description: 立即执行一次
     * @Date: 2018/3/29 15:53
     */
    @PostMapping(value = "/executeOnce", produces = "text/plain; charset=UTF-8")
    public String executeOnce(@RequestBody JobEntity jobEntity){
        String result = "success";
        try {
            jobService.executeOnce(jobEntity);
        } catch (SchedulerException e) {
            log.error("job 立即执行一次异常");
            result = "defeat";
        }
        return result;
    }


    /**
     * @Author: Jet
     * @Description: 更新任务时间表达式
     * @Date: 2018/3/29 15:53
     */
    /*@PostMapping(value = "/updateCronExpression", produces = "text/plain; charset=UTF-8")
    public String updateCronExpression(@RequestBody JobEntity jobEntity){
        String result = "success";
        try {
            jobService.updateCronExpression(jobEntity);
        } catch (SchedulerException e) {
            log.error("更新 job 任务时间表达式");
            result = "defeat";
        }
        return result;
    }*/



}
