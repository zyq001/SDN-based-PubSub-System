package org.apache.servicemix.wsn.jms;

import java.util.LinkedList;

public class ThreadPool extends ThreadGroup {
	private static int threadPoolID = 1;  //�̳߳ص�id
	private boolean isClosed = false;  //�̳߳��Ƿ�ر�
	private LinkedList workQueue;      //��������

	public ThreadPool(int poolSize) {  //poolSize ��ʾ�̳߳��еĹ����̵߳�����

		super(threadPoolID + "");      //ָ��ThreadGroup������
		setDaemon(true);               //�̳е��ķ����������Ƿ��ػ��̳߳�
		workQueue = new LinkedList();  //������������
		for (int i = 0; i < poolSize; i++) {
			new WorkThread(i).start();   //���������������߳�,�̳߳������Ƕ��پʹ������ٸ������߳�
		}
	}

	/**
	 * ���������м���һ��������,�ɹ����߳�ȥִ�и�����
	 */
	public synchronized void execute(Runnable task) {
		if (isClosed) {
			throw new IllegalStateException();
		}
		if (task != null) {
			workQueue.add(task);//������м���һ������
			notify();           //����һ������getTask()�����д�����Ĺ����߳�
		}
	}

	/**
	 * �ӹ���������ȡ��һ������,�����̻߳���ô˷���
	 */
	private synchronized Runnable getTask(int threadid) throws InterruptedException {
		while (workQueue.size() == 0) {
			if (isClosed) return null;
//            System.out.println("�����߳�"+threadid+"�ȴ�����...");   
			wait();             //�������������û������,�͵ȴ�����
		}
//        System.out.println("�����߳�"+threadid+"��ʼִ������...");   
		return (Runnable) workQueue.removeFirst(); //���ض����е�һ��Ԫ��,���Ӷ�����ɾ��
	}

	/**
	 * �ر��̳߳�
	 */
	public synchronized void closePool() {
		if (!isClosed) {
			waitFinish();        //�ȴ������߳�ִ�����
			isClosed = true;
			workQueue.clear();  //��չ�������
			interrupt();        //�ж��̳߳��е����еĹ����߳�,�˷����̳���ThreadGroup��
		}
	}

	/**
	 * �ȴ������̰߳���������ִ�����
	 */
	public void waitFinish() {
		synchronized (this) {
			isClosed = true;
			notifyAll();            //�������л���getTask()�����еȴ�����Ĺ����߳�
		}
		Thread[] threads = new Thread[activeCount()]; //activeCount() ���ظ��߳����л�̵߳Ĺ���ֵ��
		int count = enumerate(threads); //enumerate()�����̳���ThreadGroup�࣬���ݻ�̵߳Ĺ���ֵ����߳����е�ǰ���л�Ĺ����߳�
		for (int i = 0; i < count; i++) { //�ȴ����й����߳̽���
			try {
				threads[i].join();  //�ȴ������߳̽���
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * �ڲ���,�����߳�,����ӹ���������ȡ������,��ִ��
	 *
	 * @author sunnylocus
	 */
	private class WorkThread extends Thread {
		private int id;

		public WorkThread(int id) {
			//���๹�췽��,���̼߳��뵽��ǰThreadPool�߳�����
			super(ThreadPool.this, id + "");
			this.id = id;
		}

		public void run() {
			while (!isInterrupted()) {  //isInterrupted()�����̳���Thread�࣬�ж��߳��Ƿ��ж�
				Runnable task = null;
				try {
					task = getTask(id);     //ȡ������
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				//���getTask()����null�����߳�ִ��getTask()ʱ���жϣ���������߳�
				if (task == null) return;

				try {
					task.run();  //��������
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}//  end while
		}//  end run
	}// end workThread

}
