package org.stefan.snrpc;

import org.stefan.snrpc.serializer.SnRpcRequest;
import org.stefan.snrpc.serializer.SnRpcResponse;

/**
 * @author zhaoliangang 2014-11-13
 */
public interface SnRpcConnection {

	SnRpcResponse sendRequest(SnRpcRequest request) throws Throwable;

	void connection() throws Throwable;

	void close() throws Throwable;

	boolean isConnected();

	boolean isClosed();
}
