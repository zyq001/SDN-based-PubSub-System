package org.apache.servicemix.wsn.router.router;

import edu.bupt.wangfu.sdn.info.Flow;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by root on 15-10-8.
 */
public class FlowDownwardQueue extends Thread {

	private static FlowDownwardQueue INSTANCE;
	public BlockingQueue<Flow> queue;

	private FlowDownwardQueue() {
		queue = new LinkedBlockingDeque<Flow>();
	}

	public static FlowDownwardQueue grtInstance() {
		if (INSTANCE == null) INSTANCE = new FlowDownwardQueue();
		return INSTANCE;
	}

	public boolean enqueque(Flow flow) {
		boolean success = false;
		success = queue.offer(flow);
		return success;
	}

	public boolean enqueue(List<Flow> flows) {
		boolean success = false;
		for (Flow flow : flows) {
			//atmost try twice;
			if (!enqueque(flow)) success = enqueque(flow);
		}
		return success;
	}

	public void run() {
		while (true) {
			try {
				Flow flow = queue.take();
				if (!GlobleUtil.downFlow(flow.controller, flow)) {
					queue.put(flow);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
