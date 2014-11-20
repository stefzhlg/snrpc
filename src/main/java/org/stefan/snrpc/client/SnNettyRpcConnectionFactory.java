package org.stefan.snrpc.client;

import java.net.InetSocketAddress;

import org.stefan.snrpc.SnRpcConnection;
import org.stefan.snrpc.SnRpcConnectionFactory;

/**
 * SnNettyRpcConnectionFactory
 * @author zhaoliangang 2014-11-13
 */
public class SnNettyRpcConnectionFactory implements SnRpcConnectionFactory {
	private InetSocketAddress serverAddr;

	public SnNettyRpcConnectionFactory(String host, int port) {
		this.serverAddr = new InetSocketAddress(host, port);
	}

	public SnRpcConnection getConnection() throws Throwable {
		return new SnNettyRpcConnection(this.serverAddr.getHostName(),
				this.serverAddr.getPort());
	}

	public void recycle(SnRpcConnection connection) throws Throwable {
		if (null != connection) {
			connection.close();
		}
	}

}
