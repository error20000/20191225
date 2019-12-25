package com.jian.active.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.jian.tools.core.cache.CacheObject;

@Service
public class RedisCache {
	
	private static Map<String, CacheObject> objMap = new ConcurrentHashMap<String, CacheObject>();
	
	
	
	@PostConstruct
	public void test() {
		System.out.println("=====================RedisCache start===========================");
	}
	
	
	protected void initSetCacheObj(CacheObject obj) {
		objMap.put(obj.getKey(), obj);
	}
	
	protected CacheObject initGetCacheObj(String key) {
		return objMap.get(key);
	}
	
	protected void initClearCacheObj(String key) {
		objMap.remove(key);
	}
	
	
}
