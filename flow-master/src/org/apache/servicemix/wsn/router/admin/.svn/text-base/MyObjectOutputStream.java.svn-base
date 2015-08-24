package org.apache.servicemix.wsn.router.admin;
/**
 * 重写socket的ObjectOutputStream类，以解决同一线程中多个ObjectOutputStream造成的
 * .java.io.StreamCorruptedException: invalid type code: AC错误
 * 解决方法是，当上一stream未关闭时，新发的socket不加header
 * 
 * */
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class MyObjectOutputStream extends ObjectOutputStream {

	protected MyObjectOutputStream() throws IOException, SecurityException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public MyObjectOutputStream(OutputStream out) throws IOException {
		  super(out);
		  } 
		@Override 

		protected void writeStreamHeader() throws IOException { 
	   return;
		  }

}
