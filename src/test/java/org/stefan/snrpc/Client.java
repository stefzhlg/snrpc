package org.stefan.snrpc;

import org.stefan.snrpc.client.CommonSnRpcClient;
import org.stefan.snrpc.client.PoolableRpcConnectionFactory;
import org.stefan.snrpc.client.SnNettyRpcConnectionFactory;
import org.stefan.snrpc.server.SnRpcInterface;

/**
 * @author zhaoliangang 2014-11-14
 */
public class Client {

	public static void main(String[] args) {
		SnRpcConnectionFactory factory = new SnNettyRpcConnectionFactory(
				"localhost", 8080);
		factory = new PoolableRpcConnectionFactory(factory);
		SnRpcClient client = new CommonSnRpcClient(factory);
		try {
			SnRpcInterface clazz = client.proxy(SnRpcInterface.class);
			String message = clazz.getMessage("come on");
			System.out.println("client receive message .... : " + message);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
