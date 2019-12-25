package com.jian.active.config;

import org.springframework.stereotype.Component;

@Component
public class RedisCacheKey {
	
	public String userAuthCacheKey = "user:auth:cache:key:"; //权限验证
	public String userLoginOnPc = "user:login:on:pc:"; //pc登录
	public String userLoginOnMobile = "user:login:on:mobile:"; //移动登录
	public String userLoginErrorPwdCount = "user:login:error:pwd:count:"; //登录密码错误次数限制
	public String userLoginErrorPwdTime = "user:login:error:pwd:time:"; //登录密码错误时长限制
	
}

