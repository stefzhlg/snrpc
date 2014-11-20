SNRPC 

--------------------
 -- a simple netty RPC framework
 use protostuff-1.07 for serializer,use netty-3.2.1 for nio.
 
 ##How to use
 
 e.g.
 
 1,server class; interface and implementor
		// define an interface:
		 public interface SnRpcInterface {
			public String getMessage(String param);
		}
		
		// implement interface
		public class SnRpcImpl implements SnRpcInterface {
			public String getMessage(String param) {
				return "hi,it is message from server...param+" + param;
			}
		}

2, start server 

		SnRpcInterface inter = new SnRpcImpl();
		SnRpcServer server = new SnNettyRpcServer(new Object[] { inter });
		try {
			server.start();
		} catch (Throwable e) {
			e.printStackTrace();
		}

3，config file

snrpcserver.properties
=========
	#tcpNoDelay 
	snrpc.tcp.nodelay=true
	#call the bind method as many times as you want
	snrpc.tcp.reuseAddress=true
	#ISDEBUG
	snrpc.dev=true
	#TCP timeout
	snrpc.read.timeout=25000
	#server port
	snrpc.http.port=8080
----------------------------------------------------------------------
config.xml
=========
	<?xml version="1.0" encoding="UTF-8"?>
	<application>
	 <!-- rpc interface services -->	
	 <rpcServices>
			<rpcService name="SnRpcInterface" interface="org.stefan.snrpc.server.SnRpcInterface" overload="true">
	           <rpcImplementor  class="org.stefan.snrpc.server.SnRpcImpl"/> 
			</rpcService>
		</rpcServices>
	</application>	

3, client invoker

		SnRpcConnectionFactory factory = new SnNettyRpcConnectionFactory(
					"localhost", 8080);
		factory = new PoolableRpcConnectionFactory(factory);
		SnRpcClient client = new CommonSnRpcClient(factory);
		try {
			SnRpcInterface clazz = client.proxy(SnRpcInterface.class);
			String message = clazz.getMessage("come on");
			System.out.println("client receive message .... : " + message);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
## Pre-requirement

* JDK6+
* Maven 2



# Dependency

* reflectasm-1.07.jar
* asm-4.0.jar
* log4j-1.2.16.jar
* dom4j-1.6.1.jar
* xml-apis-1.0.b2.jar 
* slf4j-api-1.6.6.jar
* netty-3.2.1.Final.jar
* jaxen-1.1.6.jar
* protostuff-core-1.0.7.jar
* protostuff-api-1.0.7.jar
* protostuff-runtime-1.0.7.jar
* protostuff-collectionschema-1.0.7.jar
* commons-pool-1.6.jar		
		