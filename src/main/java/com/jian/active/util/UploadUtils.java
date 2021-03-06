package com.jian.active.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jian.active.App;
import com.jian.active.config.Config;
import com.jian.tools.core.DateTools;
import com.jian.tools.core.ResultKey;
import com.jian.tools.core.ResultTools;
import com.jian.tools.core.Tips;
import com.jian.tools.core.Tools;

/**
 * 上传工具类
 * 
 * @author Administrator
 *
 */
@Component
public class UploadUtils {

	private static String imgFiles = "bmp,gif,jpeg,jpg,png,svg";
	private static String docFiles = "csv,doc,docx,pdf,ppt,pptx,txt,xls,xlsx,md";
	private static String movFiles = "3gp,asf,avi,flv,f4v,mov,mkv,mp4,ogg,webm,wmv,ra,ram,rm,rmvb,swf,mp3,aac,wav,flac,wma";
	private static String allowFiles = imgFiles+","+docFiles+","+movFiles+","+"rar,zip,tar,gz,7z,psd,xml";
	
	public static Config baseConfig = null;
	
	@Autowired
	public void setConfig(Config config){
		UploadUtils.baseConfig = config;
		//上传文件类型可以通过配置文件配置
//		UploadUtils.imgFiles = config.upload_imgFiles;
//		UploadUtils.docFiles = config.upload_docFiles;
//		UploadUtils.movFiles = config.upload_movFiles;
//		UploadUtils.allowFiles = config.upload_allowFiles;
	}
	
	//--------------------------------------------------------------------------------------------------上传图片
	/**
	 * 图片上传。支持格式  {@link #imgFiles}
	 * @param fileName  文件名，如：test.jpg
	 * @param in	文件的输入流。
	 * @param outDir	文件输出的目录（需要“/”结尾）。
	 * @return json字符串，code=1，表示上传成功。
	 * @see #upload(String, InputStream, String, String)
	 */
	public static String uploadImg(String fileName, InputStream in, String outDir){
		return upload(fileName, in, outDir, imgFiles, false);
	}

	//--------------------------------------------------------------------------------------------------上传文档
	/**
	 * 文档上传。支持格式  {@link #docFiles}
	 * @param fileName  文件名，如：test.doc
	 * @param in	文件的输入流。
	 * @param outDir	文件输出的目录（需要“/”结尾）。
	 * @return json字符串，code=1，表示上传成功。
	 * @see #upload(String, InputStream, String, String)
	 */
	public static String uploadDoc(String fileName, InputStream in, String outDir){
		return upload(fileName, in, outDir, docFiles, false);
	}

	//--------------------------------------------------------------------------------------------------上传视频
	/**
	 * 文件上传。支持格式  {@link #movFiles}
	 * @param fileName  文件名，如：test.mp4
	 * @param in	文件的输入流。
	 * @param outDir	文件输出的目录（需要“/”结尾）。
	 * @return json字符串，code=1，表示上传成功。
	 * @see #upload(String, InputStream, String, String)
	 */
	public static String uploadMov(String fileName, InputStream in, String outDir){
		return upload(fileName, in, outDir, movFiles, false);
	}

	//--------------------------------------------------------------------------------------------------上传文件
	/**
	 * 文件上传。支持格式  {@link #allowFiles}
	 * @param fileName  文件名，如：test.rar
	 * @param in	文件的输入流。
	 * @param outDir	文件输出的目录（需要“/”结尾）。
	 * @return json字符串，code=1，表示上传成功。
	 * @see #upload(String, InputStream, String, String)
	 */
	public static String uploadFile(String fileName, InputStream in, String outDir){
		return upload(fileName, in, outDir, allowFiles, false);
	}
	
	
	
	//--------------------------------------------------------------------------------------------------基础上传
	/**
	 * 基础上传文件
	 * @param fileName  文件名，如：test.jpg
	 * @param in	文件的输入流。
	 * @param outDir	文件输出的目录（需要“/”结尾）。
	 * @param limit		文件类型限制，如：jpg,png,doc,txt...。多个逗号分隔。
	 * @param isOriginName	是否保留原文件名。true：表示保留，重名会累加，false：表示不保留，会重命名。
	 * @return json字符串，code=1，表示上传成功。
	 * @see #upload(String, InputStream, String, boolean)
	 */
	public static String upload(String fileName, InputStream in, String outDir, String limit, boolean isOriginName){
		//判断类型
		String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
		if(!(limit.toLowerCase()+",").contains(suffix.toLowerCase()+",")){
			return ResultTools.custom(Tips.ERROR212).toJSONString();
		}
		//上传
		Map<String, Object> res = upload(fileName, in, outDir, isOriginName);
		if(res == null || res.isEmpty()){
			return  ResultTools.custom(Tips.ERROR0).toJSONString();
		}
		return ResultTools.custom(Tips.ERROR1).put(ResultKey.DATA, res).toJSONString();
	}
	
	/**
	 * 基础上传文件。无文件类型限制
	 * @param fileName  文件名，如：test.txt
	 * @param in	文件的输入流。
	 * @param outDir	文件输出的目录（需要“/”结尾）。
	 * @param isOriginName	是否保留原文件名。true：表示保留，重名会累加，false：表示不保留，会重命名。
	 * @return	map 文件相关信息，如文件大小、后缀、上传名称、上传路径。
	 */
	public static Map<String, Object> upload(String fileName, InputStream in, String outDir, boolean isOriginName){
		
		//生成输出文件
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

		String basePath = Tools.isNullOrEmpty(baseConfig.out_static_path) ? App.rootPath + "static/" : baseConfig.out_static_path;
		basePath = basePath.endsWith("/") ? basePath : basePath + "/";
		String outFilePath = "upload/" + outDir + DateTools.formatDate("yyyyMMdd") + "/";
		
		String outFileName = "";
		if(isOriginName){
			outFileName = fileName.substring(0, fileName.lastIndexOf(".")) + "."+ suffix;
			File test = new File(basePath + outFilePath + outFileName);
			outFileName = rename(test, 0);
		}else{
			int random = new Random().nextInt(10000);
			String str = random+"";
			for (int i = 0; i <  4 - str.length(); i++) {
				str = "0" + str;
			}
			outFileName = DateTools.formatDate("yyyyMMddHHmmssSSS") + str + "."+ suffix;
		}
		
		outFilePath = outFilePath + outFileName;
		
		
		//上传
		Map<String, Object> res = null;
		try {
			File outFile = new File(basePath + outFilePath);
			File pfile = outFile.getParentFile();
			if(!pfile.exists()){
				pfile.mkdirs();
			}
			OutputStream out = new FileOutputStream(outFile);
			byte[] buffer = new byte[1024];
			long size = 0;
			int count = -1;
			while ((count = in.read(buffer)) != -1) {
				out.write(buffer, 0, count);
				size += count;
			}
			in.close();
			out.close();
			out.flush();
			//返回结果
			res = new HashMap<>();
			res.put("type", suffix);
			res.put("size", size);
			res.put("name", outFileName);
			res.put("path", outFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	//递归
	/*private static String rename(File file, int index){
		if(file.exists()){
			index = index + 1;
			String fname = file.getName();
			String temp = fname.substring(0, fname.lastIndexOf(".")).split("\\(")[0]+"("+index+")" + fname.substring(fname.lastIndexOf("."));
			File test = new File(file.getParent() + "/" + temp);
			return rename(test, index);
		}else{
			return file.getName();
		}
	}*/
	
	//查询
	private static String rename(File file, int index){
		String fname = file.getName();
		String tname = fname.substring(0, fname.lastIndexOf(".")).toLowerCase();
		String suffix = fname.substring(fname.lastIndexOf(".") + 1).toLowerCase();
		File parent = file.getParentFile();
		String[] str = parent.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				name = name.toLowerCase();
				return name.startsWith(tname) && name.endsWith(suffix);
			}
		});
		for (String string : str) {
			string = string.toLowerCase();
			if(string.startsWith(tname) && string.endsWith(suffix)){
				//查找最大值
				String[] tstr = string.substring(0, string.lastIndexOf(".")).replace(tname, "").split("\\(");
				int tindex= tstr.length > 1 ? Tools.parseInt(tstr[1].replace(")", "")) : 0;
				index = Math.max(index, tindex);
			}
		}
		if(!file.exists() && index == 0){
			return file.getName();
		}
		index = index + 1;
		return fname.substring(0, fname.lastIndexOf(".")) + "("+index+")" + fname.substring(fname.lastIndexOf("."));
	}
	
}
