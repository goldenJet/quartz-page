package com.education.quartz;

import org.quartz.Job;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by leon.huang on 2017/5/19.
 */
public interface ScheduleJob extends Job,Serializable {
    String getDescription();
    String getJobName();
    String getGroupName();
    String getCronExpression();
    Map<String,Object> getParamentMap();
}
