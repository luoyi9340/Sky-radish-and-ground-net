package org.sentry.commons;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;


/**
 * 相关配置信息
 * 
 * @author luoyi
 *
 */
public class Configuration extends PropertyPlaceholderConfigurer {


	//	配置项
	protected static Map<Object, Object> itemsHolder = new HashMap<Object, Object>();

	
	@Override
	protected void loadProperties(Properties props) throws IOException {
		super.loadProperties(props);
		itemsHolder.putAll(props);
	}
	
	
	/**
	 * 取值
	 */
	public static Object get(String key){
		return itemsHolder.get(key);
	}
	public static String get(String key, String def){
		String value = (String) itemsHolder.get(key);
		return value == null ? def : value;
	}
	public static Boolean get(String key, Boolean def){
		Object val = itemsHolder.get(key);
		try{
			return Boolean.valueOf(val.toString());
		}catch (Exception e){
			return def;
		}
	}
	public static Integer get(String key, Integer def){
		Object val = itemsHolder.get(key);
		try{
			return Integer.valueOf(val.toString());
		}catch (Exception e){
			return def;
		}
	}
	public static Long get(String key, Long def) {
		Object val = itemsHolder.get(key);
		try{
			return Long.valueOf(val.toString());
		}catch (Exception e){
			return def;
		}
	}
	
}
