package com.jian.active.entity;

//import
import com.jian.annotation.PrimaryKey;
import com.jian.annotation.PrimaryKeyType;
import com.jian.annotation.Table;
import com.jian.annotation.Excel;

/**
 * @author liujian
 * @Date 
 */
@Table("s_user")
public class User  extends Base<User> {
	
	//field
	@PrimaryKey(type=PrimaryKeyType.AUTO_INCREMENT)
	@Excel(name="自增主键", sort=0, length="11", isNull=0 )
	private int pid;
	@Excel(name="用户名", sort=1, length="20", isNull=1 )
	private String username;
	@Excel(name="密码   md5", sort=2, length="32", isNull=1 )
	private String password;
	@Excel(name="sso标识", sort=3, length="100", isNull=1 )
	private String sso;
	@Excel(name="系统账号  0--否 1--是", sort=4, value="0", length="4", isNull=1 )
	private int system;
	@Excel(name="状态  0：禁用，1：启用", sort=5, value="1", length="4", isNull=1 )
	private int status;
	
	//get set
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSso() {
		return sso;
	}
	public void setSso(String sso) {
		this.sso = sso;
	}
	public int getSystem() {
		return system;
	}
	public void setSystem(int system) {
		this.system = system;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
