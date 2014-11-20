package org.stefan.snrpc.serializer;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;

/**
 * ProtobufSerializer
 * @author zhaoliangang 2014-11-14
 */
public class ProtobufSerializer extends AbstractProtostuffSerializer {
	
	private static final ProtobufSerializer INSTANCE = new ProtobufSerializer();

	private ProtobufSerializer() {
	}

	public static ProtobufSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	protected <T> int writeObject(LinkedBuffer buffer, T object,
			Schema<T> schema) {
		return ProtobufIOUtil.writeTo(buffer, object, schema);
	}

	@Override
	protected <T> void parseObject(byte[] bytes, T template, Schema<T> schema) {
		ProtobufIOUtil.mergeFrom(bytes, template, schema);
	}
}
