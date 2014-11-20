package org.stefan.snrpc.server;

/**
 * @author zhaoliangang 2014-11-14
 */
public class SnRpcImpl implements SnRpcInterface {

	public String getMessage(String param) {
		return "hi,it is message from server...param+" + param;
	}

}
