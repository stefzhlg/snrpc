package org.stefan.snrpc.log;

/**
 * log level
 * 
 * @author zhaoliangang
 * 
 */
public enum Level {

	DEBUG, INFO, WARN, ERROR, FATAL;

	@Override
	public String toString() {
		return "{LogLevel:" + name() + "}";
	}

}
