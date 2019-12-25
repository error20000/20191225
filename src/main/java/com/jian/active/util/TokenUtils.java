package com.jian.active.util;


import java.util.Base64;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.jian.active.config.Config;
import com.jian.active.config.RedisCacheKey;
import com.jian.active.entity.User;
import com.jian.active.exception.ServiceException;
import com.jian.tools.core.JsonTools;
import com.jian.tools.core.Tips;
import com.jian.tools.core.Tools;
import com.jian.tools.core.cache.CacheObject;


@Component
public class TokenUtils {
	
	public static Config config = null;
	public static RedisCacheKey cacheKey = null;
	
	@Autowired
	public void setConfig(Config config, RedisCacheKey cacheKey){
		TokenUtils.config = config;
		TokenUtils.cacheKey = cacheKey;
	}
	
	public static String getLoginToken(){
		HttpServletRequest req =  ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		return getLoginToken(req);
	}

	public static String getLoginToken(HttpServletRequest req){
		//读取cookie
		String tokenStr = "";
		Cookie[] cookies = req.getCookies();
		if(cookies != null){
			for (int i = 0; i < cookies.length; i++) {
				Cookie tmp = cookies[i];
				if("token".equals(tmp.getName())){
					tokenStr = tmp.getValue();
					break;
				}
			}
		}
		//Authorization
		if(Tools.isNullOrEmpty(tokenStr)){
			tokenStr = Tools.isNullOrEmpty(req.getHeader("Authorization")) ? "" : Base64.getDecoder().decode(req.getHeader("Authorization").replace("Basic ", "")).toString();
		}
		//header parameter
		if(Tools.isNullOrEmpty(tokenStr)){
			tokenStr = Tools.isNullOrEmpty(req.getHeader("token")) ? Tools.getReqParamSafe(req, "token") : req.getHeader("token");
		}
		return tokenStr;
	}
	
	public static boolean checkLoginToken(String tokenStr){
		if(Tools.isNullOrEmpty(tokenStr)) {
			return false;
		}
		String token = getToken(tokenStr);
		String userId = getUserId(tokenStr);
		long time = getTokenTime(tokenStr);
		long expire = getTokenExpire(tokenStr);
		String str = userId + "." + time + "."  + expire;
		return token.equals(Tools.md5(str + config.tokenSecretKey));
	}
	
	public static String getToken(String tokenStr){
		String token = "";
		if(!Tools.isNullOrEmpty(tokenStr) && tokenStr.split("[.]").length >= 1){
			token = tokenStr.split("[.]")[0];
		}
		return token;
	}
	
	public static String getUserId(String tokenStr){
		String userId = "";
		if(!Tools.isNullOrEmpty(tokenStr) && tokenStr.split("[.]").length >= 2){
			userId = tokenStr.split("[.]")[1];
		}
		return userId;
	}
	
	public static long getTokenTime(String tokenStr){
		long time = 0;
		if(!Tools.isNullOrEmpty(tokenStr) && tokenStr.split("[.]").length >= 3){
			time = Long.parseLong(tokenStr.split("[.]")[2]);
		}
		return time;
	}
	
	public static long getTokenExpire(String tokenStr){
		long expire = 0;
		if(!Tools.isNullOrEmpty(tokenStr) && tokenStr.split("[.]").length >= 4){
			expire = Long.parseLong(tokenStr.split("[.]")[3]);
		}
		return expire;
	}
	
	
	public static User getLoginUser(String tokenStr){
		
		if(!TokenUtils.checkLoginToken(tokenStr)) {
			throw new ServiceException(Tips.ERROR213, "token");
		}
		String userId = TokenUtils.getUserId(tokenStr);
		String pkey = cacheKey.userLoginOnPc + userId;
		CacheObject test = RedisUtils.getCacheObj(pkey);
		if(test == null ) {
			throw new ServiceException(Tips.ERROR111);
		}
		User user = JsonTools.jsonToObj((String)test.getValue(), User.class);
		if(user == null ) {
			throw new ServiceException(Tips.ERROR111);
		}
		return user;
	}
	
	public static User getAppLoginUser(String tokenStr){
		
		if(!TokenUtils.checkLoginToken(tokenStr)) {
			throw new ServiceException(Tips.ERROR213, "token");
		}
		String userId = TokenUtils.getUserId(tokenStr);
		String pkey = cacheKey.userLoginOnMobile + userId;
		CacheObject test = RedisUtils.getCacheObj(pkey);
		if(test == null ) {
			throw new ServiceException(Tips.ERROR111);
		}
		User user = JsonTools.jsonToObj((String)test.getValue(), User.class);
		if(user == null ) {
			throw new ServiceException(Tips.ERROR111);
		}
		return user;
	}
	
}
