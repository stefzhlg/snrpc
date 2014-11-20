package org.stefan.snrpc;

/**
 * @author zhaoliangang 2014-11-13
 */
public interface SnRpcServer {

	void start() throws Throwable;

	void stop() throws Throwable;
}
