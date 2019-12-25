package com.jian.active.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jian.tools.core.cache.CacheObject;

@Component
public class RedisUtils {

	
	private static RedisCache cache;
	
	private static long outTime = 2 * 3600 * 1000; //资源超时时间。单位（毫秒）

	@Autowired
	public RedisUtils(RedisCache cache){
		RedisUtils.cache = cache;
	}

	/**
	 * 设置缓存数据。默认超时时间2小时。其中回收程序默认2小时运行一次。
	 * 
	 * @param key
	 * @param value
	 */
	public static void setCacheObj(String key, Object value) {
		setCacheObj(key, value, outTime);
	}

	/**
	 * 设置缓存数据。自定义超时时间。其中回收程序默认2小时运行一次。
	 * 
	 * @param key
	 * @param value
	 * @param timeOut
	 */
	public static void setCacheObj(String key, Object value, long timeOut) {
		CacheObject obj = new CacheObject(key, value);
		long cur = System.currentTimeMillis();
		obj.setMillis(cur); //设置缓存时间
		obj.setTimeOut(cur + timeOut); //设置超时时间
		cache.initSetCacheObj(obj);
	}

	/**
	 * 获取缓存数据。超时了返回null。
	 * 
	 * @param key
	 * @return
	 */
	public static CacheObject getCacheObj(String key) {
		CacheObject tmp = cache.initGetCacheObj(key);
		if(tmp == null){ 
			return null;
		}
		long cur = System.currentTimeMillis();
		if(tmp.getTimeOut() < cur){
			//超时移除
			clearCacheObj(key);
			return null;
		}
		return tmp;
	}

	/**
	 * 获取缓存数据。先判断自身是否超时，再判断给定的超时时间。
	 * 
	 * @param key
	 * @param outTime
	 * @return
	 */
	public static CacheObject getCacheObj(String key, long outTime) {
		CacheObject tmp = getCacheObj(key);
		if(tmp == null){ 
			return null;
		}
		long cur = System.currentTimeMillis();
		if((tmp.getMillis() + outTime) < cur){
			//超时移除
			clearCacheObj(key);
			return null;
		}
		return tmp;
	}

	/**
	 * 检测是否超时。true表示超时。默认超时时间2小时
	 * 
	 * @param key
	 * @return
	 */
	public static boolean isTimeout(String key) {
		return isTimeout(key);
	}

	/**
	 * 检测是否超时。true表示超时。自定义超时时间
	 * 
	 * @param key
	 * @param outTime
	 * @return
	 */
	public static boolean isTimeout(String key, long outTime) {
		CacheObject tmp = getCacheObj(key);
		if(tmp == null){ 
			return true;
		}
		long cur = System.currentTimeMillis();
		if((tmp.getMillis() + outTime) < cur){
			//超时移除
			clearCacheObj(key);
			return true;
		}
		return false;
	}

	/**
	 * 清楚缓存
	 * 
	 * @param key
	 */
	public static void clearCacheObj(String key) {
		cache.initClearCacheObj(key);
	}



}
