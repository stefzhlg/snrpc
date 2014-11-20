package org.stefan.snrpc.conf;

import java.io.Serializable;

import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * RpcImplementor
 * @author zhaoliangang 2014-11-12
 */
public class RpcImplementor implements Serializable {

	private static final long serialVersionUID = 4679847970989376057L;

	private Class<?> processorClass;

	private MethodAccess methodAccess;

	public RpcImplementor() {
	}

	/**
	 * @return the processorClass
	 */
	public Class<?> getProcessorClass() {
		return processorClass;
	}

	/**
	 * @param processorClass
	 * @param methodAccess
	 */
	public RpcImplementor(Class<?> processorClass) {
		super();
		this.processorClass = processorClass;
		this.methodAccess = MethodAccess.get(processorClass);
	}

	/**
	 * @param processorClass
	 *            the processorClass to set
	 */
	public void setProcessorClass(Class<?> processorClass) {
		this.processorClass = processorClass;
	}

	/**
	 * @return the methodAccess
	 */
	public MethodAccess getMethodAccess() {
		return methodAccess;
	}

	/**
	 * @param methodAccess
	 *            the methodAccess to set
	 */
	public void setMethodAccess(MethodAccess methodAccess) {
		this.methodAccess = methodAccess;
	}

}
