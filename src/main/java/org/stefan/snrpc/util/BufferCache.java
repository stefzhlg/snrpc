package org.stefan.snrpc.util;

import com.dyuproject.protostuff.LinkedBuffer;

/**
 * BufferCache
 * @author zhaoliangang 2014-11-13
 */
public class BufferCache {

	private static ThreadLocal<LinkedBuffer> BUFFERS = new ThreadLocal<LinkedBuffer>() {
		protected LinkedBuffer initialValue() {
			return LinkedBuffer.allocate(4096);
		};
	};

	public static LinkedBuffer getBuffer() {
		LinkedBuffer buffer = BUFFERS.get();
		buffer.clear();
		return buffer;
	}
}
