package org.stefan.snrpc.conf;

/**
 * RpcService 
 * <rpcServices>
 * 	 <rpcService name="SnRpcInterface"
 * 			interface="org.stefan.snrpc.server.SnRpcInterface" overload="true">
 *		 		<rpcImplementor class="org.stefan.snrpc.server.SnRpcImpl"/> 
 *		  </rpcService>
 * </rpcServices>
 * 
 * @author zhaoliangang 2014-11-12
 */
public class RpcService {

	protected Class<?> typeClass;
	protected String id;
	protected String name;
	private boolean overload = false;
	private RpcImplementor rpcImplementor;

	/**
	 * @param id
	 * @param name
	 */
	public RpcService(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	/**
	 * @return the typeClass
	 */
	public Class<?> getTypeClass() {
		return typeClass;
	}

	/**
	 * @param typeClass
	 *            the typeClass to set
	 */
	public void setTypeClass(Class<?> typeClass) {
		this.typeClass = typeClass;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the overload
	 */
	public boolean isOverload() {
		return overload;
	}

	/**
	 * @param overload
	 *            the overload to set
	 */
	public void setOverload(boolean overload) {
		this.overload = overload;
	}

	/**
	 * @return the rpcImplementor
	 */
	public RpcImplementor getRpcImplementor() {
		return rpcImplementor;
	}

	/**
	 * @param rpcImplementor
	 *            the rpcImplementor to set
	 */
	public void setRpcImplementor(RpcImplementor rpcImplementor) {
		this.rpcImplementor = rpcImplementor;
	}

}
