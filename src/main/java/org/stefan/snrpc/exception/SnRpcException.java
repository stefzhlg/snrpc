package org.stefan.snrpc.exception;

/**
 * SnRpcException
 * 
 * @author zhaoliangang
 * 
 */
public class SnRpcException extends RuntimeException {

	private static final long serialVersionUID = 6443147893553933129L;

	public SnRpcException() {
		super();
	}

	public SnRpcException(String msg) {
		super(msg);
	}

	public SnRpcException(Throwable t) {
		super(t);
	}

	public SnRpcException(String msg, Throwable t) {
		super(msg, t);
	}
}
