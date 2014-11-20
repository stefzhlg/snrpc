package org.stefan.snrpc.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.stefan.snrpc.exception.SerializerException;

/**
 * ClientSerializer
 * @author zhaoliangang 2014-11-13
 */
public interface ClientSerializer {
	/**
	 * deserialize the inputStream
	 * 
	 * @param inputStream
	 * @return
	 * @throws SerializeException
	 * @throws IOException
	 */
	SnRpcResponse decodeResponse(InputStream inputStream)
			throws SerializerException, IOException;

	/**
	 * serialize the request object into the outputStream
	 * 
	 * @param outputStream
	 * @param object
	 * @param method
	 * @param arguments
	 * @throws SerializeException
	 * @throws IOException
	 */
	void encodeRequest(OutputStream outputStream, SnRpcRequest request)
			throws SerializerException, IOException;
}