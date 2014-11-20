package org.stefan.snrpc.serializer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * SnRpcResponseDecoder
 * @author zhaoliangang 2014-11-13
 */
public class SnRpcResponseDecoder extends FrameDecoder {

	private final ClientSerializer serializer;

	public SnRpcResponseDecoder(ClientSerializer serializer) {
		this.serializer = serializer;
	}

	@Override
	protected Object decode(ChannelHandlerContext context, Channel channel,
			ChannelBuffer buffer) throws Exception {
		if (buffer.readableBytes() < 4) {
			return null;
		}
		int length = buffer.getInt(buffer.readerIndex());
		if (buffer.readableBytes() < length + 4) {
			return null;
		}
		ChannelBufferInputStream in = new ChannelBufferInputStream(buffer);
		SnRpcResponse response = serializer.decodeResponse(in);
		return response;
	}
}
