package org.apache.servicemix.application;

import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * A helper class which provides a JAXP {@link javax.xml.transform.Source} from a String which can
 * be read as many times as required.
 *
 * @version $Revision: 564607 $
 */
public class StringSource extends StreamSource implements Serializable {

	private final String text;

	private String encoding = "UTF-8";

	public StringSource(String text) {
		if (text == null) {
			throw new NullPointerException("text can not be null");
		}
		this.text = text;
	}

	public StringSource(String text, String systemId) {
		this(text);
		setSystemId(systemId);
	}

	public StringSource(String text, String systemId, String encoding) {
		this.text = text;
		this.encoding = encoding;
		setSystemId(systemId);
	}

	public InputStream getInputStream() {
		try {
			return new ByteArrayInputStream(text.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public Reader getReader() {
		return new StringReader(text);
	}

	public String toString() {
		return "StringSource[" + text + "]";
	}

	public String getText() {
		return text;
	}

}