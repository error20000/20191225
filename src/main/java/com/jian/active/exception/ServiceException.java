package com.jian.active.exception;

import com.jian.tools.core.ResultTools;
import com.jian.tools.core.Tips;

public class ServiceException extends RuntimeException{

	private static final long serialVersionUID = 1L;


	public ServiceException(Tips tips) {
		super(ResultTools.custom(tips).toJSONString());
	}
	
	public ServiceException(Tips tips, String params) {
		super(ResultTools.custom(tips, params).toJSONString());
	}
	
	public ServiceException(String msg){
		super(msg);
	}

}
