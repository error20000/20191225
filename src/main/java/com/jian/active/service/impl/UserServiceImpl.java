package com.jian.active.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jian.active.config.Config;
import com.jian.active.dao.UserDao;
import com.jian.active.entity.User;
import com.jian.active.exception.ServiceException;
import com.jian.active.service.UserService;
import com.jian.tools.core.MapTools;
import com.jian.tools.core.Tips;
import com.jian.tools.core.Tools;

/**
 * @author liujian
 * @Date  
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

	@Autowired
	private UserDao dao;
	@Autowired
	private Config config;
	
	@Override
	public void initDao() {
		super.baseDao = dao;
	}
	
	public int changePWD(String oldPwd, String newPwd, String pid) {
		
		Map<String, Object> condition = MapTools.custom().put("pid", pid).build();
		User test = dao.findObject(condition);
		//判断用户
		if(test == null){
			throw new ServiceException(Tips.ERROR102, "用户不存在。");
		}
		//判断密码
		if(!test.getPassword().equals(Tools.md5(oldPwd))){
			throw new ServiceException(Tips.ERROR102, "原密码不正确。");
		}
		//检查密码复杂度
		if(!checkPWD(newPwd)) {
			throw new ServiceException(Tips.ERROR102, "新密码不符合要求。" + config.pwdRegStr);
		}
		Map<String, Object> value = MapTools.custom().put("password", Tools.md5(newPwd)).build();
		return dao.modify(value, condition);
	}
	


	public boolean checkPWD(String pwd) {
		return pwd.matches(config.pwdReg);
	}

}
