package com.education.util;

import com.education.exception.SchedulerException;
import com.education.pojo.JobEntity;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Jet
 * @Description: 任务工具类
 * @Date: 2018/3/29 14:12
 */
@Log4j
public class TaskUtils {

    /**
     * 通过反射调用scheduleJob中定义的方法
     *
     * @param jobEntity
     */
    public static void invokeMethod(JobEntity jobEntity) throws SchedulerException {
        Object object = null;
        Class<?> clazz = null;
        //springId不为空先按springId查找bean
        if (StringUtils.isNotBlank(jobEntity.getSpringId())) {
            object = SpringUtils.getBean(jobEntity.getSpringId());
        } else if (StringUtils.isNotBlank(jobEntity.getJobClass())) {//按jobClass查找
            try {
                clazz = Class.forName(jobEntity.getJobClass());
                object = clazz.newInstance();
            } catch (Exception e) {
                log.error(e);
                throw new SchedulerException("job class 查找异常！");
            }
        }
        if (object == null) {
            log.error("任务名称 = [" + jobEntity.getJobName() + "]---------------未启动成功，请检查执行类是否配置正确！！！");
            return;
        }
        clazz = object.getClass();
        Method method = null;
        Map<String, Object> jobDataMap = new HashMap<>();
        if (jobEntity.getJobDataMap() != null) jobDataMap = jobEntity.getJobDataMap();
        try {
            if (StringUtils.isBlank(jobEntity.getMethodName())) {
                method = clazz.getDeclaredMethod("execute", Map.class);
            } else {
                method = clazz.getDeclaredMethod(jobEntity.getMethodName(), Map.class);
            }
        } catch (NoSuchMethodException e) {
            log.error("任务名称 = [" + jobEntity.getJobName() + "]---------------未启动成功，请检查执行类的方法名是否设置错误！！！", e);
            throw new SchedulerException("job method 查找异常！");
        } catch (SecurityException e) {
            log.error(e);
            throw new SchedulerException("job method 查找异常！");
        }
        if (method != null) {
            try {
                method.invoke(object, jobDataMap);
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                log.error(e);
                throw new SchedulerException("job method 参数异常！");
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                log.error(e);
                throw new SchedulerException("job method 参数异常！");
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                log.error(e);
                throw new SchedulerException("job method 参数异常！");
            }
        }
    }

    /**
     * 判断cron时间表达式正确性
     * @param cronExpression
     * @return
     */
    public static boolean isValidExpression(final String cronExpression){
//      CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        CronTriggerImpl trigger = new CronTriggerImpl();
        try {
            trigger.setCronExpression(cronExpression);
            Date date = trigger.computeFirstFireTime(null);
            return date != null && date.after(new Date());
        } catch (ParseException e) {
            log.error("cron 表达式："+ cronExpression +"解析错误", e);
        }
        return false;
    }

    //public static JobEntity convert2JobEntity(JobDetail jobDetail){
    //    jobDetail.
    //}

}
