package com.najie.activiti.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务监听器
 * @author admin
 *
 */
public class AssigneeListener implements TaskListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(AssigneeListener.class);
    @Override
    public void notify(DelegateTask delegateTask) {
    	 String eventName = delegateTask.getEventName();
    	 logger.info("eventName="+eventName);
         logger.info("===进入到Task监听===");
         String taskName = delegateTask.getName();
         logger.info("taskName:"+taskName);
         
    }
}
