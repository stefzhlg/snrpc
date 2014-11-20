package org.stefan.snrpc.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.stefan.snrpc.SnRpcServer;
import org.stefan.snrpc.conf.SnRpcConfig;
import org.stefan.snrpc.log.Logger;
import org.stefan.snrpc.log.LoggerFactory;
import org.stefan.snrpc.serializer.ProtobufSerializer;
import org.stefan.snrpc.serializer.SnRpcRequestDecoder;
import org.stefan.snrpc.serializer.SnRpcResponseEncoder;
import org.stefan.snrpc.util.HandlerMapper;

/**
 * 
 * SnNettyRpcServer
 * 
 * @author zhaoliangang 2014-11-13
 */
public class SnNettyRpcServer implements SnRpcServer {

	private static Logger logger = LoggerFactory
			.getLogger(SnNettyRpcServer.class);

	protected Map<String, Object> handlersMap;

	private ServerBootstrap bootstrap = null;
	private AtomicBoolean stopped = new AtomicBoolean(false);
	private SnRpcConfig snRpcConfig = SnRpcConfig.getInstance();
	private Timer timer;
	private int httpListenPort;

	public SnNettyRpcServer(Object... handlers) {
		snRpcConfig.loadProperties("snrpcserver.properties");
		this.handlersMap = HandlerMapper.getHandlerMap(handlers);
	}

	public SnNettyRpcServer(String fileName, Object... handlers) {
		snRpcConfig.loadProperties(fileName);
		this.handlersMap = HandlerMapper.getHandlerMap(handlers);
	}

	private void initServerInfo() {
		httpListenPort = snRpcConfig.getHttpPort();
		new ParseXmlToService().parse();
	}

	private void initHttpBootstrap() {
		if (SnRpcConfig.getInstance().getDevMod()) {
			StatisticsService.reportPerformance();
		}
		logger.info("init HTTP Bootstrap...........");
		final ChannelGroup channelGroup = new DefaultChannelGroup(getClass()
				.getName());
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));

		bootstrap.setOption("tcpNoDelay", Boolean.parseBoolean(snRpcConfig
				.getProperty("snrpc.tcp.nodelay", "true")));
		bootstrap.setOption("reuseAddress", Boolean.parseBoolean(snRpcConfig
				.getProperty("snrpc.tcp.reuseaddress", "true")));

		timer = new HashedWheelTimer();
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();

				int readTimeout = snRpcConfig.getReadTimeout();
				if (readTimeout > 0) {
					pipeline.addLast("timeout", new ReadTimeoutHandler(timer,
							readTimeout, TimeUnit.MILLISECONDS));
				}

				pipeline.addLast("decoder", new SnRpcRequestDecoder(
						ProtobufSerializer.getInstance()));
				pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
				pipeline.addLast("encoder", new SnRpcResponseEncoder(
						ProtobufSerializer.getInstance()));
				pipeline.addLast("deflater", new HttpContentCompressor());
				pipeline.addLast("handler", new SnNettyRpcServerHandler(
						handlersMap, channelGroup));
				return pipeline;
			}
		});

		if (!checkPortConfig(httpListenPort)) {
			throw new IllegalStateException("port: " + httpListenPort
					+ " already in use!");
		}

		Channel channel = bootstrap.bind(new InetSocketAddress(httpListenPort));
		channelGroup.add(channel);
		logger.info("snrpc server started");

		waitForShutdownCommand();
		ChannelGroupFuture future = channelGroup.close();
		future.awaitUninterruptibly();
		bootstrap.releaseExternalResources();
		timer.stop();
		timer = null;

		logger.info("snrpc server stoped");

	}

	public void start() {
		initServerInfo();
		initHttpBootstrap();
	}

	public void stop() throws Throwable {
		stopped.set(true);
		synchronized (stopped) {
			stopped.notifyAll();
		}
	}

	private void waitForShutdownCommand() {
		synchronized (stopped) {
			while (!stopped.get()) {
				try {
					stopped.wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private boolean checkPortConfig(int listenPort) {
		if (listenPort < 0 || listenPort > 65536) {
			throw new IllegalArgumentException("Invalid start port: "
					+ listenPort);
		}
		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(listenPort);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(listenPort);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
				}
			}
		}
		return false;
	}

}
