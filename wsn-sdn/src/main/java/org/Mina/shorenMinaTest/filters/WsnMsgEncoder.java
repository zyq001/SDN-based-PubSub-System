/**
 * @author shoren
 * @date 2013-6-5
 */
package org.Mina.shorenMinaTest.filters;

import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 *
 */
public class WsnMsgEncoder extends ProtocolEncoderAdapter {

	private final Charset charset;

	public WsnMsgEncoder(Charset charset) {
		this.charset = charset;
	}


	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
//		System.out.println("enter in WsnMsgEncoder");

		String msgString = null;
		//����IoBuffer ���������󣬲�����Ϊ�Զ���չ��
		IoBuffer buffer = IoBuffer.allocate(10000).setAutoExpand(true);
		if (message instanceof WsnMsg) {
			//��message ����ǿ��ת��Ϊָ���Ķ������ͣ�
			WsnMsg msg = (WsnMsg) message;

			//��ת�����message �����еĸ������ְ���ָ����Ӧ�ò�Э�������װ����put()��IoBuffer ��������
			//���������StringBuffer
			msgString = msg.msgToString();
		} else {
			msgString = message.toString();
			msgString = msgString.length() + msgString;
		}


//		System.out.println("encode msg:" + msgString);

		CharsetEncoder ce = charset.newEncoder();
		buffer.putInt(msgString.length());
		buffer.putString(msgString, ce);
		//���IoBuffer ������ʵ��
		buffer.flip();
		out.write(buffer);
	}

}
