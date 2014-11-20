package org.stefan.snrpc.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.stefan.snrpc.exception.SerializerException;

/**
 * ServerSerializer
 * @author zhaoliangang 2014-11-13
 */
public interface ServerSerializer {
	/**
	 * deserialize the inputStream
	 * 
	 * @param inputStream
	 * @return
	 * @throws SerializeException
	 * @throws IOException
	 */
	SnRpcRequest decodeRequest(InputStream inputStream)
			throws SerializerException, IOException;

	/**
	 * serialize the result object into the outputStream
	 * 
	 * @param outputStream
	 * @param result
	 * @throws SerializeException
	 * @throws IOException
	 */
	void encodeResponse(OutputStream outputStream, SnRpcResponse result)
			throws SerializerException, IOException;
}
