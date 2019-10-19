package com.najie.activiti;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.najie.activiti.exception.BizException;

@RestController
public class GlobalExceptionHandler {

	@ExceptionHandler({BizException.class})
	public Map<String,Object> handlerException(Throwable t){
		Map<String,Object> map = new HashMap<>();
		BizException e = (BizException)t;
		map.put("code", e.getCode());
		map.put("message", e.getMessage());
		return map;
	}
}
