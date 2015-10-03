package OvsInitModule;

import java.io.IOException;

import OvsOperation.OvsOperate;

public class OvsInit {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void initOvs() throws IOException{
		
		OvsOperate OvsOp = new OvsOperate();
		
		OvsOp.AddBridge("br0");
		
		OvsOp.AddBridgePort("br0", "eth0");
		
		/////////////////////////////////////////////////////
		OvsOp.AddBridgePort("br0", "eth1"); 
		////////////////////////////////////////////////////
		OvsOp.AddFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30001,actions=normal");
		OvsOp.AddFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30002,actions=normal");
		OvsOp.AddFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30003,actions=normal");

	}

}
