package org.stefan.snrpc.util;

import org.stefan.snrpc.serializer.SnRpcRequest;
import org.stefan.snrpc.serializer.SnRpcResponse;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * SchemaCache
 * @author zhaoliangang
 *	2014-11-20
 */
public class SchemaCache {
	
	private static final LRUMap<String, Schema<?>> SCHEMA_CACHE = new LRUMap<String, Schema<?>>(
			4096);

	@SuppressWarnings("unchecked")
	public static <T> Schema<T> getSchema(Class<T> clazz) {
		String className = clazz.getName();
		Schema<T> schema = (Schema<T>) SCHEMA_CACHE.get(className);
		if (null != schema) {
			return schema;
		}
		synchronized (SCHEMA_CACHE) {
			if (null == SCHEMA_CACHE.get(className)) {
				schema = RuntimeSchema.getSchema(clazz);
				SCHEMA_CACHE.put(className, schema);
				return schema;
			} else {
				return (Schema<T>) SCHEMA_CACHE.get(className);
			}
		}
	}

	public static Schema<SnRpcRequest> getSchema(SnRpcRequest request) {
		Schema<SnRpcRequest> schema = getSchema(SnRpcRequest.class);
		Object[] parameters = request.getParameters();
		if (null != parameters && parameters.length > 0) {
			for (Object param : parameters) {
				if (null != param) {
					getSchema(param.getClass());
				}
			}
		}
		return schema;
	}

	public static Schema<SnRpcResponse> getSchema(SnRpcResponse response) {
		Schema<SnRpcResponse> schema = getSchema(SnRpcResponse.class);
		if (response.getException() != null) {
			getSchema(response.getException().getClass());
		}
		if (response.getResult() != null) {
			getSchema(response.getResult().getClass());
		}
		return schema;
	}
}
