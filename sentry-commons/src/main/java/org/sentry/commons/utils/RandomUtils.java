package org.sentry.commons.utils;


/**
 * 随机数工具类
 * @author luoyi
 *
 */
public class RandomUtils {

	
	/**
	 * a ~ b
	 */
	public static Long randomAB(long a, long b) {
		long res = (long)(Math.random() * (a - b) + b);
		return res;
	}
	/**
	 * 随机决议值（0 ~ 100）
	 */
	public static Long randomElection() {
		return randomAB(0, 100);
	}
	
}
