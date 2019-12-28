package com.jian.active.controller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jian.active.config.Config;
import com.jian.active.entity.Active;
import com.jian.active.service.ActiveService;
import com.jian.active.util.RedisUtils;
import com.jian.annotation.API;
import com.jian.tools.core.DateTools;
import com.jian.tools.core.JsonTools;
import com.jian.tools.core.MapTools;
import com.jian.tools.core.ResultKey;
import com.jian.tools.core.ResultTools;
import com.jian.tools.core.Tips;
import com.jian.tools.core.Tools;
import com.jian.tools.core.cache.CacheObject;

@Controller
@RequestMapping("/api/active")
@API(name="活动记录表")
public class ActiveController extends BaseController<Active> {

	@Autowired
	private ActiveService service;
	@Autowired
	private Config config;
	
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
		Map<String, Object> vMap = null;
		//登录
		vMap = verifyLogin(req);
		if(vMap != null){
			return JsonTools.toJsonString(vMap);
		}
		//sign
		vMap = verifySign(req);
		if(vMap != null){
			return JsonTools.toJsonString(vMap);
		}
		//权限
		vMap = verifyAuth(req);
		if(vMap != null){
			return JsonTools.toJsonString(vMap);
		}
		
		//参数
		String page = Tools.getReqParamSafe(req, "page");
		String rows = Tools.getReqParamSafe(req, "rows");
		String startTime = Tools.getReqParamSafe(req, "start");
		String endTime = Tools.getReqParamSafe(req, "end");
		vMap = Tools.verifyParam("page", page, 0, 0, true);
		if(vMap != null){
			return JsonTools.toJsonString(vMap);
		}
		vMap = Tools.verifyParam("rows", rows, 0, 0, true);
		if(vMap != null){
			return JsonTools.toJsonString(vMap);
		}
		int start = Tools.parseInt(page) <= 1 ? 0 : (Tools.parseInt(page) - 1) * Tools.parseInt(rows);
		//参数
		Map<String, Object> condition = Tools.getReqParamsToMap(req, Active.class);
		String wsql = " 1=1 ";
		for (String key : condition.keySet()) {
			wsql += " and "+key+" = :"+key;
		}
		if(!Tools.isNullOrEmpty(startTime)) {
			wsql += " and date >= :startTime";
			condition.put("startTime", startTime);
		}
		if(!Tools.isNullOrEmpty(endTime)) {
			wsql += " and date <= :endTime";
			condition.put("endTime", endTime);
		}
		
		
		List<Active> list = service.getDao().findList(wsql, condition, start, Tools.parseInt(rows));
		long total = service.getDao().size(wsql, condition);
        return ResultTools.custom(Tips.ERROR1).put(ResultKey.TOTAL, total).put(ResultKey.DATA, list).toJSONString();
	}

	
	
	//TODO 自定义方法
	

	@RequestMapping("/save")
    @ResponseBody
	@API(name="参与活动", info="需微信登录认证")
	public String save(HttpServletRequest req) {
		Map<String, Object> vMap = null;
		
		//参数
		String openid = Tools.getReqParamSafe(req, "openid");
		String type = Tools.getReqParamSafe(req, "type");
		String info = Tools.getReqParamSafe(req, "info");
		vMap = Tools.verifyParam("openid", openid, 0, 0);
		if(vMap != null){
			return JsonTools.toJsonString(vMap);
		}
		vMap = Tools.verifyParam("type", type, 0, 0, true);
		if(vMap != null){
			return JsonTools.toJsonString(vMap);
		}
		
		//检查openid是否有效
		CacheObject cobj = RedisUtils.getCacheObj(openid);
		if(cobj == null) {
			return ResultTools.custom(Tips.ERROR200, "openid").toJSONString();
		}
		String access_token = cobj.getValue() + "";
		if(Tools.isNullOrEmpty(access_token)) {
			return ResultTools.custom(Tips.ERROR200, "openid").toJSONString();
		}
		/*String checkTokenUrl = config.wx_check_token_url
				.replace("", "")
				.replace("", "");*/
		
		//检查是否已参与过活动
		Active test = service.findOne(MapTools.custom().put("other", openid).put("type", type).build());
		if(test != null) {
			return ResultTools.custom(Tips.ERROR303).put(ResultKey.MSG, "已点赞").toJSONString();
		}
		
		//保存
		Active obj = new Active();
		obj.setDate(DateTools.formatDate());
		obj.setOther(openid);
		obj.setType(Tools.parseInt(type));
		obj.setIp(Tools.getIp(req));
		obj.setInfo(info == null ? "" : info);
		
		int res = service.add(obj);
		if(res > 0) {
			long count = service.size(MapTools.custom().put("type", type).build());
			return ResultTools.custom(Tips.ERROR1).put(ResultKey.DATA, count).toJSONString();
		}else {
			return ResultTools.custom(Tips.ERROR0).toJSONString();
		}
	}
	

	@RequestMapping("/count")
    @ResponseBody
	@API(name="参与活动数量", info="")
	public String count(HttpServletRequest req) {

		Map<String, Object> vMap = null;
		
		//参数
		String type = Tools.getReqParamSafe(req, "type");
		vMap = Tools.verifyParam("type", type, 0, 0, true);
		if(vMap != null){
			return JsonTools.toJsonString(vMap);
		}
		
		long res = service.size(MapTools.custom().put("type", type).build());
		
		return ResultTools.custom(Tips.ERROR1).put(ResultKey.DATA, res).toJSONString();
	}
	

	@RequestMapping("/list")
    @ResponseBody
	@API(name="参与活动记录", info="")
	public String list(HttpServletRequest req) {

		Map<String, Object> vMap = null;
		
		//参数
		String openid = Tools.getReqParamSafe(req, "openid");
		vMap = Tools.verifyParam("openid", openid, 0, 0);
		if(vMap != null){
			return JsonTools.toJsonString(vMap);
		}
		
		List<Active> res = service.findList(MapTools.custom().put("other", openid).build());
		
		return ResultTools.custom(Tips.ERROR1).put(ResultKey.DATA, res).toJSONString();
	}
	
	@RequestMapping("/excel")
    @ResponseBody
	public String excel(HttpServletRequest req, HttpServletResponse resp) {
		
		Map<String, Object> vMap = null;
		//登录
		vMap = verifyLogin(req);
		if(vMap != null){
			return JsonTools.toJsonString(vMap);
		}
		//sign
		vMap = verifySign(req);
		if(vMap != null){
			return JsonTools.toJsonString(vMap);
		}
		//权限
		vMap = verifyAuth(req);
		if(vMap != null){
			return JsonTools.toJsonString(vMap);
		}

		String wsql = " 1=1 ";
		String start = Tools.getReqParamSafe(req, "start");
		String end = Tools.getReqParamSafe(req, "end");
		//查询
		List<Active> list = null;
		Map<String, Object> condition = Tools.getReqParamsToMap(req, Active.class);
		for (String key : condition.keySet()) {
			wsql += " and "+key+" = :"+key;
		}
		if(!Tools.isNullOrEmpty(start)) {
			wsql += " and date >= :start";
			condition.put("start", start);
		}
		if(!Tools.isNullOrEmpty(end)) {
			wsql += " and date <= :end";
			condition.put("end", end);
		}
		if(condition == null || condition.isEmpty()){
			list = service.findAll();
		}else {
			list = service.getDao().findList(wsql, condition);
		}

		//执行
		resp.addHeader("Content-Disposition","attachment;filename=active.xls");
		resp.setContentType("application/vnd.ms-excel;charset=utf-8");
		try {
			OutputStream toClient = new BufferedOutputStream(resp.getOutputStream());
			//实例化HSSFWorkbook
            HSSFWorkbook workbook = new HSSFWorkbook();
            //创建一个Excel表单，参数为sheet的名字
            HSSFSheet sheet = workbook.createSheet("sheet");

			//设置表头
			String head = "Pid,事件序号,日期,IP,openid,其他信息";
			String[] heads = head.split(",");
            HSSFRow row = sheet.createRow(0);
            //设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度
            for (int i = 0; i <= heads.length; i++) {
            	sheet.setColumnWidth(i, (int)(( 15 + 0.72) * 256)); // 15 在EXCEL文档中实际列宽为14.29
            }
            //设置为居中加粗,格式化时间格式
            HSSFCellStyle style = workbook.createCellStyle();
            HSSFFont font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            style.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy/MM/dd HH:mm:ss"));
            //创建表头名称
            HSSFCell cell;
            for (int j = 0; j < heads.length; j++) {
                cell = row.createCell(j);
                cell.setCellValue(heads[j]);
                cell.setCellStyle(style);
            }
			//遍历导出数据
			for (int i = 0; i < list.size(); i++) {
				Active node = list.get(i);

				HSSFRow rowc = sheet.createRow(i+1);
				rowc.createCell(0).setCellValue(node.getPid()+"");
				rowc.createCell(1).setCellValue(node.getType());
				rowc.createCell(2).setCellValue(node.getDate());
				rowc.createCell(3).setCellValue(node.getIp());
				rowc.createCell(4).setCellValue(node.getOther());
				rowc.createCell(5).setCellValue(node.getInfo());
			}
			workbook.write(toClient);
			workbook.close();
			toClient.flush();
			toClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
