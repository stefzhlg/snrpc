package org.stefan.snrpc.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.stefan.snrpc.SnRpcConnection;
import org.stefan.snrpc.conf.SnRpcConfig;
import org.stefan.snrpc.serializer.ProtobufSerializer;
import org.stefan.snrpc.serializer.SnRpcRequest;
import org.stefan.snrpc.serializer.SnRpcRequestEncoder;
import org.stefan.snrpc.serializer.SnRpcResponse;
import org.stefan.snrpc.serializer.SnRpcResponseDecoder;

/**
 * Sn netty rpc connection
 * @author zhaoliangang 2014-11-13
 */
public class SnNettyRpcConnection extends SimpleChannelHandler implements
		SnRpcConnection {

	//inetsocket address
	private InetSocketAddress inetAddr;

	//org.jboss.netty.channel.Channel
	private volatile Channel channel;

	//response
	private volatile SnRpcResponse response;

	//exception
	private volatile Throwable exception;

	//HashedWheelTimer
	private volatile Timer timer;

	private boolean connected;

	private SnRpcConfig snRpcConfig = SnRpcConfig.getInstance();

	public SnNettyRpcConnection(String host, int port) {
		snRpcConfig.loadProperties("snrpcserver.properties");
		this.inetAddr = new InetSocketAddress(host, port);
		this.timer = new HashedWheelTimer();
	}

	public SnRpcResponse sendRequest(SnRpcRequest request) throws Throwable {
		if (!isConnected()) {
			throw new IllegalStateException("not connected");
		}
		ChannelFuture writeFuture = channel.write(request);
		if (!writeFuture.awaitUninterruptibly().isSuccess()) {
			close();
			throw writeFuture.getCause();
		}
		waitForResponse();

		Throwable ex = exception;
		SnRpcResponse resp = this.response;
		this.response = null;
		this.exception = null;

		if (null != ex) {
			close();
			throw ex;
		}
		return resp;
	}

	public void connection() throws Throwable {
		if (connected) {
			return;
		}
		ChannelFactory factory = new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		ClientBootstrap bootstrap = new ClientBootstrap(factory);

		bootstrap.setOption("tcpNoDelay", Boolean.parseBoolean(snRpcConfig
				.getProperty("snrpc.tcp.nodelay", "true")));
		bootstrap.setOption("reuseAddress", Boolean.parseBoolean(snRpcConfig
				.getProperty("snrpc.tcp.reuseAddress", "true")));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() {
				ChannelPipeline pipeline = Channels.pipeline();
				int readTimeout = snRpcConfig.getReadTimeout();
				if (readTimeout > 0) {
					pipeline.addLast("timeout", new ReadTimeoutHandler(timer,
							readTimeout, TimeUnit.MILLISECONDS));
				}
				pipeline.addLast("decoder", new SnRpcRequestEncoder(
						ProtobufSerializer.getInstance()));
				pipeline.addLast("aggregator", new HttpChunkAggregator(1024*1024));
				pipeline.addLast("encoder", new SnRpcResponseDecoder(
						ProtobufSerializer.getInstance()));
				pipeline.addLast("deflater", new HttpContentCompressor());
				pipeline.addLast("handler", SnNettyRpcConnection.this);
				return pipeline;
			}
		});

		ChannelFuture channelFuture = bootstrap.connect(inetAddr);
		if (!channelFuture.awaitUninterruptibly().isSuccess()) {
			bootstrap.releaseExternalResources();
			throw channelFuture.getCause();
		}
		channel = channelFuture.getChannel();
		connected = true;
	}

	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		response = (SnRpcResponse) e.getMessage();
		synchronized (channel) {
			channel.notifyAll();
		}
	}

	public void close() throws Throwable {
		connected = false;
		if (null != timer) {
			timer.stop();
			timer = null;
		}
		if (null != channel) {
			channel.close().awaitUninterruptibly();
			channel.getFactory().releaseExternalResources();

			this.exception = new IOException("connection closed");
			synchronized (channel) {
				channel.notifyAll();
			}
			channel = null;
		}
	}

	public void waitForResponse() {
		synchronized (channel) {
			try {
				channel.wait();
			} catch (InterruptedException e) {
			}
		}
	}

	public boolean isConnected() {
		return connected;
	}

	public boolean isClosed() {
		return (null == channel) || !channel.isConnected()
				|| !channel.isReadable() || !channel.isWritable();
	}

}
