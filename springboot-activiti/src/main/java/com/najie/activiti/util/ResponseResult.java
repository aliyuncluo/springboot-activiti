package com.najie.activiti.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseResult {

	public static ResponseResult newInstance() {
		return new ResponseResult();
	}
	
	public Map<String,Object> success(){
		Map<String,Object> map = new HashMap<>();
		map.put("code", ResStatus.SUCCESS.getCode());
		map.put("message", ResStatus.SUCCESS.getMessage());
		return map;
	}
	
	public Map<String,Object> success(Map<String,Object> data){
		Map<String,Object> map = new HashMap<>();
		map.put("code", ResStatus.SUCCESS.getCode());
		map.put("message", ResStatus.SUCCESS.getMessage());
		map.put("data", data);
		return map;
	}
	
	public Map<String,Object> success(List<Map<String,Object>> list){
		Map<String,Object> map = new HashMap<>();
		map.put("code", ResStatus.SUCCESS.getCode());
		map.put("message", ResStatus.SUCCESS.getMessage());
		map.put("data", list);
		return map;
	}
}
