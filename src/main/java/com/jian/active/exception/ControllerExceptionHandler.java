package com.jian.active.exception;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jian.tools.core.ResultKey;
import com.jian.tools.core.ResultTools;
import com.jian.tools.core.Tips;

@ResponseBody
@Order(-1)
@ControllerAdvice(basePackages = "com.jian")
public class ControllerExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

	@ExceptionHandler({ ServiceException.class })
	@ResponseStatus(HttpStatus.OK)
	public String ServiceValidHandler(HttpServletRequest request, HttpServletResponse response, ServiceException e) {
		String message = e.getLocalizedMessage();
		log.error(message);
		return message;
	}

	@ExceptionHandler({ Exception.class })
	@ResponseStatus(HttpStatus.OK)
	public String GlobalException(HttpServletRequest request, HttpServletResponse response, Exception e) {

		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		
		log.error(result.toString());
		
		return ResultTools.custom(Tips.ERROR0).put(ResultKey.MSG, e.getMessage()).toJSONString();
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(OutOfMemoryError.class)
	private String handleOutOfMemoryError(OutOfMemoryError e) {
		String message = e.getLocalizedMessage();
		System.out.println("----------------------------");
		System.out.println(message);
		try {
			
			String cmd = "";
			String pid = System.getProperty("PID");
			String osName = System.getProperty("os.name");
			String javaVersion = System.getProperty("java.version");
			String javaBin = System.getProperty("java.home").split(javaVersion)[0].replace("jre", "jdk") + javaVersion + File.separator + "bin";
			String userDir = System.getProperty("user.dir");
			String jstackPath = userDir + File.separator + "jstack_" + System.currentTimeMillis() + ".txt";
			if(osName.toLowerCase().contains("windows")){
				// cmd /c cd C:\\Program Files\\Java\\jre1.8.0_111\\bin && c: && jstack -F "+ pid + " > d:\\1.txt
				cmd = "cmd /c cd "+javaBin+" && "+javaBin.split(":")[0]+": && jstack -F " + pid + " > "+jstackPath;
			}else{
				cmd = "cmd -c cd "+javaBin+" && jstack -F " + pid + " > "+jstackPath;
			}
			Process test = Runtime.getRuntime().exec(cmd);
			/*InputStream in = test.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "gbk"));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

			System.out.println("===========================================");
			InputStream ein = test.getErrorStream();
			BufferedReader ereader = new BufferedReader(new InputStreamReader(ein, "gbk"));
			String eline;
			while ((eline = ereader.readLine()) != null) {
				System.out.println(eline);
			}*/
			System.out.println("=======================end jstack=============================");
			String jinfoPath = userDir + File.separator + "jinfo_" + System.currentTimeMillis() + ".txt";
			if(osName.toLowerCase().contains("windows")){
				// cmd /c cd C:\\Program Files\\Java\\jre1.8.0_111\\bin && c: && jstack -F "+ pid + " > d:\\1.txt
				cmd = "cmd /c cd "+javaBin+" && "+javaBin.split(":")[0]+": && jinfo " + pid + " > "+jinfoPath;
			}else{
				cmd = "cmd -c cd "+javaBin+" && jinfo " + pid + " > "+jinfoPath;
			}
			Process info = Runtime.getRuntime().exec(cmd);
			
			System.out.println("=======================end jinfo=============================");
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return message;
	}
}
