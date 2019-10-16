package com.najie.activiti.service;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivitiServiceImpl implements ActivitiService{
    public static final String PROCESS_FILE = "processes/leave-process.bpmn";
    private Logger logger = LoggerFactory.getLogger(ActivitiServiceImpl.class);
    @Autowired
    private  RuntimeService runtimeService;
    @Autowired
    private  TaskService taskService;
    @Autowired
    private  HistoryService historyService;
    @Autowired
    private  RepositoryService repositoryService;

    //部署流程定义
    @PostConstruct
    public ProcessDefinition deployProcessDefinition() throws IOException {
        //String resource = ResourceLoader.CLASSPATH_URL_PREFIX+PROCESS_FILE;
        Deployment deployment = repositoryService.createDeployment().addClasspathResource(PROCESS_FILE).deploy();
        logger.info("deployment["+deployment+"]");
        logger.info("Process [" + deployment.getName() + "] deployed successful");
        return repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
    }


    /**
     * 启动流程实例
     * @param processDefinitionKey 流程定义的key
     * @return
     */
    @Transactional
    public Map<String,Object> startProcessInstanceByKey(String processDefinitionKey) {
        Map<String,Object> map = new HashMap<>();
        if (StringUtils.isEmpty(processDefinitionKey)) {
            return null;
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("username","张三");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey,variables);
        logger.info("流程实例启动成功");
        String processInstanceId = processInstance.getId();
        String processInstanceName = processInstance.getName();
        String processDefinitionId = processInstance.getProcessDefinitionId();
        String processDefinitionName = processInstance.getProcessDefinitionName();
        Integer processDefinitionVersion = processInstance.getProcessDefinitionVersion();
        map.put("code","10000");
        map.put("msg","流程实例启动成功");
        map.put("processInstanceId",processInstanceId);
        map.put("processInstanceName",processInstanceName);
        map.put("processDefinitionId",processDefinitionId);
        map.put("processDefinitionName",processDefinitionName);
        map.put("processDefinitionVersion",processDefinitionVersion);
        return map;
    }

    /**
     * 根据流程定义的key,查询流程实例
     * @param processDefinitionKey 流程定义的key
     * @return
     */
    @Transactional
    public Map<String,Object> getProcessInstanceInfo(String processDefinitionKey){
        Map<String,Object> map = new HashMap<>();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).desc().singleResult();
        String processInstanceId = processInstance.getId();
        String processInstanceName = processInstance.getName();
        String processDefinitionId = processInstance.getProcessDefinitionId();
        String processDefinitionName = processInstance.getProcessDefinitionName();
        Integer processDefinitionVersion = processInstance.getProcessDefinitionVersion();
        map.put("processInstanceId",processInstanceId);
        map.put("processInstanceName",processInstanceName);
        map.put("processDefinitionId",processDefinitionId);
        map.put("processDefinitionName",processDefinitionName);
        map.put("processDefinitionVersion",processDefinitionVersion);
        return map;
    }

    public Map<String,Object> setActivitiVariables(String taskId,Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try {
            taskService.setVariables(taskId,params);
            map.put("code","10000");
            map.put("msg","流程变量参数设置成功");
        } catch (Exception e) {
            map.put("code","21000");
            map.put("msg","流程变量参数设置失败");
        }
        return map;
    }

    /**
     * 根据流程实例ID查询当前任务
     * @param processInstanceId 流程实例ID
     * @return
     */
    public Task getTaskByInstanceId(String processInstanceId){
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        return task;
    }

    /**
     * 根据候选人查询任务
     * @param candidate
     * @return
     */
    public List<Task> getTaskByCandidate(String candidate){
        if (StringUtils.isEmpty(candidate)) {
            return null;
        }
        List<Task> list = taskService.createTaskQuery().taskCandidateUser(candidate).orderByTaskCreateTime().desc().list();
        return list;
    }
    /**
     * 根据指派人查询当前任务
     * @param assignee
     * @return
     */
    public List<Task> getTasksByAssignee(String assignee) {
        if (StringUtils.isEmpty(assignee)) {
            return null;
        }
        List<Task> taskList = taskService.createTaskQuery().taskAssignee(assignee).list();
        return taskList;
    }

    /**
     * 根据所在组查询任务
     * @param group
     * @return
     */
    public List<Task> getTasksByGroup(String group) {
        if (StringUtils.isEmpty(group)) {
            return null;
        }
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(group).list();
        return tasks;
    }

    /**
     * 根据指派人或组来查询任务
     * @param assigneeOrGroup
     * @return
     */
    public List<Task> getTasks(String assigneeOrGroup) {
        if (StringUtils.isEmpty(assigneeOrGroup)) {
            return null;
        }
        List<Task> tasks = taskService.createTaskQuery().taskCandidateOrAssigned(assigneeOrGroup).list();
        return tasks;
    }

    /**
     * 查询历史任务
     * @param taskId
     * @return
     */
    public List<Map<String,Object>> getHistoryTask(String taskId){
        List<Map<String,Object>> list = new ArrayList<>();
        List<HistoricDetail> detailList = historyService.createHistoricDetailQuery().taskId(taskId).list();
        for(HistoricDetail historyDetail:detailList){
             Map<String,Object> map = new HashMap<>();
             map.put("id",historyDetail.getId());
             map.put("executionId",historyDetail.getExecutionId());
             map.put("processInstanceId",historyDetail.getProcessInstanceId());
             map.put("taskId",historyDetail.getTaskId());
             list.add(map);
        }
        return list;
    }

    /**
     * 任务取消（审批不通过）
     * @param taskId
     */
    public void cancelTask(String taskId){
        if (StringUtils.isEmpty(taskId)) {
            logger.error("taskId cannot be empty");
            return;
        }
        taskService.deleteTask(taskId);
    }

    /**
     * 完成任务（审批通过）
     * @param taskId
     */
    public void completeTask(String taskId) {
        if (StringUtils.isEmpty(taskId)) {
            logger.error("taskId cannot be empty");
            return;
        }
        taskService.complete(taskId);
    }



}
