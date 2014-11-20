package org.stefan.snrpc.server;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.stefan.snrpc.conf.RpcService;
import org.stefan.snrpc.conf.SnRpcConfig;
import org.stefan.snrpc.log.Logger;
import org.stefan.snrpc.log.LoggerFactory;
import org.stefan.snrpc.serializer.SnRpcRequest;
import org.stefan.snrpc.serializer.SnRpcResponse;
import org.stefan.snrpc.util.ReflectionCache;

/**
 * SnNettyRpcServerHandler
 * 
 * @author zhaoliangang 2014-11-13
 */
public class SnNettyRpcServerHandler extends SimpleChannelUpstreamHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(SnNettyRpcServerHandler.class);

	private final Map<String, Object> handlersMap;

	private final static Map<String, RpcService> serviceMap = new HashMap<String, RpcService>();

	private final ChannelGroup channelGroups;

	public SnNettyRpcServerHandler(Map<String, Object> handlersMap) {
		this(handlersMap, null);
	}

	public SnNettyRpcServerHandler(Map<String, Object> handlersMap,
			ChannelGroup channelGroups) {
		this.handlersMap = handlersMap;
		this.channelGroups = channelGroups;
	}

	public static void putService(RpcService service) {
		if (null != service) {
			serviceMap.put(service.getName(), service);
		}
	}

	public static Map<String, RpcService> getServiceMap() {
		return Collections.unmodifiableMap(serviceMap);
	}

	public static RpcService getServiceByName(String serviceName) {
		return serviceMap.get(serviceName);
	}

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		if (null != channelGroups) {
			channelGroups.add(e.getChannel());
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		SnRpcRequest request = (SnRpcRequest) ctx.getAttachment();
		logger.warn("handle rpc request fail! request: <{}>",
				new Object[] { request }, e.getCause());
		e.getChannel().close().awaitUninterruptibly();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Object msg = e.getMessage();
		if (!(msg instanceof SnRpcRequest)) {
			return;
		}
		SnRpcRequest request = (SnRpcRequest) msg;
		ctx.setAttachment(request);

		SnRpcResponse response = new SnRpcResponse(request.getRequestID());
		try {
			Object result = handle(request);
			response.setResult(result);
		} catch (Throwable t) {
			logger.warn("handle rpc request fail! request: <{}>",
					new Object[] { request }, t);
			response.setException(t);
		}
		e.getChannel().write(response);
	}

	private Object handle(SnRpcRequest request) throws Throwable {
		if (SnRpcConfig.getInstance().getDevMod()) {
			StatisticsService.reportBeforeInvoke(request);
		}
		String className = request.getClassName();
		String[] classNameSplits = className.split("\\.");
		String serviceName = classNameSplits[classNameSplits.length - 1];
		RpcService rpcService = getServiceByName(serviceName);
		if (null == rpcService)
			throw new NullPointerException("server interface config is null");

		Class<?> clazz = rpcService.getRpcImplementor().getProcessorClass();
		Method method = ReflectionCache.getMethod(clazz.getName(),
				request.getMethodName(), request.getParameterTypes());
		Object[] parameters = request.getParameters();
		// get handler
		Object handler = handlersMap.get(request.getClassName());
		// invoke
		Object result = method.invoke(handler, parameters);
		return result;
	}

}
