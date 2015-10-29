/**
 * @author shoren
 * @date 2013-6-5
 */
package org.Mina.shorenMinaTest.filters;

import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;


/**
 *
 */
public class WsnMsgDecoder extends ProtocolDecoderAdapter {

	private final Charset charset;
	private final AttributeKey CONTEXT = new AttributeKey(getClass(), "context");
	private int maxPackLength = 10000;


	public WsnMsgDecoder(Charset charset) {
		this.charset = charset;
	}

	/**
	 * ��������ǰ������Ϣ�����������ݳ���.ͨ�������õ���Ϣ��ʵ�������ݳ���ֵ����ȡ���е���Ϣ���ݡ�
	 * Ȼ�����ʵ����StringToMsg������Ĭ���ǵ���MinaUtil�е�ʹ�÷���ķ�������Ϊʵ���ĸ�����ֵ��
	 * ��ô���ֽ����ұ߽��أ�����������������
	 */
	@Override
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
//		System.out.println("enter in WsnMsgDecoder");  

		final int packHeadLength = 4;
		//�Ȼ�ȡ�ϴεĴ��������ģ����п�����δ�����������
		Context ctx = getContext(session);
		// �Ȱѵ�ǰbuffer�е�����׷�ӵ�Context��buffer����
		ctx.append(in);
		//��positionָ��0λ�ã���limitָ��ԭ����positionλ��
		IoBuffer buf = ctx.getBuffer();
		buf.flip();

		// Ȼ�����ݰ���Э����ж�ȡ
		while (buf.remaining() >= packHeadLength) {
			buf.mark();
			// ��ȡ��Ϣͷ����
			int length = buf.getInt();     //nice

			//����ȡ�İ�ͷ�Ƿ��������������Ļ����buffer
			if (length < 0 || length > maxPackLength) {
				buf.clear();
				break;
			}
			//��ȡ��������Ϣ������д��������У��Ա�IoHandler���д���
			else if (length <= buf.remaining()) {   //length��ʾ���Ǻ������ݵĳ���
				int oldLimit2 = buf.limit();
				buf.limit(buf.position() + length);
				String content = buf.getString(ctx.getDecoder());
				buf.limit(oldLimit2);
//	                System.out.println(content); //logger record it
				if (content.contains("className=")) {
					//������Ϣʵ��
					int index1 = content.indexOf("=");
					int index2 = content.indexOf(";");
					String className = content.substring(index1 + 1, index2);
					content = content.substring(index2 + 1);  //��Ϣ����

					WsnMsg msg = (WsnMsg) Class.forName(className).newInstance(); //�õ�ʵ��

					if (content != null && content != "" && content.length() != 0) {
						msg.stringToMsg(content);  //Ϊ������ֵ
					}
					out.write(msg);       //д��ͨ��
				} else {
					out.write(content);
				}


			} else {
				// �����Ϣ��������
				// ��ָ�������ƶ���Ϣͷ����ʼλ��
				buf.reset();
				break;
			}
		}
		if (buf.hasRemaining()) {
			// �������Ƶ�buffer����ǰ��
			IoBuffer temp = IoBuffer.allocate(maxPackLength).setAutoExpand(true);
			temp.put(buf);
			temp.flip();
			buf.clear();
			buf.put(temp);
			//        String con = buf.getString(ctx.getDecoder());

		} else {// ��������Ѿ�������ϣ��������
			buf.clear();
		}


	}


	/**
	 * ��¼�����ģ���Ϊ���ݴ���û�й�ģ���ܿ���ֻ�յ����ݰ���һ��.	���ԣ���Ҫ������ƴ�������������Ĵ��� .
	 */
	private Context getContext(IoSession session) {
		Context context = (Context) session.getAttribute(CONTEXT);
		if (context == null) {
			context = new Context();
			session.setAttribute(CONTEXT, context);
		}
		return context;
	}

	private class Context {
		private final IoBuffer innerBuffer;
		private final CharsetDecoder decoder;
		private String sms = "";
		private int matchCount = 0;


		public Context() {
			decoder = charset.newDecoder();
			innerBuffer = IoBuffer.allocate(100).setAutoExpand(true);
		}

		public CharsetDecoder getDecoder() {
			return decoder;
		}

		public int getMatchCount() {
			return matchCount;
		}

		public void setMatchCount(int matchCount) {
			this.matchCount = matchCount;
		}

		public void reset() {
			this.innerBuffer.clear();
			this.matchCount = 0;
			this.sms = "";
		}

		public IoBuffer getBuffer() {
			return innerBuffer;
		}

		public void append(IoBuffer in) {
			getBuffer().put(in);
		}
	}

}
