package org.sentry.commons.mulitiple;

import java.util.Timer;
import java.util.TimerTask;

import org.sentry.commons.guard.IDetection;
import org.sentry.commons.guard.Sentry;

/**
 * 哨兵组模式下的哨兵
 * 
 * @author luoyi
 *
 */
public class MulitipleSentry extends Sentry {

	
	//	是否是当前master
	protected boolean master = false;
	
	//	是否是备选master
	protected boolean standBy = false;

	
	@Override
	public void doWork() {
		if (!running) {
			if (timer == null) {timer = new Timer();}
			
			timer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					for (IDetection detection : detections) {
						boolean res = detection.detection();
						
						//	如果检测到资源异常
						//	如果自己就是master，则询问其他节点资源是否异常
						if (master) {
							
						}
						//	如果自己不是master，则上报master有资源异常
						else {
							
						}
						
						if (res) {callback.normal(detection.name());}
						else {callback.abnormality(detection.name());}
					}
				}
			}, 1000, interval);
		
			running = true;
		}
	}
	
}
