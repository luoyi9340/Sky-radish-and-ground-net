package org.sentry.group.com.wait_receipt_queue;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.log4j.Logger;
import org.sentry.group.com.ICom;
import org.sentry.group.com.ICom.IMsgReceivedCallback;
import org.sentry.group.com.ICom.IMsgTimeoutCallback;
import org.sentry.group.com.IWaitReceiptQueue;
import org.sentry.group.com.msg.AGroupMsg;
import org.sentry.group.node.CommunicationList;


/**
 * 等待回执队列
 * 
 * @author luoyi
 *
 */
public class WaitReceiptQueueImpl implements IWaitReceiptQueue {
	
	
	static Logger log = Logger.getLogger("sentry-group-com-receipt");

	
	//	实际存储等待超时队列（跳表）
	protected ConcurrentSkipListSet<WaitVo> queue = new ConcurrentSkipListSet<WaitVo>(new QueueComparator());
	//	实际存储等待回执队列（key：seqId，value：msgReceivedCallback）
	protected Map<String, IMsgReceivedCallback> receivedHolder = new ConcurrentHashMap<String, IMsgReceivedCallback>();
	
	
	//	触发线程
	protected TriggerThread triggerThread;
	
	//	对方信息
	protected CommunicationList.Info info;
	
	
	/**
	 * 初始化
	 */
	public void init() {
		triggerThread = new TriggerThread();
		triggerThread.start();
	}
	
	
	/**
	 * 消息扔进队列
	 */
	public void append(AGroupMsg msg, long timeout, ICom.IMsgTimeoutCallback callback) {
		WaitVo vo = new WaitVo();
		vo.setMsg(msg);
		vo.setInfo(info == null ? msg.getInfo() : info);
		vo.setTimeout(System.currentTimeMillis() + timeout);
		vo.setTimeoutCallback(callback);
		
		queue.add(vo);
		
		//	检测新对象的触发时间是否小于队首，或者队列只有1个，则线程唤醒一次
		if (vo.getTimeout() < touch().getTimeout()
				|| queue.size() == 1) {
			synchronized (triggerThread) {
				try {
					triggerThread.notify();
				}catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}
	/**
	 * 消息扔进队列
	 */
	@Override
	public void append(AGroupMsg msg, long timeout, IMsgTimeoutCallback callback, IMsgReceivedCallback receivedCallback) {
		append(msg, timeout, callback);
		
		//	保存等待回执holder
		receivedHolder.put(msg.getSeqId(), receivedCallback);
	}


	/**
	 * 删除等待回执的消息
	 */
	public boolean remove(AGroupMsg msg) {
		WaitVo vo = new WaitVo();
		vo.setMsg(msg);

		boolean succ = queue.remove(vo);
		//	从receivedHolder中删除
		receivedHolder.remove(msg.getSeqId());
		return succ;
	}
	
	
	@Override
	public IMsgReceivedCallback getReceivedCallback(AGroupMsg msg) {
		return receivedHolder.get(msg.getSeqId());
	}


	/**
	 * 取第一个元素信息
	 */
	public WaitVo touch() {
		if (queue.isEmpty()) {return null;}
		
		return queue.first();
	}
	/**
	 * 取第一个元素
	 */
	public WaitVo poll() {
		if (queue.isEmpty()) {return null;}
		
		return queue.pollFirst();
	}
	
	
	/**
	 * 队首扫描线程
	 * <p>	touch到队首时如果没到timeout时间，则等待到timeout时间戳，最多等待1s。允许10ms误差
	 */
	class TriggerThread extends Thread {

		//	是否在跑
		protected volatile boolean running = false;
		
		@Override
		public void run() {
			running = true;
			log.info("[TriggerThread] started...");
			
			while (running) {
				try {
					//	检测队首元素的超时时间
					WaitVo vo = poll();
					//	如果取到是空，则等待1s
					if (vo == null) {
						synchronized (this) {
							this.wait(1000);
						}
						continue;
					}
					
					//	距离触发时间的时间差（允许10ms误差）
					long time = System.currentTimeMillis() - vo.timeout;
					if (time + 10 > 0) {
						vo.timeoutCallback.timeout(vo.info, vo.msg);
						//	从receivedHolder中删除
						receivedHolder.remove(vo.msg.getSeqId());
						continue;
					}
					else {
						//	还没到触发时间的对象丢回队列
						queue.add(vo);
						
						//	等待休眠到实际触发时间，但最多等待1s
						time = Math.abs(time);
						synchronized (this) {
							this.wait(time);
						}
					}
				}catch (Throwable t) {
					log.error("[TriggerThread] run error. " + t.getMessage(), t);
				}
			}
		}
	}
	
	
	/**
	 * 等待对象排序器
	 * <p>	按照等待的超时时间升序
	 */
	static class QueueComparator implements Comparator<WaitVo> {
		public int compare(WaitVo o1, WaitVo o2) {
			if (o1.equals(o2)) {return 0;}
			return o1.getTimeout() > o2.getTimeout() ? 1 : -1;
		}
	}


	public CommunicationList.Info getInfo() {
		return info;
	}

	public void setInfo(CommunicationList.Info info) {
		this.info = info;
	}
}
