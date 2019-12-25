package com.jian.active.service;

import com.jian.active.entity.User;


/**
 * @author liujian
 * @Date  
 */
public interface UserService extends BaseService<User> {
	
	public int changePWD(String oldPwd, String newPwd, String pid);

}
