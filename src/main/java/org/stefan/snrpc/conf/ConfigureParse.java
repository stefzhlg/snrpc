package org.stefan.snrpc.conf;

import java.util.List;

/**
 * ConfigureParse
 * @author zhaoliangang 2014-11-12
 */
public interface ConfigureParse {

	List<RpcService> parseService();
}
