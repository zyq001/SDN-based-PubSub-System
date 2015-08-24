package org.Mina.shorenMinaTest.mgr;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;

import org.Mina.shorenMinaTest.mgr.base.AState;
import org.Mina.shorenMinaTest.msg.WsnMsg;


public class RegState extends AState {
	private static Log log = LogFactory.getLog(RegState.class);

	public RtMgr getMgr() {
		return mgr;
	}

	public void setMgr(RtMgr mgr) {
		this.mgr = mgr;
	}


	public RegState(RtMgr mgr) {
		this.mgr = mgr;
	}

	@Override
	public void processMsg(IoSession session, WsnMsg msg) {
		msg.processRegMsg(session);
	}

}
