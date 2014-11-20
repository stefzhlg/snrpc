package org.stefan.snrpc.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.stefan.snrpc.log.Logger;
import org.stefan.snrpc.log.LoggerFactory;
import org.stefan.snrpc.util.StringUtil;

/**
 * 
 * XmlConfigureParse
 * <application>
 	<rpcServices>
		<rpcService name="SnRpcInterface" interface="org.stefan.snrpc.server.SnRpcInterface" overload="true">
           <rpcImplementor  class="org.stefan.snrpc.server.SnRpcImpl"/> 
		</rpcService>
	</rpcServices>
</application>
 * @author zhaoliangang 2014-11-12
 */
public class XmlConfigureParse implements ConfigureParse {

	private static final Logger logger = LoggerFactory
			.getLogger(XmlConfigureParse.class);

	private String configFile = null;
	private Document document = null;
	private Element root = null;

	/**
	 * @param configFile
	 * @param root
	 */
	public XmlConfigureParse(String configFile) {
		super();
		this.configFile = configFile;
		this.root = getRoot();
	}

	@SuppressWarnings("unchecked")
	private Element getRoot() {
		Document doc = getDocument();
		List<Element> list = doc.selectNodes("//application");
		if (list.size() > 0) {
			Element aroot = list.get(0);
			return aroot;
		}
		return null;
	}

	private Document getDocument() {
		InputStream is = getFileStream();
		try {
			if (document == null) {
				SAXReader sr = new SAXReader();
				sr.setValidation(false);
				if (is == null) {
					throw new RuntimeException("can not find config file..."
							+ configFile);
				}
				document = sr.read(is);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("get xml file failed");
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return document;
	}

	private InputStream getFileStream() {
		return getFileStream(configFile);
	}

	private InputStream getFileStream(String fileName) {
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(fileName);
		return is;
	}

	@SuppressWarnings("unchecked")
	public List<RpcService> parseService() {

		List<RpcService> slist = new ArrayList<RpcService>();
		Node serviceRoot = root.selectSingleNode("//rpcServices");
		List<Element> serviceList = serviceRoot.selectNodes("//rpcService");

		int i = 0;
		for (Element serviceNode : serviceList) {
			String name = serviceNode.attributeValue("name");// service name
			String interfaceStr = serviceNode.attributeValue("interface");
			String overloadStr = serviceNode.attributeValue("overload");

			if (StringUtil.isEmpty(name)) {
				logger.warn(configFile + ":a rpcservice's name is empty.");
				continue;
			}
			if (StringUtil.isEmpty(interfaceStr)) {
				logger.warn(configFile + ":rpcservice［" + name
						+ "］ has an empty interface configure.");
				continue;
			}
			Class<?> type = null;
			try {
				type = Class.forName(interfaceStr);
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage());
				throw new RuntimeException("can't find rpc Interface:"
						+ interfaceStr);
			}
			RpcService service = new RpcService("" + i, name);
			service.setTypeClass(type);

			if (StringUtil.isNotEmpty(overloadStr)
					&& "true".equals(overloadStr.trim())) {
				service.setOverload(true);
			}

			Element rpcImplementor = serviceNode.element("rpcImplementor");
			String processor = rpcImplementor.attributeValue("class");
			Class<?> providerClass = null;
			try {
				providerClass = Class.forName(processor);
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage());
				throw new RuntimeException("can't find rpcImplementor Class:"
						+ processor);
			}
			RpcImplementor sv = new RpcImplementor(providerClass);
			service.setRpcImplementor(sv);
			slist.add(service);
			i++;
		}
		return slist;
	}

}
