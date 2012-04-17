package com.pellcorp.android.transact.sshtunnel;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * This class is really only for debugging!
 */
public class Shell extends Tunnel {
	public Shell(final TunnelConfig tunnelConfig) throws JSchException {
		super(tunnelConfig);
	}
	
	public String getShellLoginMessage() throws JSchException, UnsupportedEncodingException {
		Session session = init();
	
		Channel channel = session.openChannel("shell");
	
		channel.setInputStream(System.in);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		channel.setOutputStream(outputStream);
	
		channel.connect();
		
		// let the session connect
		try {
			Thread.sleep(2000);
		} catch(Exception e) {}
		
		return new String(outputStream.toByteArray(), "UTF-8");
	}
}
