package org.stefan.snrpc;

/**
 * @author zhaoliangang 2014-11-13
 */
public interface SnRpcConnectionFactory {

	SnRpcConnection getConnection() throws Throwable;

	void recycle(SnRpcConnection connection) throws Throwable;
}
