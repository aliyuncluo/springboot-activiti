package com.najie.activiti.util;

public enum ResStatus {
     
	SUCCESS("10000","操作成功"),
	TASK_SUBMIT_FAIL("10001","任务提交失败"),
	CLAIM_TASK_FAIL("10002","领取任务失败"),
	CANCEL_PROCESS_FAIL("10003","取消流程失败"),
	SEARCH_TASK_FAIL("10004","查询任务失败"),
	START_PROCESS_FAIL("10005","启动流程失败");
	
	private String code;
	private String message;
	
	ResStatus(String code,String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
