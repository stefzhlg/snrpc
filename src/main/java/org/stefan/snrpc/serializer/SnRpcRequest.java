package org.stefan.snrpc.serializer;

import org.stefan.snrpc.util.MessageFormatter;

/**
 * SnRpcRequest
 * @author zhaoliangang 2014-11-13
 */
public class SnRpcRequest {

	private String requestID;

	private String className;

	private String methodName;

	private String[] parameterTypes;

	private Object[] parameters;

	public SnRpcRequest() {
	}

	public SnRpcRequest(String className, String methodName,
			String[] parameterTypes, Object[] parameters) {
		this.className = className;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.parameters = parameters;
	}

	public SnRpcRequest(String requestID, String className, String methodName,
			String[] parameterTypes, Object[] parameters) {
		this.requestID = requestID;
		this.className = className;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.parameters = parameters;
	}

	public String getRequestID() {
		return requestID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(String[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		return MessageFormatter
				.format("requestID: {}, className: {}, methodName: {}, parameterTypes: {}, parameters: {}",
						new Object[] { requestID, className, methodName,
								parameterTypes, parameters });
	}
}
