package com.najie.activiti.service;

import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

public interface ActivitiService {
    //启动流程实例
    public Map<String,Object> startProcessInstanceByKey(String processDefinitionKey,Map<String, Object> variables);
    //取消流程实例
    public void cancelProcessInstance(String processInstanceId);
    //根据流程定义的key查看流程实例信息
    public Map<String,Object> getProcessInstanceInfo(String processKey);
    //根据流程实例的ID查看任务
    public Map<String,Object> getTaskByInstanceId(String processInstanceId);
    //查询历史任务
    public List<Map<String,Object>> getHistoryLocalTask(String processInstanceId);
    //全局历史任务
    public List<Map<String,Object>> getHistoryGlobalTask(String processInstanceId);
    //局部任务通过
    public void completeLocalTask(String taskId,Map<String,Object> variables);
    //全局任务
    public void completeGlobalTask(String taskId,Map<String,Object> variables);
    //设置全局流程变量
    public Map<String,Object> setActivitiVariables(String taskId,Map<String,Object> params);
    //设置局部流程变量
    public Map<String,Object> setActivitiVariableLocal(String taskId,Map<String,Object> params);
    //根据任务ID查询任务信息
    public Map<String,Object> getTaskById(String taskId);
    //根据任务名称查询任务信息
    public Map<String,Object> getTaskByName(String taskName);
    //查询执行流程的信息
    public List<Execution> getExecutionInfo();
    //根据流程实例获取并行任务（针对并行网关）
    public List<Map<String,Object>> getParallelTaskByInstanceId(String processInstanceId);
    //根据指派人查询所属任务
    public List<Map<String,Object>> getTasksByAssignee(String assignee);
    //根据候选人查询所属任务
    public List<Map<String,Object>> getTaskByCandidateUser(String candidateUser);
    //根据候选组查询所属任务
    public List<Map<String,Object>> getTasksByGroup(String group);
    //根据指派人或组来查询任务
    public List<Map<String,Object>> getTasksByAssigneeOrGroup(String assigneeOrGroup);
    //领取任务
    public void claimTask(String taskId,String userId);
}
