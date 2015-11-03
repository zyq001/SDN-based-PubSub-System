package org.apache.servicemix.wsn.router.admin.detection;

import org.apache.servicemix.wsn.router.msg.udp.MsgHeart;

import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class DtAdmin implements DtAction, IDt, Runnable {
	private long threshold = 45000;//失效阀值的缺省值
	private long sendPeriod = 10000;//发送频率的缺省值
	private long scanPeriod = 15000;//扫描频率的缺省值
	private long synPeriod = 20000;//同步频率的缺省值

	private HrtMsgHdlr AdminMgr;//调度模块

	private Timer timer;//计时器
	private DtTask scanTask;//扫描任务
	private DtTask sendTask;//发送任务
	private DtTask synTask;//同步任务

	private ConcurrentHashMap<String, Long> tbl;//心跳信息表，key为目标标识（目标是集群的话为集群名，目标为代理的话为其地址），value为时间的long表示
	private ArrayBlockingQueue<MsgHeart> q;//心跳消息的队列

	public DtAdmin(HrtMsgHdlr AdminMgr) {
		this.AdminMgr = AdminMgr;

		tbl = new ConcurrentHashMap<String, Long>();

		q = new ArrayBlockingQueue<MsgHeart>(10);

		timer = new Timer();
	}

	public void setThreshold(long value) {
		threshold = value;
	}

	public void setSendPeriod(long value) {
		if (sendTask != null)
			sendTask.cancel();

		sendTask = new DtTask(this, SEND);
		timer.schedule(sendTask, sendPeriod, sendPeriod);
	}

	public void setScanPeriod(long value) {
		if (scanTask != null)
			scanTask.cancel();

		scanTask = new DtTask(this, SCAN);
		timer.schedule(scanTask, scanPeriod, scanPeriod);
	}

	public void setSynPeriod(long value) {
		if (synTask != null)
			synTask.cancel();

		synTask = new DtTask(this, SYN);
		timer.schedule(synTask, synPeriod, synPeriod);
	}


	public void action(int type) {
		// TODO Auto-generated method stub
		if (type == SCAN)
			scanAction();
		else if (type == SEND)
			sendAction();
		else
			synAction();
	}

	private void synAction() {
		// TODO Auto-generated method stub
		AdminMgr.synTopoInfo();
	}

	private void scanAction() {
		Date cur = new Date();
		if (!tbl.isEmpty()) {
			for (String in : tbl.keySet()) {
				Long temTime = tbl.get(in);
				System.out.println(temTime);
				System.out.println(cur.getTime());
				if (cur.getTime() - tbl.get(in) > threshold) {

					int BackupIsPrimary = AdminMgr.BackupIsPrimary();//与备份服务器建立连接，探知对方是否是主管理员
					if (BackupIsPrimary == 0) {//对方不是主管理员

						//tell routing manager that some broker is timeout and remove the item
						tbl.remove(in);
						AdminMgr.lost(in);
					}
				}
			}
		}
	}

	private void sendAction() {
		AdminMgr.sendHrtMsg();

	}


	public void run() {
		// TODO Auto-generated method stub		
		try {
			while (true) {
				MsgHeart msg = q.take();
				tbl.put(msg.indicator, new Date().getTime());
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void addTarget(String indicator) {
		// TODO Auto-generated method stub
		tbl.put(indicator, new Date().getTime() + 1000);//add 1 minute to the new target
	}


	public void onMsg(Object msg) {
		// TODO Auto-generated method stub
		MsgHeart heartMsg = (MsgHeart) msg;
		try {
			q.put(heartMsg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}


	public void removeTarget(String indicator) {
		// TODO Auto-generated method stub
		if (tbl.containsKey(indicator))
			tbl.remove(indicator);
	}

}
