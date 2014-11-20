package org.stefan.snrpc.server;

import java.util.List;

import org.stefan.snrpc.conf.ConfigureParse;
import org.stefan.snrpc.conf.RpcService;
import org.stefan.snrpc.conf.SnRpcConfig;
import org.stefan.snrpc.conf.XmlConfigureParse;

/**
 * ParseXmlToService
 * 
 * @author zhaoliangang 2014-11-14
 */
public class ParseXmlToService {

	public void parse() {
		String configFile = SnRpcConfig.getInstance().getpropertiesFile();
		ConfigureParse parse = new XmlConfigureParse(configFile);
		List<RpcService> serviceList = parse.parseService();
		for (RpcService service : serviceList) {
			SnNettyRpcServerHandler.putService(service);
		}
	}
}
