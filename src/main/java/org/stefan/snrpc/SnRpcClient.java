package org.stefan.snrpc;

/**
 * @author zhaoliangang 2014-11-13
 */
public interface SnRpcClient {

	public <T> T proxy(Class<T> interfaceClass) throws Throwable;
}
