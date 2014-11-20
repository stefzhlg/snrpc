package org.stefan.snrpc;

import org.stefan.snrpc.server.SnNettyRpcServer;
import org.stefan.snrpc.server.SnRpcImpl;
import org.stefan.snrpc.server.SnRpcInterface;

/**
 * @author zhaoliangang 2014-11-14
 */
public class Server {

	public static void main(String[] args) {

		SnRpcInterface inter = new SnRpcImpl();
		SnRpcServer server = new SnNettyRpcServer(new Object[] { inter });
		try {
			server.start();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
