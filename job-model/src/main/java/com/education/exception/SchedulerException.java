package com.education.exception;

/**
 * @Author: Jet
 * @Description: 自定义异常
 * @Date: 2018/4/2 11:07
 */
public class SchedulerException extends Exception{
    public SchedulerException(){
        super();
    }
    public SchedulerException(String msg){
        super(msg);
    }
}
