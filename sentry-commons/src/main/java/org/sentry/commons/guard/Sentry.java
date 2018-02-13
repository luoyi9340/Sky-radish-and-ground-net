package org.sentry.commons.guard;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 哨兵定时任务
 * 
 * @author luoyi
 *
 */
public class Sentry {

	
	//	触发间隔（单位：毫秒）
	protected long interval;
	
	
	//	需要盯着的资源组
	protected List<IDetection> detections;
	//	资源回调
	protected IDetectionCallback callback;
	
	
	//	定时任务
	protected Timer timer;
	protected boolean running = false;
	
	
	/**
	 * 开启检测
	 */
	public void doWork() {
		if (!running) {
			if (timer == null) {timer = new Timer();}
			
			timer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					for (final IDetection detection : detections) {
						boolean res = detection.detection();
						
						if (res) {callback.normal(detection.name());}
						else {callback.abnormality(detection.name());}
					}
				}
			}, 1000, interval);
			
			running = true;
		}
	}
	

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}


	public List<IDetection> getDetections() {
		return detections;
	}


	public void setDetections(List<IDetection> detections) {
		this.detections = detections;
	}


	public IDetectionCallback getCallback() {
		return callback;
	}


	public void setCallback(IDetectionCallback callback) {
		this.callback = callback;
	}
}
