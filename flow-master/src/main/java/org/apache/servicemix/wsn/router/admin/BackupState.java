package org.apache.servicemix.wsn.router.admin;

import org.apache.servicemix.wsn.router.admin.base.AState;
import org.apache.servicemix.wsn.router.admin.detection.IDt;
import org.apache.servicemix.wsn.router.mgr.MsgNotis;
import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;
import org.apache.servicemix.wsn.router.msg.tcp.MsgAdReboot_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgAdminChange;
import org.apache.servicemix.wsn.router.msg.tcp.MsgConf_;
import org.apache.servicemix.wsn.router.msg.udp.MsgHeart;
import org.apache.servicemix.wsn.router.wsnPolicy.ShorenUtils;

import javax.naming.NamingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

public class BackupState extends AState {
	private AdminMgr Amgr;

	private IDt dt;

	public BackupState(AdminMgr Amgr, IDt dt) {
		this.Amgr = Amgr;
		this.dt = dt;
	}

	@Override
	public void sendHrt() {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		MsgHeart heart = new MsgHeart();
		byte[] Bheart = null;

		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			heart.indicator = Amgr.localAddr;
			oos.writeObject(heart);
			Bheart = baos.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			DatagramPacket p = new DatagramPacket(Bheart, Bheart.length, InetAddress.getByName(Amgr.backup), Amgr.uPort);
			DatagramSocket s = new DatagramSocket();
			s.send(p);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public void lost(String indicator) {
		if (Amgr.backup == indicator) {//备份管理员检测到主管理员失效
			if (Amgr.groups.size() > 0) {


				MsgNotis priAdminLost = new MsgNotis();// 当主管理员丢失时,与随机节点建立连接,将主管理员失效以通知消息形式送出

				priAdminLost.originatorAddr = Amgr.localAddr;
				priAdminLost.topicName = "priAdminLost";
				priAdminLost.doc = "priAdminLost";
				Date date = new Date();
				priAdminLost.sendDate = date;

				Socket s2 = null;
				ObjectOutputStream oos2 = null;
				ObjectInputStream ois2 = null;
				Random random = new Random();
				ArrayList<GroupUnit> list = new ArrayList(Amgr.groups.values());
				GroupUnit gu = list.get(random.nextInt(Amgr.groups.size()));
				MsgAdReboot_ mar_ = null;
				try {
					s2 = new Socket(gu.addr, gu.tPort);
					oos2 = new ObjectOutputStream(s2.getOutputStream());
					ois2 = new ObjectInputStream(s2.getInputStream());
					oos2.writeObject(priAdminLost);

					mar_ = (MsgAdReboot_) ois2.readObject();

					while (!mar_.c.isEmpty()) {
						//两种GroupUnit的转换
						GroupUnit g = (GroupUnit) mar_.c.poll();
						Amgr.groups.put(g.name, g);
					}

					ois2.close();
					oos2.close();
					s2.close();
//			} catch (UnknownHostException e) {
//				e.printStackTrace();
				} catch (IOException e) {
					System.out.println("与随机集群建立连接失败，该集群可能已经丢失");
					//e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("Primary administrator" + indicator + "lost，backup adminitrator" + Amgr.localAddr + "replace！");

				// 向根节点发送MsgAdReboot信息，要求返回MsgAdReboot_信息
				// 所有集群的集合
				dt.removeTarget(indicator);
//			if (!(gu.addr==null)&&!(gu.name==null)&&!(gu.tPort==0)) {
//				
//				MsgAdReboot mar = new MsgAdReboot();
//				MsgAdReboot_ mar_ = null;
//				
//				ObjectInputStream ois=null;
//				ObjectOutputStream oos=null;
//				Socket s=null;
//				
//				try {			
//					s = new Socket(gu.addr, gu.tPort);
//					oos = new ObjectOutputStream(s.getOutputStream());
//					ois = new ObjectInputStream(s.getInputStream()); 
//					
//					oos.writeObject(mar);
//					mar_ = (MsgAdReboot_)ois.readObject();
//					
//					while (!mar_.c.isEmpty()) {
//						//两种GroupUnit的转换
//						org.apache.servicemix.wsn.router.msg.tcp.GroupUnit g = (org.apache.servicemix.wsn.router.msg.tcp.GroupUnit)mar_.c.poll();
//						Amgr.groups.put(g.name, g);
//					}
//				} catch (UnknownHostException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (ClassNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//
//					if (s != null)
//						try {
//							ois.close();
//							oos.close();
//							s.close();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//				}//finally
//				
//			}//if
				//将管理员改变的消息转发至所有代表
				MsgAdminChange mac = new MsgAdminChange();
				mac.NewAdminAddr = Amgr.localAddr;

				Socket s = null;
				ObjectOutputStream oos = null;
				ObjectInputStream ois = null;

				Iterator<GroupUnit> it = Amgr.groups.values().iterator();
				while (it.hasNext()) {
					GroupUnit g = it.next();
					try {
						s = new Socket(g.addr, g.tPort);
						s.setSoTimeout(5000);
						oos = new ObjectOutputStream(s.getOutputStream());
						ois = new ObjectInputStream(s.getInputStream());
						oos.writeObject(mac);

						oos.close();
						ois.close();
						s.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("向某代表转发管理员改变消息失败，该集群可能已经丢失");
						//e.printStackTrace();
					}
				}
			}
			//恢复管理员界面

			Amgr.setState(Amgr.getPriState());
			Amgr.IsPrimary = 1;
			Amgr.port = 30006;

			try {
				Amgr.serverSocket = new ServerSocket(Amgr.port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			new Thread(Amgr).start();
			AdminUIThread uiThread = new AdminUIThread(Amgr);
			new Thread(uiThread).start();
		}//if

		try {
			ShorenUtils.encodeAllPolicy();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			System.out.println("将所有策略信息写入xml文件时出错");
			e.printStackTrace();
		}
	}

	@Override
	public void synTopoInfo() {
		// TODO Auto-generated method stub
	}

	@Override
	public void processUdpMsg(Object msg) {
		if (msg instanceof MsgHeart) {

			dt.onMsg(msg);
			System.out.println("heart from " + ((MsgHeart) msg).indicator);

		}
	}

	@SuppressWarnings("static-access")
	@Override
	public void processTcpMsg(Socket s) {
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		Object msg = null;

		try {
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
			msg = ois.readObject();
			if (msg instanceof GroupAllInfo) {//当前主管理员发来的集群信息

				GroupAllInfo grps = (GroupAllInfo) msg;
				if (grps.sendFlag.equals("Ask")) {//当前程序是主管理员，收到备份管理员的请求，回复所有集群信息
					GroupAllInfo sendInfo = new GroupAllInfo("Send");
					if (Amgr.groups != null) {
						Iterator<String> Itr = Amgr.groups.keySet().iterator();
						while (Itr.hasNext()) {
							String g = Itr.next();
							MsgConf_ conf_ = Amgr.ui.getConfiguration(g);
							sendInfo.groupconfs.put(g, conf_);
						}
						sendInfo.groups.putAll(Amgr.groups);
						System.out.println("*****************fasong");
						oos.writeObject(sendInfo);
					}
				} else if (grps.sendFlag.equals("Send")) {
					System.out.println(grps);
					if (!grps.groups.isEmpty()) {
						System.out.println("get all groups,count is " + grps.groups.size());
						Iterator<String> Itr = grps.groups.keySet().iterator();
						while (Itr.hasNext()) {
							String g = Itr.next();
							//groups.put(g, grps.groups.get(g));
							//groupconfs.put(g, grps.groupconfs.get(g));
							Amgr.ui.updateConfigureFile(g, grps.groupconfs.get(g));
						}
						Amgr.groups.putAll(grps.groups);
						System.out.println("groups变化,新的groups为" + Amgr.groups);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}