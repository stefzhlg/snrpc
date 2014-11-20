package org.stefan.snrpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;

import org.stefan.snrpc.SnRpcClient;
import org.stefan.snrpc.SnRpcConnection;
import org.stefan.snrpc.SnRpcConnectionFactory;
import org.stefan.snrpc.log.Logger;
import org.stefan.snrpc.log.LoggerFactory;
import org.stefan.snrpc.serializer.SnRpcRequest;
import org.stefan.snrpc.serializer.SnRpcResponse;
import org.stefan.snrpc.util.Sequence;

/**
 * rpc client
 * @author zhaoliangang 2014-11-13
 */
public class CommonSnRpcClient implements SnRpcClient {

	private static final Logger logger = LoggerFactory
			.getLogger(CommonSnRpcClient.class);

	private SnRpcConnectionFactory connectionFactory;

	private SnRpcConnection connection;

	private SnRpcInvoker invoker = new SnRpcInvoker();

	/**
	 * @param connectionFactory
	 */
	public CommonSnRpcClient(SnRpcConnectionFactory connectionFactory) {
		if (null == connectionFactory)
			throw new NullPointerException("connectionFactory is null...");
		this.connectionFactory = connectionFactory;
	}

	/**
	 * @param connection
	 */
	public CommonSnRpcClient(SnRpcConnection connection) {
		if (null == connection)
			throw new NullPointerException("connection is null...");
		this.connection = connection;
	}

	/**
	 * destroy
	 * @throws Throwable
	 */
	public void destroy() throws Throwable {
		if (null != connection) {
			connection.close();
		}
	}

	
	/**
	 * generate  requestID
	 * @return
	 */
	protected String generateRequestID() {
		return Sequence.next()+"";
	}

	/**
	 * recycle
	 * @param connection
	 */
	private void recycle(SnRpcConnection connection) {
		if (null != connection && null != connectionFactory) {
			try {
				connectionFactory.recycle(connection);
			} catch (Throwable t) {
				logger.warn("recycle rpc connection fail!", t);
			}
		}
	}

	/**
	 * get connection
	 * @return
	 * @throws Throwable
	 */
	private SnRpcConnection getConnection() throws Throwable {
		if (null != connection) {
			if (!connection.isConnected()) {
				connection.connection();
			}
			return connection;
		} else {
			return connectionFactory.getConnection();
		}
	}

	/* 
	 * proxy
	 */
	@SuppressWarnings("unchecked")
	public <T> T proxy(Class<T> interfaceClass) throws Throwable {
		if (!interfaceClass.isInterface()) {
			throw new IllegalArgumentException(interfaceClass.getName()
					+ " is not an interface");
		}
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
				new Class<?>[] { interfaceClass }, invoker);
	}

	
	/**
	 * invoker
	 */
	private class SnRpcInvoker implements InvocationHandler {
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			String className = method.getDeclaringClass().getName();
			List<String> parameterTypes = new LinkedList<String>();
			for (Class<?> parameterType : method.getParameterTypes()) {
				parameterTypes.add(parameterType.getName());
			}

			String requestID = generateRequestID();
			SnRpcRequest request = new SnRpcRequest(requestID, className,
					method.getName(), parameterTypes.toArray(new String[0]),
					args);
			SnRpcConnection connection = null;
			SnRpcResponse response = null;
			try {
				connection = getConnection();
				response = connection.sendRequest(request);
			} catch (Throwable t) {
				logger.warn("send rpc request fail! request: <{}>",
						new Object[] { request }, t);
				throw new RuntimeException(t);
			} finally {
				recycle(connection);
			}

			if (response.getException() != null) {
				throw response.getException();
			} else {
				return response.getResult();
			}
		}
	}
}
