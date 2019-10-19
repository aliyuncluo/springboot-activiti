package com.najie.activiti.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParallelListener implements ExecutionListener {
    private Logger logger = LoggerFactory.getLogger(ParallelListener.class);
	@Override
	public void notify(DelegateExecution execution) {
		logger.info("===进入到execution监听===");
		String eventName = execution.getEventName();
		logger.info("eventName:"+eventName);
	
	}

}
