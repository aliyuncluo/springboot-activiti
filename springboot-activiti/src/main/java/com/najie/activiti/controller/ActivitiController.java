package com.najie.activiti.controller;

import com.najie.activiti.service.ActivitiService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/activiti")
public class ActivitiController {
    @Autowired
    private ActivitiService activitiService;

    @GetMapping("/startProcess")
    public Map<String,Object> startProcessInstance(@RequestParam("processDefinitionKey") String processDefinitionKey){
        Map<String, Object> map = activitiService.startProcessInstanceByKey(processDefinitionKey);
        return map;
    }


}
