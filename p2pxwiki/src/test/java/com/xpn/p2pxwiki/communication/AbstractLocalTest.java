package com.xpn.p2pxwiki.communication;

import junit.framework.TestCase;

import org.p2psockets.P2PNetwork;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiConfig;
import com.xpn.xwiki.XWikiContext;

abstract public class AbstractLocalTest extends TestCase {
	
	private String localPeer;
	private String remotePeer;
	private int port;
	private XWikiConfig config;
	private XWikiContext xcontext;
	private XWiki xwiki;
	private ConnectorPlugin plugin;
	private HandlerStub stub;

	public AbstractLocalTest(String arg0) {
		super(arg0);
	}
	
	public abstract String getLocalPeer();
	public abstract String getRemotePeer();
	public abstract String getNetwork();
	public abstract String getConnectionFactory();
	
	public void setUp() throws Exception {
		localPeer = getLocalPeer();
		remotePeer = getRemotePeer();
		port = 9099;
		
		config = new XWikiConfig();
		config.put("xwiki.store.cache", "0");
		config.put(AbstractNetwork.PEER_NAME, localPeer);
		config.put(AbstractNetwork.SERVER_PORT, (new Integer(port)).toString());
		config.put(ConnectorPlugin.NETWORK_TYPE, getNetwork());
		config.put(AbstractNetwork.CONNECTOR_FACTORY, getConnectionFactory());

		xcontext = new XWikiContext();
		xwiki = new XWiki(config, xcontext);
		xcontext.setWiki(xwiki);

		plugin = new ConnectorPlugin(ConnectorPlugin.PLUGIN_NAME, ConnectorPlugin.class.getName(), xcontext);
	}

	public void tearDown() throws Exception {
		plugin.closeConnection(stub);
		plugin.cleanup();
		try {
			P2PNetwork.signOff();
		} catch (RuntimeException ignore) {
			
		}
	}

	public void test() throws P2PXWikiException {
		stub = plugin.getConnection(remotePeer);
		assertEquals(remotePeer, stub.getPeerName());
		assertEquals("hello world", stub.execute("hello_handler.hello", new Object[] {"world"}));
	}
	
//	public void testDynamicProxies() throws P2PXWikiException {
//		stub = plugin.getConnection(remotePeer);
//		Hello hello = (Hello)stub.getDynamicProxy("hello_handler", Hello.class, true);
//		assertEquals("hello world", hello.hello("world"));
//		assertTrue(hello.toString().startsWith("com.xpn.p2pxwiki.communication.AbstractHandlerStub"));
//	}
//	
//	public void testInitializableHandlers() throws P2PXWikiException {
//		stub = plugin.getConnection(remotePeer);
//		assertEquals(remotePeer, stub.getPeerName());
//		assertEquals(new Boolean(true), stub.execute("hello_init.isInitialized", new Object[] {}));
//		assertEquals("hello world", stub.execute("hello_init.hello", new Object[] {"world"}));
//	}
}
