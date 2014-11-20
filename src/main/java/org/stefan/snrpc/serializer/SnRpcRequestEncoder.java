package org.stefan.snrpc.serializer;

import java.io.ByteArrayOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * @author zhaoliangang 2014-11-13
 */
public class SnRpcRequestEncoder extends SimpleChannelHandler {

	private final ClientSerializer serializer;

	public SnRpcRequestEncoder(ClientSerializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		SnRpcRequest request = (SnRpcRequest) e.getMessage();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(16384);
		serializer.encodeRequest(baos, request);
		ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(baos.toByteArray());
		Channels.write(ctx, e.getFuture(), buffer);
	}

}
