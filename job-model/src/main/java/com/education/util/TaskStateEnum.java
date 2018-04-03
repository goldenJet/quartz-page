package com.education.util;

/**
 * @Author: Jet
 * @Description: 任务运行状态
 * @Date: 2018/3/29 13:23
 */
public enum TaskStateEnum{
        NONE("NONE","未知"),       
        NORMAL("NORMAL", "正常运行"),       
        PAUSED("PAUSED", "暂停状态"),        
        COMPLETE("COMPLETE","完成"),
        ERROR("ERROR","错误状态"),       
        BLOCKED("BLOCKED","锁定状态"),
        DELETE("DELETE","删除状态");

        private TaskStateEnum(String index, String name) {
            this.name = name;          
            this.index = index;          
        }         
        private String index;         
        private String name;         
        public String getName() {          
            return name;          
        }          
        public String getIndex() {          
            return index;          
        }       
    }       