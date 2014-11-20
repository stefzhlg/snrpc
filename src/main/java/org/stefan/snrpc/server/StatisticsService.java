package org.stefan.snrpc.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.stefan.snrpc.log.Logger;
import org.stefan.snrpc.log.LoggerFactory;
import org.stefan.snrpc.serializer.SnRpcRequest;

/**
 * StatisticsService
 * 
 * @author zhaoliangang 2014-11-13
 */
public class StatisticsService {

	private static final Logger logger = LoggerFactory
			.getLogger(StatisticsService.class);

	private static final AtomicLong requestTimes = new AtomicLong();
	private static final SimpleDateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	static final boolean reportBeforeInvoke(SnRpcRequest request) {
		doReport(request);
		return true;
	}

	private static final void doReport(SnRpcRequest request) {
		requestTimes.getAndIncrement();
		StringBuilder tip = new StringBuilder(
				"\nsnRpc request report -------- ").append(
				df.format(new Date())).append(
				" ------------------------------\n");
		String className = request.getClassName();
		String methodName = request.getMethodName();
		String requestId = request.getRequestID();
		Object[] param = request.getParameters();
		tip.append("requestId : ").append(requestId);
		tip.append("className : ").append(className);
		tip.append("method  : ").append(methodName);
		tip.append("param[0] : ").append(param[0]).append("\n");
		tip.append("--------------------------------------------------------------------------------\n");
		System.out.print(tip.toString());
	}

	static final void reportPerformance() {
		new Thread(new Runnable() {
			public void run() {
				final long begin = System.currentTimeMillis();
				while (true) {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
					}
					long pass = System.currentTimeMillis() - begin;
					long totalMemory = Runtime.getRuntime().totalMemory();
					long freeMemory = Runtime.getRuntime().freeMemory();
					long usedMemory = totalMemory - freeMemory;
					java.text.NumberFormat format = new java.text.DecimalFormat(
							"###,###");
					String memoryInfo = format.format(usedMemory) + "/"
							+ format.format(totalMemory);
					logger.warn("\r\nMemory:" + memoryInfo + ",Time:"
							+ df.format(new Date()) + ",Time passed：" + pass
							+ ",Total：" + requestTimes.get() + "（Average TPS："
							+ (requestTimes.get() * 1000 / pass) + ")");
				}
			}
		}).start();
	}
}
