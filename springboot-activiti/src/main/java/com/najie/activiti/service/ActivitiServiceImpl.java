package com.najie.activiti.service;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
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

import com.najie.activiti.util.DateUtils;

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
        Deployment deployment = repositoryService.createDeployment().addClasspathResource(PROCESS_FILE).name("离职申请流程").deploy();
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
    public Map<String,Object> startProcessInstanceByKey(String processDefinitionKey,Map<String, Object> variables) {
        Map<String,Object> map = new HashMap<>();
        if (StringUtils.isEmpty(processDefinitionKey)) {
            return null;
        }
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey,variables);
        logger.info("流程实例启动成功");
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

    /**
     * 取消流程实例
     * @return
     */
    public void cancelProcessInstance(String processInstanceId){
        
        runtimeService.deleteProcessInstance(processInstanceId,null);
        //runtimeService.suspendProcessInstanceById(processInstanceId);
        logger.info("流程实例["+processInstanceId+"]已经删除");
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
    
    /**
     * 查询执行流程的信息
     * @return
     */
    public List<Execution> getExecutionInfo(){
    	return runtimeService.createExecutionQuery().list();
    	
    }

    /**
     * 设置流程变量(全局变量)
     * @param taskId
     * @param params
     * @return
     */
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
     * 设置流程变量(局部变量)
     * @param taskId
     * @param params
     * @return
     */
    public Map<String,Object> setActivitiVariableLocal(String taskId,Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        try {
            taskService.setVariablesLocal(taskId,params);
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
    public Map<String,Object> getTaskByInstanceId(String processInstanceId){
        if(StringUtils.isEmpty(processInstanceId)){
            return null;
        }
        Map<String,Object> result = new HashMap<>();
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if(task!=null){
            result.put("taskId",task.getId());
            result.put("taskName",task.getName());
            result.put("createTime", DateUtils.formatDate(task.getCreateTime()));
            result.put("taskOwner",task.getOwner());
            result.put("taskAssignee",task.getAssignee());
            result.put("taskCategory",task.getCategory());
            result.put("delegationState",task.getDelegationState());
            result.put("suspended",task.isSuspended());
            result.put("processInstanceId",processInstanceId);
        }
        return result;
    }

    /**
     * @desc 根据流程实例获取并行任务（针对并行网关）
     * @param processInstanceId
     * @return
     */
    public List<Map<String,Object>> getParallelTaskByInstanceId(String processInstanceId){
    	List<Map<String,Object>> list = new ArrayList<>();
    	if(StringUtils.isEmpty(processInstanceId)){
            return null;
        }
    	List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
    	if(taskList.size()>0 && taskList!=null) {
    		for(Task task:taskList) {
    			Map<String,Object> result = new HashMap<>();
	            result.put("taskId",task.getId());
	            result.put("taskName",task.getName());
	            result.put("taskOwner",task.getOwner());
	            result.put("createTime", DateUtils.formatDate(task.getCreateTime()));
	            result.put("taskAssignee",task.getAssignee());
	            result.put("taskCategory",task.getCategory());
	            result.put("delegationState",task.getDelegationState());
	            result.put("suspended",task.isSuspended());
	            result.put("processInstanceId", task.getProcessInstanceId());
    	        list.add(result);
    		}
    	}
    	return list;
    }
    /**
     * 根据任务ID查询任务信息
     * @param taskId
     * @return
     */
    public Map<String,Object> getTaskById(String taskId){
        Map<String,Object> result = new HashMap<>();
        if (StringUtils.isEmpty(taskId)) {
            return null;
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task!=null) {
        	result.put("taskId",task.getId());
            result.put("taskName",task.getName());
            result.put("assignee",task.getAssignee());
            result.put("owner",task.getOwner());
            result.put("createTime", DateUtils.formatDate(task.getCreateTime()));
            result.put("executionId",task.getExecutionId());
            result.put("processInstanceId",task.getProcessInstanceId());
            result.put("suspended",task.isSuspended());
        }
        
        return result;
    }
    
    /**
     * 根据任务名称查询任务信息
     * @param taskName
     * @return
     */
    public Map<String,Object> getTaskByName(String taskName){
    	Map<String,Object> result = new HashMap<>();
    	if (StringUtils.isEmpty(taskName)) {
            return null;
        }
    	Task task = taskService.createTaskQuery().taskName(taskName).singleResult();
    	if(task!=null) {
    		result.put("taskId",task.getId());
            result.put("taskName",task.getName());
            result.put("assignee",task.getAssignee());
            result.put("owner",task.getOwner());
            result.put("createTime", DateUtils.formatDate(task.getCreateTime()));
            result.put("executionId",task.getExecutionId());
            result.put("processInstanceId",task.getProcessInstanceId());
            result.put("suspended",task.isSuspended());
    	}
        return result;
    }

    /**
     * 根据候选人查询任务
     * @param candidate
     * @return
     */
    public List<Map<String,Object>> getTaskByCandidateUser(String candidateUser){
    	List<Map<String,Object>> list = new ArrayList<>();
    	if (StringUtils.isEmpty(candidateUser)) {
            return null;
        }
        List<Task> taskList = taskService.createTaskQuery().taskCandidateUser(candidateUser).orderByTaskCreateTime().desc().list();
        if(taskList!=null && taskList.size()>0) {
        	for(Task task:taskList) {
        		Map<String,Object> result = new HashMap<>();
        		result.put("taskId",task.getId());
                result.put("taskName",task.getName());
                result.put("assignee",task.getAssignee());
                result.put("owner",task.getOwner());
                result.put("createTime", DateUtils.formatDate(task.getCreateTime()));
                result.put("executionId",task.getExecutionId());
                result.put("processInstanceId",task.getProcessInstanceId());
                result.put("suspended",task.isSuspended());
                list.add(result);
        	}
        }
        return list;
    }
    /**
     * 根据指派人查询当前任务
     * @param assignee
     * @return
     */
    public List<Map<String,Object>> getTasksByAssignee(String assignee) {
    	List<Map<String,Object>> list = new ArrayList<>();
    	if (StringUtils.isEmpty(assignee)) {
            return null;
        }
        List<Task> taskList = taskService.createTaskQuery().taskAssignee(assignee).list();
        if(taskList!=null && taskList.size()>0) {
        	for(Task task:taskList){
                Map<String,Object> map = new HashMap<>();
            	map.put("processInstanceId", task.getProcessInstanceId());
            	map.put("taskId", task.getId());
                map.put("taskName", task.getName());
                map.put("assignee", task.getAssignee());
                map.put("createTime", DateUtils.formatDate(task.getCreateTime()));
                map.put("executionId", task.getExecutionId());
                map.put("suspended",task.isSuspended());
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 根据所在组查询任务
     * @param group
     * @return
     */
    public List<Map<String,Object>> getTasksByGroup(String group) {
        List<Map<String,Object>> list = new ArrayList<>();
    	if (StringUtils.isEmpty(group)) {
            return null;
        }
        List<Task> taskList = taskService.createTaskQuery().taskCandidateGroup(group).list();
        if(taskList!=null && taskList.size()>0) {
        	for(Task task:taskList) {
        		Map<String,Object> map = new HashMap<>();
            	map.put("processInstanceId", task.getProcessInstanceId());
            	map.put("taskId", task.getId());
                map.put("taskName", task.getName());
                map.put("assignee", task.getAssignee());
                map.put("createTime", DateUtils.formatDate(task.getCreateTime()));
                map.put("executionId", task.getExecutionId());
                map.put("suspended",task.isSuspended());
                list.add(map);
        	}
        }
        return list;
    }

    /**
     * 根据指派人或组来查询任务
     * @param assigneeOrGroup
     * @return
     */
    public List<Map<String,Object>> getTasksByAssigneeOrGroup(String assigneeOrGroup) {
    	List<Map<String,Object>> list = new ArrayList<>();
    	if (StringUtils.isEmpty(assigneeOrGroup)) {
            return null;
        }
        List<Task> taskList = taskService.createTaskQuery().taskCandidateOrAssigned(assigneeOrGroup).list();
        if(taskList!=null && taskList.size()>0) {
        	for(Task task:taskList) {
        		Map<String,Object> map = new HashMap<>();
            	map.put("processInstanceId", task.getProcessInstanceId());
            	map.put("taskId", task.getId());
                map.put("taskName", task.getName());
                map.put("assignee", task.getAssignee());
                map.put("createTime", DateUtils.formatDate(task.getCreateTime()));
                map.put("executionId", task.getExecutionId());
                map.put("suspended",task.isSuspended());
                list.add(map);
        	}
        }
        return list;
    }

    /**
     * 查询全局历史任务
     * @param processInstanceId
     * @return
     */
    public List<Map<String,Object>> getHistoryGlobalTask(String processInstanceId){
        List<Map<String,Object>> list = new ArrayList<>();
        List<HistoricTaskInstance> historicTaskInstanceList  = historyService.createHistoricTaskInstanceQuery().includeProcessVariables().processInstanceId(processInstanceId).orderByTaskCreateTime().asc().list();
        logger.info("historicTaskInstanceList size:"+historicTaskInstanceList.size());
        for(HistoricTaskInstance historicTaskInstance:historicTaskInstanceList ){
             Map<String,Object> map = new HashMap<>();
             map.put("taskId",historicTaskInstance.getId()); //taskId 活动ID
             map.put("taskName",historicTaskInstance.getName());
             map.put("startTime",DateUtils.formatDate(historicTaskInstance.getStartTime()));
             map.put("endTime",DateUtils.formatDate(historicTaskInstance.getEndTime()));
             map.put("assignee",historicTaskInstance.getAssignee());
             map.put("category",historicTaskInstance.getCategory());
             map.put("executionId",historicTaskInstance.getExecutionId());
             map.put("claimTime",historicTaskInstance.getClaimTime());
             map.put("description",historicTaskInstance.getDescription());
             map.put("owner",historicTaskInstance.getOwner());
             Map<String, Object> processVariables = historicTaskInstance.getProcessVariables();
             logger.info("processVariables:"+processVariables);
             map.putAll(processVariables);
             list.add(map);
        }
        return list;
    }

    /**
     * 查询历史任务（流程局部变量）
     * @param processInstanceId
     * @return
     */
    public List<Map<String,Object>> getHistoryLocalTask(String processInstanceId){
        List<Map<String,Object>> list = new ArrayList<>();
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().includeTaskLocalVariables().processInstanceId(processInstanceId).orderByTaskCreateTime().asc().list();
        logger.info("historicTaskInstanceList size:"+historicTaskInstanceList.size());
        for(HistoricTaskInstance historicTaskInstance:historicTaskInstanceList ){
            Map<String,Object> map = new HashMap<>();
            map.put("taskId",historicTaskInstance.getId()); //taskId 活动ID
            map.put("taskName",historicTaskInstance.getName());
            map.put("startTime",DateUtils.formatDate(historicTaskInstance.getStartTime()));
            map.put("endTime",historicTaskInstance.getEndTime()==null?"":DateUtils.formatDate(historicTaskInstance.getEndTime()));
            map.put("assignee",historicTaskInstance.getAssignee());
            map.put("category",historicTaskInstance.getCategory());
            map.put("executionId",historicTaskInstance.getExecutionId());
            map.put("claimTime",historicTaskInstance.getClaimTime());
            map.put("description",historicTaskInstance.getDescription());
            map.put("owner",historicTaskInstance.getOwner());
            Map<String, Object> localVariables = historicTaskInstance.getTaskLocalVariables();
            logger.info("localVariables:"+localVariables);
            map.putAll(localVariables);
            list.add(map);
        }
        return list;
    }
    
    /**
     * @desc 领取任务（针对岗位）
     * @param taskId
     * @param userId
     */
    public void claimTask(String taskId,String userId) {
    	taskService.claim(taskId, userId);
    }
    /**
     * 任务取消
     * @param taskId
     */
    public void cancelTask(String taskId){
        if (StringUtils.isEmpty(taskId)) {
            logger.error("taskId cannot be empty");
            return;
        }
        taskService.deleteTask(taskId,true);
    }

    /**
     * 完成任务
     * @param taskId
     */
    public void completeLocalTask(String taskId,Map<String,Object> variables) {
        if (StringUtils.isEmpty(taskId)) {
            logger.error("taskId cannot be empty");
            return;
        }
        taskService.setVariablesLocal(taskId,variables);
        taskService.complete(taskId);
    }

    /**
     * 完成任务
     * @param taskId
     */
    public void completeGlobalTask(String taskId,Map<String,Object> variables) {
        if (StringUtils.isEmpty(taskId)) {
            logger.error("taskId cannot be empty");
            return;
        }
        if(variables!=null && variables.size()>0){
            taskService.complete(taskId,variables);
        }else{
            taskService.complete(taskId);
        }
    }

}
