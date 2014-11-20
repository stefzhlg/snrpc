package org.stefan.snrpc.serializer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * SnRpcRequestDecoder
 * 
 * @author zhaoliangang 2014-11-13
 */
public class SnRpcRequestDecoder extends FrameDecoder {

	private final ServerSerializer serializer;

	public SnRpcRequestDecoder(ServerSerializer serializer) {
		this.serializer = serializer;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {
		if (buffer.readableBytes() < 4) {
			return null;
		}
		int length = buffer.getInt(buffer.readerIndex());
		if (buffer.readableBytes() < length + 4) {
			return null;
		}
		ChannelBufferInputStream in = new ChannelBufferInputStream(buffer);
		SnRpcRequest request = serializer.decodeRequest(in);
		return request;
	}

}
