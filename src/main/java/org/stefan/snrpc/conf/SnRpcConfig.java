package org.stefan.snrpc.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.stefan.snrpc.exception.SnRpcException;
import org.stefan.snrpc.log.Logger;
import org.stefan.snrpc.log.LoggerFactory;
import org.stefan.snrpc.util.StringUtil;

/**
 * SnRpcConfig
 * @author zhaoliangang 2014-11-13
 */
public class SnRpcConfig {

	private static Logger logger = LoggerFactory.getLogger(SnRpcConfig.class);

	private static SnRpcConfig snRpcConfig;

	private static Properties properties = new Properties();

	private SnRpcConfig() {
	}

	public static SnRpcConfig getInstance() {
		if (snRpcConfig == null)
			snRpcConfig = new SnRpcConfig();
		return snRpcConfig;
	}

	public void loadProperties(String fileName) {
		if (StringUtil.isEmpty(fileName))
			throw new SnRpcException("snRpcConfig name is null...");
		InputStream inputStream = null;
		try {
			inputStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(fileName);
			properties.load(inputStream);
		} catch (IOException e) {
			throw new SnRpcException(" snRpcConfig file load failed... "
					+ fileName);
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		if (properties == null)
			throw new RuntimeException("Properties file loading failed: "
					+ fileName);
	}

	public Properties getProperties() {
		return properties;
	}

	public String getProperty(String key) {
		return properties.getProperty(key).trim();
	}

	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue.trim());
	}

	/**
	 * get the service properties file,default is config.xml
	 * 
	 * @return
	 */
	public String getpropertiesFile() {
		String f = properties.getProperty("properties.file", "config.xml");
		return f.trim();
	}

	public boolean getDevMod() {
		String dev = properties.getProperty("snrpc.dev", "false");
		return Boolean.parseBoolean(dev);
	}

	/**
	 * get the method's invoke timeout,default is 3s
	 * 
	 * @return
	 */
	public int getReadTimeout() {
		String timeOutStr = properties
				.getProperty("snrpc.read.timeout", "3000");
		return Integer.parseInt(timeOutStr);
	}

	/**
	 * get the server's HTTP port,default is -1
	 * 
	 * @return
	 */
	public int getHttpPort() {
		String port = properties.getProperty("snrpc.http.port", "-1");
		return Integer.parseInt(port);
	}

}
