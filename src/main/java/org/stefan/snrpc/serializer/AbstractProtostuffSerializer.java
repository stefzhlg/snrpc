package org.stefan.snrpc.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.stefan.snrpc.exception.SerializerException;
import org.stefan.snrpc.util.BufferCache;
import org.stefan.snrpc.util.IOUtils;
import org.stefan.snrpc.util.SchemaCache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.Schema;

/**
 * AbstractProtostuffSerializer
 * @author zhaoliangang 2014-11-14
 */
public abstract class AbstractProtostuffSerializer implements ClientSerializer,
		ServerSerializer {
	/**
	 * @param buffer
	 *            buffer writen to
	 * @param object
	 * @param schema
	 * @return length
	 */
	protected abstract <T> int writeObject(LinkedBuffer buffer, T object,
			Schema<T> schema);

	/**
	 * @param bytes
	 * @param template
	 * @param schema
	 * @return
	 */
	protected abstract <T> void parseObject(byte[] bytes, T template,
			Schema<T> schema);

	public SnRpcRequest decodeRequest(InputStream inputStream)
			throws SerializerException, IOException {
		return decode(inputStream, new SnRpcRequest());
	}

	public void encodeResponse(OutputStream outputStream, SnRpcResponse result)
			throws SerializerException, IOException {
		encode(outputStream, result);
	}

	public SnRpcResponse decodeResponse(InputStream inputStream)
			throws SerializerException, IOException {
		return decode(inputStream, new SnRpcResponse());
	}

	public void encodeRequest(OutputStream outputStream, SnRpcRequest request)
			throws SerializerException, IOException {
		encode(outputStream, request);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> void encode(OutputStream out, T object) throws IOException {
		LinkedBuffer buffer = BufferCache.getBuffer();
		Schema schema = null;
		if (null == object) {
			schema = SchemaCache.getSchema(Object.class);
		} else {
			schema = SchemaCache.getSchema(object.getClass());
		}

		// write the length header
		int length = writeObject(buffer, object, schema);
		IOUtils.writeInt(out, length);
		// write content
		LinkedBuffer.writeTo(out, buffer);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> T decode(InputStream in, T template) throws IOException {
		Schema schema = SchemaCache.getSchema(template.getClass());

		// read the length header
		int length = IOUtils.readInt(in);
		// read exactly $length bytes
		byte[] bytes = new byte[length];
		IOUtils.readFully(in, bytes, 0, length);
		// parse object
		parseObject(bytes, template, schema);
		return template;
	}
}
