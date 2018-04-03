package com.education.pojo;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

@Data
@Entity
@Table(name="job_entity")
public class JobEntity {
    public static final String STATUS_RUNNING = "1";
    public static final String STATUS_NOT_RUNNING = "0";
    public static final String CONCURRENT_DISALLOWED = "1";
    public static final String CONCURRENT_ALLOWED = "0";

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    /** 任务id */
    //private String jobId;

    /** 任务名称 */
    private String jobName;

    /** 任务分组，任务名称+组名称应该是唯一的 */
    private String jobGroup;

    /** 任务状态*/
    private String jobStatus;

    /** 任务data */
    private String jobDataMapString;
    @Transient
    private Map<String, Object> jobDataMap;

    /** 触发器名称*/
    private String triggerName;

    /** 触发器分组*/
    private String triggerGroup;

    /** 触发器时区*/
    private String triggerTimeZone;

    /** 任务是否取消并发 （默认是有并发的） */
    private String isConcurrentDisallowed;

    /** 任务运行时间表达式 */
    private String cronExpression;

    /** 任务描述 */
    private String description;

    /** 任务调用类在spring中注册的bean id，如果spingId不为空，则按springId查找 */
    private String springId;

    /** 任务调用类名，包名+类名，通过类反射调用 ，如果spingId为空，则按jobClass查找   */
    private String jobClass;

    /** 任务调用的方法名 */
    private String methodName;

    /** 启动时间 */
    private Date startTime;

    /** 前一次运行时间 */
    private Date previousTime;

    /** 下次运行时间 */
    private Date nextTime;

    /** job 运行次数（包括异常）*/
    private int jobExecCount;

    /** job 运行异常次数*/
    private int jobExceptionCount;

    @Column(name="create_dt")
    private Timestamp createDate;

    // 1:已删除，0:未删除
    @Column(name="is_del")
    private Boolean isDelete;

    public JobEntity(){}

    /*public JobEntity(JobDetail jobDetail, Trigger trigger, Scheduler scheduler){
        JobKey jobKey = jobDetail.getKey();
        TriggerKey triggerKey = trigger.getKey();
        this.jobName = jobKey.getName();
        this.jobGroup = jobKey.getGroup();
        try {
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            this.jobStatus = triggerState.name();
        } catch (SchedulerException e) { }
        this.jobDataMap = jobDetail.getJobDataMap();
        if (this.jobDataMap != null) {
            this.jobDataMapString = JSON.toJSONString(this.jobDataMap);
        }
        this.triggerName = triggerKey.getName();
        this.triggerGroup = triggerKey.getGroup();
        if (trigger instanceof CronTrigger) {
            CronTrigger cronTrigger = (CronTrigger) trigger;
            this.cronExpression = cronTrigger.getCronExpression();
            this.triggerTimeZone = cronTrigger.getTimeZone().getID();
        }
        this.isConcurrentDisallowed = jobDetail.isConcurrentExectionDisallowed() ? "1" : "0";
        this.description = jobDetail.getDescription();
        this.jobClass = jobDetail.getJobClass().getName();
        //this.methodName = jobDetail.
        this.startTime = trigger.getStartTime();
        this.previousTime = trigger.getPreviousFireTime();
        this.nextTime = trigger.getNextFireTime();
        this.setIsDelete(false);
    }*/
}
