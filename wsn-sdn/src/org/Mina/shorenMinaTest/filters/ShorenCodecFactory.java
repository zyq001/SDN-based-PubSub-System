/**
 * @author shoren
 * @date 2013-6-5
 */
package org.Mina.shorenMinaTest.filters;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;

/**
 *
 */
public class ShorenCodecFactory implements ProtocolCodecFactory {
	private final WsnMsgEncoder encoder;
	private final WsnMsgDecoder decoder;
	
	public ShorenCodecFactory() {
		this(Charset.defaultCharset());
	}
	
	public ShorenCodecFactory(Charset charSet) {
		this.encoder = new WsnMsgEncoder(charSet);
		this.decoder = new WsnMsgDecoder(charSet);
	}
	
	@Override
	public WsnMsgDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}
	
	@Override
	public WsnMsgEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

}
