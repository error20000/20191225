package com.jian.active.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jian.annotation.API;

import com.jian.active.entity.Active;
import com.jian.active.service.ActiveService;

@Controller
@RequestMapping("/api/active")
@API(name="活动记录表")
public class ActiveController extends BaseController<Active> {

	@Autowired
	private ActiveService service;
	
	@Override
	public void initService() {
		super.service = service;
	}
	
	//TODO 基本方法
	
	/**
	 * 
	 * @param req
	 * <p>请求参数说明：参数	必选	类型	描述
	 * <pre>
	 * type	否	int	分类
	 * ip	否	String	IP
	 * other	否	String	参与人
	 * info	否	String	其他信息
	 * </pre>
	 * @return resp
	 * <p>响应示例：
	 * <pre type="template">
	 * {
	 * 	"code": 1,
	 * 	"msg": "成功",
	 * 	"data": 1
	 * }
	 * </pre>
	 * <p>参数说明：参数	描述
	 * <pre>
	 * code	错误码，大于0表示成功，小于 0 表示失败。[错误码说明](#错误码说明)
	 * msg	错误消息 ，code 小于 0 时的具体错误信息。
	 * data	自增id或受影响条数。
	 * </pre>
	 */
	@Override
	@RequestMapping("/add")
    @ResponseBody
	@API(name="新增", info="需登录认证")
	public String add(HttpServletRequest req) {
		return super.add(req);
	}

	/**
	 * 
	 * @param req
	 * <p>请求参数说明：参数	必选	类型	描述
	 * <pre>
	 * pid	是	int	自增主键
	 * type	否	int	分类
	 * ip	否	String	IP
	 * other	否	String	参与人
	 * info	否	String	其他信息
	 * </pre>
	 * @return resp
	 * <p>响应示例：
	 * <pre type="template">
	 * {
	 * 	"code": 1,
	 * 	"msg": "成功",
	 * 	"data": 1
	 * }
	 * </pre>
	 * <p>参数说明：参数	描述
	 * <pre>
	 * code	错误码，大于0表示成功，小于 0 表示失败。[错误码说明](#错误码说明)
	 * msg	错误消息 ，code 小于 0 时的具体错误信息。
	 * data	受影响条数。
	 * </pre>
	 */
	@Override
	@RequestMapping("/update")
    @ResponseBody
	@API(name="修改", info="需登录认证")
	public String update(HttpServletRequest req) {
		return super.update(req);
	}

	/**
	 * 
	 * @param req
	 * <p>请求参数说明：参数	必选	类型	描述
	 * <pre>
	 * pid	是	int	自增主键
	 * </pre>
	 * @return resp
	 * <p>响应示例：
	 * <pre type="template">
	 * {
	 * 	"code": 1,
	 * 	"msg": "成功",
	 * 	"data": 1
	 * }
	 * </pre>
	 * <p>参数说明：参数	描述
	 * <pre>
	 * code	错误码，大于0表示成功，小于 0 表示失败。[错误码说明](#错误码说明)
	 * msg	错误消息 ，code 小于 0 时的具体错误信息。
	 * data	受影响条数。
	 * </pre>
	 */
	@Override
	@RequestMapping("/delete")
    @ResponseBody
	@API(name="删除", info="需登录认证")
	public String delete(HttpServletRequest req) {
		return super.delete(req);
	}

	/**
	 * 
	 * @param req
	 * <p>请求参数说明：参数	必选	类型	描述
	 * <pre>
	 * page	是	int	页码，从1开始。
	 * rows	是	int	每页条数。
	 * pid	否	int	自增主键
	 * type	否	int	分类
	 * date	否	String	日期  yyyy-MM-dd HH:mm:ss
	 * ip	否	String	IP
	 * other	否	String	参与人
	 * info	否	String	其他信息
	 * </pre>
	 * @return resp
	 * <p>响应示例：
	 * <pre type="template">
	 * {
	 * 	"code": 1,
	 * 	"msg": "成功",
	 * 	"total": 0,
	 * 	"data": [{
	 * 		"pid":"",
	 * 		"type":"",
	 * 		"date":"",
	 * 		"ip":"",
	 * 		"other":"",
	 * 		"info":"",
	 * 	},...]
	 * }
	 * </pre>
	 * <p>参数说明：参数	描述
	 * <pre>
	 * code	错误码，大于0表示成功，小于 0 表示失败。[错误码说明](#错误码说明)
	 * msg	错误消息 ，code 小于 0 时的具体错误信息。
	 * total	总条数
	 * data	数据集。
	 * 数据字段
	 * pid	自增主键
	 * type	分类
	 * date	日期  yyyy-MM-dd HH:mm:ss
	 * ip	IP
	 * other	参与人
	 * info	其他信息
	 * </pre>
	 */
	@Override
	@RequestMapping("/findPage")
    @ResponseBody
	@API(name="分页查询", info="需登录认证")
	public String findPage(HttpServletRequest req) {
		return super.findPage(req);
	}

	/**
	 * 
	 * @param req
	 * <p>请求参数说明：参数	必选	类型	描述
	 * <pre>
	 * pid	否	int	自增主键
	 * type	否	int	分类
	 * date	否	String	日期  yyyy-MM-dd HH:mm:ss
	 * ip	否	String	IP
	 * other	否	String	参与人
	 * info	否	String	其他信息
	 * </pre>
	 * <b style="color:#004b91;">注意：</b>以上参数不可同时为空。
	 * @return resp
	 * <p>响应示例：
	 * <pre type="template">
	 * {
	 * 	"code": 1,
	 * 	"msg": "成功",
	 * 	"data": {
	 * 		"pid":"",
	 * 		"type":"",
	 * 		"date":"",
	 * 		"ip":"",
	 * 		"other":"",
	 * 		"info":"",
	 * 	}
	 * }
	 * </pre>
	 * <p>参数说明：参数	描述
	 * <pre>
	 * code	错误码，大于0表示成功，小于 0 表示失败。[错误码说明](#错误码说明)
	 * msg	错误消息 ，code 小于 0 时的具体错误信息。
	 * data	数据。
	 * 数据字段
	 * pid	自增主键
	 * type	分类
	 * date	日期  yyyy-MM-dd HH:mm:ss
	 * ip	IP
	 * other	参与人
	 * info	其他信息
	 * </pre>
	 */
	@Override
	@RequestMapping("/findOne")
    @ResponseBody
	@API(name="查询一个", info="需登录认证")
	public String findOne(HttpServletRequest req) {
		return super.findOne(req);
	}

	/**
	 * 
	 * @param req
	 * <p>请求参数说明：参数	必选	类型	描述
	 * <pre>
	 * pid	否	int	自增主键
	 * type	否	int	分类
	 * date	否	String	日期  yyyy-MM-dd HH:mm:ss
	 * ip	否	String	IP
	 * other	否	String	参与人
	 * info	否	String	其他信息
	 * </pre>
	 * <b style="color:#004b91;">注意：</b>以上参数不可同时为空。
	 * @return resp
	 * <p>响应示例：
	 * <pre type="template">
	 * {
	 * 	"code": 1,
	 * 	"msg": "成功",
	 * 	"data": [{
	 * 		"pid":"",
	 * 		"type":"",
	 * 		"date":"",
	 * 		"ip":"",
	 * 		"other":"",
	 * 		"info":"",
	 * 	},...]
	 * }
	 * </pre>
	 * <p>参数说明：参数	描述
	 * <pre>
	 * code	错误码，大于0表示成功，小于 0 表示失败。[错误码说明](#错误码说明)
	 * msg	错误消息 ，code 小于 0 时的具体错误信息。
	 * data	数据集。
	 * 数据字段
	 * pid	自增主键
	 * type	分类
	 * date	日期  yyyy-MM-dd HH:mm:ss
	 * ip	IP
	 * other	参与人
	 * info	其他信息
	 * </pre>
	 */
	@Override
	@RequestMapping("/findList")
    @ResponseBody
	@API(name="查询多个", info="需登录认证")
	public String findList(HttpServletRequest req) {
		return super.findList(req);
	}
	
	//TODO 自定义方法
	
}
