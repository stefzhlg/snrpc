package org.stefan.snrpc.serializer;

import org.stefan.snrpc.util.MessageFormatter;

/**
 * SnRpcResponse
 * 
 * @author zhaoliangang 2014-11-13
 */
public class SnRpcResponse {

	private String requestID;

	private Throwable exception;

	private Object result;

	public SnRpcResponse() {
	}

	public SnRpcResponse(String requestID) {
		this.requestID = requestID;
	}

	public String getRequestID() {
		return requestID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return MessageFormatter.format(
				"requestID: {}, result: {}, exception: {}", new Object[] {
						requestID, result, exception });
	}
}
