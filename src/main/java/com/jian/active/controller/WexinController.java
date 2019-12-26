package com.jian.active.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jian.active.config.Config;
import com.jian.active.util.RedisUtils;
import com.jian.tools.core.HttpTools;
import com.jian.tools.core.JsonTools;
import com.jian.tools.core.ResultKey;
import com.jian.tools.core.ResultTools;
import com.jian.tools.core.Tips;
import com.jian.tools.core.Tools;

@Controller
@RequestMapping("/api/wx")
public class WexinController {

	@Autowired
	private Config config;
	
	

	@RequestMapping("/login")
    @ResponseBody
	public String login(HttpServletRequest req, HttpServletResponse resp) {
		
		//参数
		String redirect_url = Tools.getReqParamSafe(req, "redirect_url");

		try {
			String reqUrl = req.getRequestURL().toString();	
			reqUrl = reqUrl.split("/api/")[0];
			String loginUrl = config.wx_code_url
					.replace("APPID", config.wx_appId)
					.replace("SCOPE", "snsapi_base")
					.replace("REDIRECT_URI", URLEncoder.encode(reqUrl + "/api/wx/callback"+(Tools.isNullOrEmpty(redirect_url) ? "" : "?redirect_url=" + URLEncoder.encode(redirect_url, "UTF-8")), "UTF-8"))
					.replace("STATE", "active");
			
			resp.sendRedirect(loginUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@RequestMapping("/callback")
    @ResponseBody
	public String callback(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> vMap = null;
		
		//参数
		String code = Tools.getReqParam(req, "code");
		String state = Tools.getReqParam(req, "state");
		String redirect_url = Tools.getReqParam(req, "redirect_url");
		vMap = Tools.verifyParam("code", code, 0, 0);
		if(vMap != null){
			return ResultTools.custom(Tips.ERROR206, "code").toJSONString();
		}
		vMap = Tools.verifyParam("state", state, 0, 0);
		if(vMap != null){
			return ResultTools.custom(Tips.ERROR206, "state").toJSONString();
		}

		//获取access_token  1万/分钟
		String access_token = "";
		String openid = "";
		String tokenUrl = config.wx_token_url
				.replace("APPID", config.wx_appId)
				.replace("SECRET", config.wx_appsecret)
				.replace("CODE", code);
		String tokenRes = HttpTools.getInstance().sendHttpGet(tokenUrl);
		Map<String, Object> map = JsonTools.jsonToMap(tokenRes, String.class, Object.class);
		if(map.get("errcode") == null){
			access_token = map.get("access_token") + "";
			openid = map.get("openid") + "";
			//缓存
			RedisUtils.setCacheObj(openid, access_token);
		}
		if(Tools.isNullOrEmpty(access_token)){
			return ResultTools.custom(Tips.ERROR108, "access_token").toJSONString();
		}
		if(Tools.isNullOrEmpty(openid)){
			return ResultTools.custom(Tips.ERROR108, "openid").toJSONString();
		}

		//获取结果
		if(Tools.isNullOrEmpty(redirect_url)){
			Map<String, String> data = new HashMap<String, String>();
			data.put("openid", openid);
			return ResultTools.custom(Tips.ERROR1).put(ResultKey.DATA, data).toJSONString();
		}else{
			//自动重定向
			try {
				resp.sendRedirect(redirect_url+"?openid="+openid);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return ResultTools.custom(Tips.ERROR1).toJSONString();
		}
	}
	
	
}
