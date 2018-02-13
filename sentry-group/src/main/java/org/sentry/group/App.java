package org.sentry.group;

import org.apache.log4j.Logger;
import org.sentry.commons.utils.PidUtils;
import org.sentry.group.com.ICom;
import org.sentry.group.node.CommunicationList;
import org.sentry.group.node.ConfirmResourceCallbackPool;
import org.sentry.group.node.ResolutionPool;
import org.sentry.group.node.TeamMembers;
import org.sentry.group.node.TeamMembers.ICloseCallback;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * 	状态：
 *	1 所有节点分为Looking，Leader，Following3种状态。
 *	2 无主的节点都处于Looking状态，除了选主不做其他任何事情。（新启动的节点，检测到主挂掉时的节点）
 *	3 Leader为主节点，负责发起除出选主外的各项决议
 *	
 *
 *	决议：
 *	1 当需要半数以上票数才能产生结果时，会向集群其他节点发起决议
 *	2 每项决议信息会有（决议版本号，决议类型，决议值）
 *		决议版本号 + 决议类型 决定一轮决议
 *		决议值为本节点针对本轮决议提出的值
 *		决议类型包括：选主，确认资源
 *	3 每次决议有时限，超过时限还没收到回应的节点忽略，已当前收到的信息为准
 *
 *
 *	选主：
 *	1 只有Looking状态的节点才能发起选主决议
 *	2 Looking会询问集群其他节点询问当前谁是主（lv：当前主版本号，id：host:port）
 *		连续两次收不到回应则判定集群中该节点已经已失效，忽略该节点本轮选主的回应
 *		集群中所有节点都失效则广播自己当主（此时的lv为0）
 *		如果被询问方是Looking节点，则直接回当前无主
 *		如果被询问方是Leader节点，则直接回自己
 *		如果被询问方是Following节点，则先确认当前主是否有效，确认有效再回当前主信息，否则自己变成Looking节点
 *	3 如果收到回应当前存在主，则以回应中当前版本号最大的为准，若存在两个版本号相同但id不同，则发起重新选主
 *	4 如果收到回应都是当前没主，则发起选主决议
 *		Looking节点一旦受到选主决议，便不再继续询问当前主信息
 *	5 Looking节点一旦发起或收到选主决议，则向集群中其他人广播自己的参选信息（lv：决议版本号，val：自己的值）
 *		若Looking节点先收到参选信息，则直接进入选主决议流程
 *		若Leader或Following节点收到选主决议，则先判断当前决议版本号是否超过当前主版本号
 *			若超过，则自己退回Looking状态，参与选主
 *			若小于，否则直接返回当前主信息
 *			若等于，则不予处理，等待新主产生时再做判断
 *	6 所有参选信息均被回收时，如果自己是val值最大的，则广播自己是主（lv：决议版本号，val：自己的值，info：自己的信息）
 *	7 若Leader或Following收到广播的主信息，则判断决议版本号和val值
 *		若决议版本号超过自己，则以新主为准
 *		若小于，则广播自己是主
 *		若等于，以val值大的为准
 *
 *	
 *	
 *
 */
@SuppressWarnings("restriction")
public class App {
	
	
	static Logger log = Logger.getLogger("sentry-group");
	
	
	//	全局的一些信息
	public static ApplicationContext context;
	//	成员-自己
	public static TeamMembers ME;
	
	
	public static void main(String[] args) {
		//	写入自己的pid
		PidUtils.writeMyself();
		//	挂载关闭监听
		killHandly();
		
		initContext();
		initSentry();
	}
	
	
	/**
	 * 监听kill命令
	 * <p>	有点奇怪，不是打印‘服务资源已关闭’就说明已经关闭了
	 * <p>	总是是有部分资源要等上几秒甚至几十秒才能释放干净
	 */
	protected static void killHandly() {
		SignalHandler handler = new SignalHandler() {
			public void handle(Signal arg0) {
				log.info("[KILL] 收到kill命令，执行关闭.");
				
				ME.close(new ICloseCallback() {
					public void onSucc() {
						log.info("[KILL] 服务资源已关闭.");
						System.exit(0);
					}
					@Override
					public void onError(Throwable t) {
						log.error("[KILL] 服务资源关闭异常." + t.getMessage(), t);
					}
				});
			}
		};
		Signal.handle(new Signal("TERM"), handler);				//	kill PID
	}
	/**
	 * 关闭
	 */
	public static void errorClose() {
		log.info("[ERROR] 无法对外提供服务，异常关闭.");
		System.exit(0);
	}
	
	
	/**
	 * 初始化环境
	 */
	protected static void initContext() {
		context = new ClassPathXmlApplicationContext("conf/spring/spring-sentry-group.xml");
	}
	
	/**
	 * 初始化哨兵信息
	 */
	protected static void initSentry() {
		//	初始化时有先后顺序
		ME = context.getBean("teamMembers", TeamMembers.class);
		ME.init();
		//	初始化通讯清单
		CommunicationList communicationList = context.getBean("communicationList", CommunicationList.class);
		communicationList.init();
		//	初始化通讯器
		ICom com = context.getBean("com", ICom.class);
		com.init();
		//	初始化资源确认handler池
		ConfirmResourceCallbackPool confirmPool = context.getBean("confirmPool", ConfirmResourceCallbackPool.class);
		confirmPool.init();
		//	初始化决议池子
		ResolutionPool resolutionPool = context.getBean("resolutionPool", ResolutionPool.class);
		resolutionPool.init();
		//	开始工作
		ME.startWorking();
	}
}
