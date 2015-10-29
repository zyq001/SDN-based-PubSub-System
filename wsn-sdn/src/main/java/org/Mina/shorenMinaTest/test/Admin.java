package org.Mina.shorenMinaTest.test;

import org.Mina.shorenMinaTest.MinaUtil;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Admin {


	public static void main(String[] args) {
//		timeAdjust();
		fireMsg();
//		randomData();
	}

	//adjust time for client
	/*	public static void TCPMessageReceived(IoSession session, Object message){
			System.out.println("**** receive time : " + message);
			long t1 = Long.parseLong((String)message);
			long t2 = System.currentTimeMillis();
			System.out.println("**** adjust time : " + (t2 - t1));
		}
		*/

	public static void timeAdjust() {
		NioSocketConnector connector1 = MinaUtil.createSocketConnector();
		ConnectFuture cf1 = connector1.connect(new InetSocketAddress("10.109.253.29", 30001));//��������
		cf1.awaitUninterruptibly();//�ȴ����Ӵ������ 
		long t1 = System.currentTimeMillis();
		cf1.getSession().write(t1);
		System.out.println(t1);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		t1 = System.currentTimeMillis();
		cf1.getSession().write(t1);
		System.out.println(t1);
	}


	public static void fireMsg() {
		String message = "Ready?Go!";
		NioSocketConnector connector1 = MinaUtil.createSocketConnector();
		ConnectFuture cf1 = connector1.connect(new InetSocketAddress("10.109.253.31", 30001));//��������
		cf1.awaitUninterruptibly();//�ȴ����Ӵ������   

		NioSocketConnector connector2 = MinaUtil.createSocketConnector();
		ConnectFuture cf2 = connector2.connect(new InetSocketAddress("10.109.253.13", 30001));//��������
		cf2.awaitUninterruptibly();//�ȴ����Ӵ������

		NioSocketConnector connector3 = MinaUtil.createSocketConnector();
		ConnectFuture cf3 = connector3.connect(new InetSocketAddress("10.109.253.14", 30001));//��������
		cf3.awaitUninterruptibly();//�ȴ����Ӵ������

		NioSocketConnector connector4 = MinaUtil.createSocketConnector();
		ConnectFuture cf4 = connector4.connect(new InetSocketAddress("10.109.253.18", 30001));//��������
		cf4.awaitUninterruptibly();//�ȴ����Ӵ������
		
/*		NioSocketConnector connector5 = MinaUtil.createSocketConnector();	
		ConnectFuture cf5 = connector5.connect(new InetSocketAddress("10.109.253.14", 30001));//��������
		cf5.awaitUninterruptibly();//�ȴ����Ӵ������
*/		
		/*NioSocketConnector connector6 = MinaUtil.createSocketConnector();	
		ConnectFuture cf6 = connector6.connect(new InetSocketAddress("10.109.253.26", 30001));//��������
		cf5.awaitUninterruptibly();//�ȴ����Ӵ������
*/				
		/*NioSocketConnector connector6 = MinaUtil.createSocketConnector();	
		ConnectFuture cf6 = connector6.connect(new InetSocketAddress("10.109.253.14", 30001));//��������
		cf6.awaitUninterruptibly();//�ȴ����Ӵ������
*/		
		/*NioSocketConnector connector7 = MinaUtil.createSocketConnector();	
		ConnectFuture cf7 = connector7.connect(new InetSocketAddress("10.109.253.32", 30001));//��������
		cf7.awaitUninterruptibly();//�ȴ����Ӵ������
*/				
/*		NioSocketConnector connector8 = MinaUtil.createSocketConnector();	
		ConnectFuture cf8 = connector8.connect(new InetSocketAddress("10.109.254.65", 30001));//��������
		cf8.awaitUninterruptibly();//�ȴ����Ӵ������
		*/
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cf1.getSession().write(message);
		//       cf2.getSession().write(message);
//		cf3.getSession().write(message);
//		cf4.getSession().write(message);
//		cf5.getSession().write(message);
//		cf6.getSession().write(message);
//		cf7.getSession().write(message);
//		cf8.getSession().write(message);
	}


	public static void randomData() {
		HashMap<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>();
		Random rd = new Random();
		for (int i = 0; i < 5; i++) {
			System.out.println(rd.nextInt(8));
		}
/*		for(int i=0;i<45;i++){
			int t1 = rd.nextInt(30);
			int t2 = rd.nextInt(31);
			if(map.get(t1) != null){
				map.get(t1).add(t2);
			}	else{
				ArrayList<Integer> arr = new ArrayList<Integer>();
				arr.add(t2);
				map.put(t1, arr);
			}		
		}
		Set<Integer> keys = map.keySet();
		Iterator it = keys.iterator(); 
		while(it.hasNext()){
			Integer key = (Integer) it.next();
			ArrayList<Integer> arr = map.get(key);
			System.out.println(key + "-------" + arr.toString());
		}*/
	}
}

