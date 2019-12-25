package com.jian.active.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jian.active.entity.Active;
import com.jian.active.dao.ActiveDao;
import com.jian.active.service.ActiveService;

/**
 * @author liujian
 * @Date  
 */
@Service
public class ActiveServiceImpl extends BaseServiceImpl<Active> implements ActiveService {

	@Autowired
	private ActiveDao dao;
	
	@Override
	public void initDao() {
		super.baseDao = dao;
	}

}
