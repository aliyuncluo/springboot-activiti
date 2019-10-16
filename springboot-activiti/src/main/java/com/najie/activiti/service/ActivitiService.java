package com.najie.activiti.service;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.util.Map;

public interface ActivitiService {
    //启动流程实例
    public Map<String,Object> startProcessInstanceByKey(String processDefinitionKey);
    //根据流程定义的key查看流程实例信息
    public Map<String,Object> getProcessInstanceInfo(String processKey);
    //根据流程实例的ID查看任务
    public Task getTaskByInstanceId(String processInstanceId);
}
