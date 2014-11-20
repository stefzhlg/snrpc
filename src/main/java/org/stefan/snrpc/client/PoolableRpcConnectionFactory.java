package org.stefan.snrpc.client;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.stefan.snrpc.SnRpcConnection;
import org.stefan.snrpc.SnRpcConnectionFactory;

/**
 * connection poolable
 * 
 * @author zhaoliangang 2014-11-14
 */
public class PoolableRpcConnectionFactory implements SnRpcConnectionFactory,
		PoolableObjectFactory<SnRpcConnection> {
	//connectionFactory
	private SnRpcConnectionFactory connectionFactory;

	//org.apache.commons.pool
	private GenericObjectPool<SnRpcConnection> pool = new GenericObjectPool<SnRpcConnection>(
			this);

	public PoolableRpcConnectionFactory(SnRpcConnectionFactory factory) {
		if (null == factory) {
			throw new NullPointerException("factory");
		}
		this.connectionFactory = factory;
	}

	/* get Connection
	 * @see org.stefan.snrpc.SnRpcConnectionFactory#getConnection()
	 */
	public SnRpcConnection getConnection() throws Throwable {
		return pool.borrowObject();
	}

	/* recycle connection pool
	 * @see org.stefan.snrpc.SnRpcConnectionFactory#recycle(org.stefan.snrpc.SnRpcConnection)
	 */
	public void recycle(SnRpcConnection connection) throws Throwable {
		if (null != connection) {
			pool.returnObject(connection);
		}
	}

	/**
	 * destroy connection  pool
	 * @throws Throwable
	 */
	public void destroy() throws Throwable {
		pool.close();
	}

	/* activate connection
	 * @see org.apache.commons.pool.PoolableObjectFactory#activateObject(java.lang.Object)
	 */
	public void activateObject(SnRpcConnection connection) throws Exception {
		try {
			connection.connection();
		} catch (Throwable e) {
			throw new Exception(e);
		}
	}

	/*destroy connection
	 * @see org.apache.commons.pool.PoolableObjectFactory#destroyObject(java.lang.Object)
	 */
	public void destroyObject(SnRpcConnection connection) throws Exception {
		try {
			connection.close();
		} catch (Throwable e) {
			throw new Exception(e);
		}
	}

	/* make connection
	 * @see org.apache.commons.pool.PoolableObjectFactory#makeObject()
	 */
	public SnRpcConnection makeObject() throws Exception {
		try {
			return connectionFactory.getConnection();
		} catch (Throwable e) {
			throw new Exception(e);
		}
	}

	/* passivateconnection
	 * @see org.apache.commons.pool.PoolableObjectFactory#passivateObject(java.lang.Object)
	 */
	public void passivateObject(SnRpcConnection connection) throws Exception {
	}

	/* validate connection
	 * @see org.apache.commons.pool.PoolableObjectFactory#validateObject(java.lang.Object)
	 */
	public boolean validateObject(SnRpcConnection connection) {
		return connection.isConnected() && !connection.isClosed();
	}

	public void setLifo(boolean lifo) {
		pool.setLifo(lifo);
	}

	public void setMaxActive(int maxActive) {
		pool.setMaxActive(maxActive);
	}

	public void setMaxIdle(int maxIdle) {
		pool.setMaxIdle(maxIdle);
	}

	public void setMaxWait(long maxWait) {
		pool.setMaxWait(maxWait);
	}

	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		pool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
	}

	public void setMinIdle(int minIdle) {
		pool.setMinIdle(minIdle);
	}

	public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
		pool.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
	}

	public void setSoftMinEvictableIdleTimeMillis(
			long softMinEvictableIdleTimeMillis) {
		pool.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		pool.setTestOnBorrow(testOnBorrow);
	}

	public void setTestOnReturn(boolean testOnReturn) {
		pool.setTestOnReturn(testOnReturn);
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		pool.setTestWhileIdle(testWhileIdle);
	}

	public void setTimeBetweenEvictionRunsMillis(
			long timeBetweenEvictionRunsMillis) {
		pool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
	}

	public void setWhenExhaustedAction(byte whenExhaustedAction) {
		pool.setWhenExhaustedAction(whenExhaustedAction);
	}
}
