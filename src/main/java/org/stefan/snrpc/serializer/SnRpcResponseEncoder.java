package org.stefan.snrpc.serializer;

import java.io.ByteArrayOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * SnRpcResponseEncoder
 * @author zhaoliangang 2014-11-13
 */
public class SnRpcResponseEncoder extends SimpleChannelHandler {
	private final ServerSerializer serializer;

	public SnRpcResponseEncoder(ServerSerializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		SnRpcResponse response = (SnRpcResponse) e.getMessage();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(16384);
		serializer.encodeResponse(baos, response);
		ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(baos.toByteArray());
		Channels.write(ctx, e.getFuture(), buffer);
	}
}
