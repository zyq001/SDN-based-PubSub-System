package org.Mina.shorenMinaTest.mgr;


import org.Mina.shorenMinaTest.mgr.base.AState;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;


public class RegState extends AState {
	private static Log log = LogFactory.getLog(RegState.class);

	public RegState(RtMgr mgr) {
		this.mgr = mgr;
	}

	public RtMgr getMgr() {
		return mgr;
	}

	public void setMgr(RtMgr mgr) {
		this.mgr = mgr;
	}

	@Override
	public void processMsg(IoSession session, WsnMsg msg) {
		msg.processRegMsg(session);
	}

	public void processMsg(WsnMsg msg) {
//		msg.processRegMsg(new IoSession()) {
//		});
	}

}
