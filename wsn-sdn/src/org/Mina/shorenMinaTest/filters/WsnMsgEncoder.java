/**
 * @author shoren
 * @date 2013-6-5
 */
package org.Mina.shorenMinaTest.filters;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import org.Mina.shorenMinaTest.msg.WsnMsg;

/**
 *
 */
public class WsnMsgEncoder extends ProtocolEncoderAdapter{

	private final Charset charset;
	
	public WsnMsgEncoder(Charset charset){
		this.charset = charset;
	}
	
	
	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
//		System.out.println("enter in WsnMsgEncoder");
		
		String msgString = null;
		//创建IoBuffer 缓冲区对象，并设置为自动扩展；
		IoBuffer buffer = IoBuffer.allocate(10000).setAutoExpand(true);
		if(message instanceof WsnMsg){
			//将message 对象强制转换为指定的对象类型；
			WsnMsg msg = (WsnMsg)message;
			
			//将转换后的message 对象中的各个部分按照指定的应用层协议进行组装，并put()到IoBuffer 缓冲区；
			//具体编码用StringBuffer
			msgString = msg.msgToString();
		}else{
			msgString = message.toString();
			msgString = msgString.length() + msgString;
		}
			

			
//		System.out.println("encode msg:" + msgString);
		
		CharsetEncoder ce = charset.newEncoder();
		buffer.putInt(msgString.length());
		buffer.putString(msgString, ce);
		//输出IoBuffer 缓冲区实例
		buffer.flip();
		out.write(buffer);
	}

}
