package org.stefan.snrpc.exception;

/**
 * SerializerException
 * 
 * @author zhaoliangang
 * 
 */
public class SerializerException extends RuntimeException {

	private static final long serialVersionUID = -6831220895401658422L;

	public SerializerException() {
		super();
	}

	public SerializerException(String msg) {
		super(msg);
	}

	public SerializerException(Throwable t) {
		super(t);
	}

	public SerializerException(String msg, Throwable t) {
		super(msg, t);
	}
}
