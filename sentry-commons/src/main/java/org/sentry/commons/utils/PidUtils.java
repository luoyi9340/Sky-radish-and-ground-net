package org.sentry.commons.utils;

import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * pid帮助文件
 * 
 * @author luoyi
 *
 */
public class PidUtils {

	/**
	 * 把自己的pid写入当前目录的pid文件中
	 */
	public static void writeMyself() {
		//	取自己的pid
		try {
			int pid = getPid();
			
			//	写入当前目录的pid文件里
			FileWriter fr = new FileWriter("pid");
			fr.write(new String((pid + "").getBytes(), "UTF-8"));
			fr.flush();
			fr.close();
		} catch (Exception e) {
			//	取失败就算了
		}
	}

	protected static int getPid() throws Exception{
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		String name = runtime.getName(); // format: "pid@hostname"
		try {
			return Integer.parseInt(name.substring(0, name.indexOf('@')));
		} catch (Exception e) {
			throw e;
		}
	}

}
