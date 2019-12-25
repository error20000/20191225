package com.jian.active.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jian.active.config.Config;
import com.jian.active.config.RedisCacheKey;
import com.jian.active.entity.User;
import com.jian.active.service.UserService;
import com.jian.active.util.RedisUtils;
import com.jian.active.util.TokenUtils;
import com.jian.tools.core.HttpTools;
import com.jian.tools.core.JsonTools;
import com.jian.tools.core.MapTools;
import com.jian.tools.core.ResultKey;
import com.jian.tools.core.ResultTools;
import com.jian.tools.core.Tips;
import com.jian.tools.core.Tools;
import com.jian.tools.core.cache.CacheObject;

@Controller
@RequestMapping("/api/wx")
public class WexinController {

	@Autowired
	private RedisCacheKey cacheKey;
	@Autowired
	private Config config;
	
	

	@RequestMapping("/code")
    @ResponseBody
	public String code(HttpServletRequest req) {
		Map<String, Object> vMap = null;
		
		//参数
		String username = Tools.getReqParamSafe(req, "username");
		String password = Tools.getReqParamSafe(req, "password");
		vMap = Tools.verifyParam("username", username, 0, 0);
		if(vMap != null){
			return ResultTools.custom(Tips.ERROR206, "username").toJSONString();
		}
		vMap = Tools.verifyParam("password", password, 0, 0);
		if(vMap != null){
			return ResultTools.custom(Tips.ERROR206, "password").toJSONString();
		}
		
		//检查
		User user = service.findOne(MapTools.custom().put("username", username).build());
		if(user == null){
			return ResultTools.custom(Tips.ERROR109).toJSONString();
		}
		if(user.getStatus() == 0){
			return ResultTools.custom(Tips.ERROR107, "账号").toJSONString();
		}
		//时长
		String tkey = cacheKey.userLoginErrorPwdTime + user.getPid();
		CacheObject tobj = RedisUtils.getCacheObj(tkey);
		if(tobj != null) {
			int m = (int)((tobj.getTimeOut() - System.currentTimeMillis()) / (60 * 1000));
			m = m <= 0 ? 1 : m;
			return ResultTools.custom(Tips.ERROR108).put(ResultKey.MSG, "账号登录失败，请" +m+"分钟后重试。").toJSONString();
		}
		//次数
		if(!user.getPassword().equals(Tools.md5(password))){
			//限制错误次数
			int loinTime = config.maxLoginCount - 1;
			String ckey = cacheKey.userLoginErrorPwdCount + user.getPid();
			CacheObject cobj = RedisUtils.getCacheObj(ckey);
			if(cobj == null){
				RedisUtils.setCacheObj(ckey, loinTime);
			}else{
				loinTime = Integer.parseInt(cobj.getValue()+"");
				loinTime--;
				if(loinTime <= 0){
					//时长限制
					RedisUtils.setCacheObj(tkey, 1, config.maxLoginTime); 
					RedisUtils.clearCacheObj(ckey); //解除禁用
				}else{
					RedisUtils.setCacheObj(ckey, loinTime);
				}
			}
			return ResultTools.custom(Tips.ERROR110).put(ResultKey.DATA, loinTime).toJSONString();
		}
		user.setPassword("");
		
		//保存
		String okey = cacheKey.userLoginOnPc + user.getPid();
		String tokenStr = newToken(user);
		RedisUtils.setCacheObj(okey, JsonTools.toJsonString(user), config.expireTime); 
		
		String url = config.wx_code_url
				.replace("", "")
		
		String res = HttpTools.getInstance().sendHttpGet(url);
		
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("token", tokenStr);
		res.put("user", user);
		return ResultTools.custom(Tips.ERROR1).put(ResultKey.DATA, res).toJSONString();
	}
	
	@RequestMapping("/callback")
    @ResponseBody
	public String callback(HttpServletRequest req) {
		//保存
		String tokenStr = TokenUtils.getLoginToken(req);
		
		if(!TokenUtils.checkLoginToken(tokenStr)) {
			return ResultTools.custom(Tips.ERROR213, "token").toJSONString();
		}

		String key = cacheKey.userLoginOnPc + TokenUtils.getUserId(tokenStr);
		RedisUtils.clearCacheObj(key);
		return ResultTools.custom(Tips.ERROR1).toJSONString();
	}
	

	@RequestMapping("/isLogin")
    @ResponseBody
	public String isLogin(HttpServletRequest req) {
		//保存
		String tokenStr = TokenUtils.getLoginToken(req);
		
		if(!TokenUtils.checkLoginToken(tokenStr)) {
			return ResultTools.custom(Tips.ERROR213, "token").toJSONString();
		}
		
		String key = cacheKey.userLoginOnPc + TokenUtils.getUserId(tokenStr);
		CacheObject test = RedisUtils.getCacheObj(key);
		if(test == null){
			return ResultTools.custom(Tips.ERROR0).toJSONString();
		}else {
			return ResultTools.custom(Tips.ERROR1).put(ResultKey.DATA, JsonTools.jsonToMap(String.valueOf(test.getValue())) ).toJSONString();
		}
	}
	

	@RequestMapping("/changePWD")
    @ResponseBody
	public String changePWD(HttpServletRequest req) {
		Map<String, Object> vMap = null;
		
		//参数
		String oldPwd = Tools.getReqParamSafe(req, "oldPwd");
		String newPwd = Tools.getReqParamSafe(req, "newPwd");
		vMap = Tools.verifyParam("oldPwd", oldPwd, 0, 0);
		if(vMap != null){
			return ResultTools.custom(Tips.ERROR206, "oldPwd").toJSONString();
		}
		vMap = Tools.verifyParam("newPwd", newPwd, 0, 0);
		if(vMap != null){
			return ResultTools.custom(Tips.ERROR206, "newPwd").toJSONString();
		}
		
		//保存
		int res = service.changePWD(oldPwd, newPwd, getLoginUser(req).getPid()+"");
		if(res > 0){
			return ResultTools.custom(Tips.ERROR1).toJSONString();
		}else{
			return ResultTools.custom(Tips.ERROR0).put(ResultKey.DATA, res).toJSONString();
		}
	}

	
	private String newToken(User user){
		return newToken(user, config.expireTime);
	}
	
	private String newToken(User user, int expireTime){
		long curTime = System.currentTimeMillis();
		String str = user.getPid() + "." + curTime + "."  + expireTime;
		String token = Tools.md5(str + config.tokenSecretKey); // userId + time + expire + key
		String tokenStr = token + "." + str;
		return tokenStr;
	}
	
	private User getLoginUser(HttpServletRequest req) {
		String tokenStr = TokenUtils.getLoginToken(req);
		return TokenUtils.getLoginUser(tokenStr);
	}
	
}
