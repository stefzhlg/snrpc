package org.stefan.snrpc.log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zhaoliangang 2014-11-12
 */
public class LoggerFactory {

	private static LoggerFactory instance = null;
	private final ConcurrentMap<String, Logger> instances;
	private Constructor<? extends Logger> instanceConstructor;

	private LoggerFactory() {
		super();
		instances = new ConcurrentHashMap<String, Logger>();
	}

	private static void init() {
		if (instance == null)
			instance = new LoggerFactory();
	}

	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	public static Logger getLogger(String name) {
		if (name == null)
			throw new NullPointerException("Logger name can not be null...");

		init();
		return instance.internalGetLogger(name);
	}

	private Logger internalGetLogger(String name) {
		assert name != null : "logger name is null";
		Logger logger = instances.get(name);
		if (logger == null) {
			Logger newLogger = null;
			try {
				newLogger = getNewInstance(name);
			} catch (Exception e) {
				throw new RuntimeException("Problem getting logger", e);
			}
			Logger tmp = instances.putIfAbsent(name, newLogger);
			logger = tmp == null ? newLogger : tmp;
		}

		return logger;
	}

	/**
	 * @param name
	 * @return
	 */
	private Logger getNewInstance(String name) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		if (instanceConstructor == null) {
			getConstructor();
		}
		Object[] args = { name };
		Logger logger = instanceConstructor.newInstance(args);
		return logger;
	}

	// Find the appropriate constructor
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getConstructor() {
		Class<? extends Logger> c = DefaultLogger.class;
		String className = System.getProperty("org.stefan.log.LoggerImpl");

		if (className != null) {
			try {
				c = (Class<? extends Logger>) Class.forName(className);
			} catch (NoClassDefFoundError e) {
				System.err.println("Warning:  " + className
						+ " not found while initializing"
						+ " org.stefan.snrpc.log.LoggerFactory");
				e.printStackTrace();
				c = DefaultLogger.class;
			} catch (ClassNotFoundException e) {
				System.err.println("Warning:  " + className
						+ " not found while initializing"
						+ " org.stefan.snrpc.log.LoggerFactory");
				e.printStackTrace();
				c = DefaultLogger.class;
			}
		}

		// Find the best constructor
		try {
			// Try to find a constructor that takes a single string
			Class[] args = { String.class };
			instanceConstructor = c.getConstructor(args);
		} catch (NoSuchMethodException e) {
			try {
				// Try to find an empty constructor
				Class[] args = {};
				instanceConstructor = c.getConstructor(args);
			} catch (NoSuchMethodException e2) {
				System.err.println("Warning:  " + className
						+ " has no appropriate constructor, using defaults.");

				// Try to find a constructor that takes a single string
				try {
					Class[] args = { String.class };
					instanceConstructor = DefaultLogger.class
							.getConstructor(args);
				} catch (NoSuchMethodException e3) {
					// This shouldn't happen.
					throw new NoSuchMethodError(
							"There used to be a constructor that takes a single "
									+ "String on " + DefaultLogger.class
									+ ", but I can't " + "find one now.");
				} // SOL
			} // No empty constructor
		} // No constructor that takes a string
	} // getConstructor

}
