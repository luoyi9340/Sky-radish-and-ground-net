package org.sentry.commons;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Bean工具类
 * 
 * @author luoyi
 *
 */
public class BeanUtils {

	/**
	 * 取泛型类型
	 */
	public static Class<?> getParameterzedType(Class<?> clazz, int index) {
		ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
		if (parameterizedType == null) {return null;}
		
		Type[] types = parameterizedType.getActualTypeArguments();
		if (index > types.length - 1) {return null;}
		
		Class<?> entityClass = (Class<?>) (types[index]);
		return entityClass;
	}
}
