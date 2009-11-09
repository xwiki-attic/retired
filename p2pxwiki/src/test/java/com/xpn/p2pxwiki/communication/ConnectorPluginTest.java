package com.xpn.p2pxwiki.communication;

import junit.framework.TestCase;

import com.xpn.p2pxwiki.mocks.MockConnectionFactory;
import com.xpn.p2pxwiki.mocks.MockHandlerStub;
import com.xpn.p2pxwiki.mocks.MockNetwork;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiConfig;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;

public class ConnectorPluginTest extends TestCase {
	private ConnectorPlugin plugin;
	private XWikiContext xcontext;
	private XWiki xwiki;
    private XWikiConfig config;
    private String remotePeer;
	private MockNetwork network;
	public ConnectorPluginTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		remotePeer = "remotePeer";
		
		this.config = new XWikiConfig();
		this.config.put(ConnectorPlugin.NETWORK_TYPE, MockNetwork.class.getName());

		this.xcontext = new XWikiContext();
		this.xwiki = new XWiki(this.config, this.xcontext);
		this.xcontext.setWiki(this.xwiki);

		plugin = new ConnectorPlugin(ConnectorPlugin.PLUGIN_NAME, ConnectorPlugin.class.getName(), xcontext);
		
		// this won't happen in real life, but it's useful for tests
		network = MockNetwork.getInstance();
	}

	protected void tearDown() throws Exception {
		MockNetwork.resetInstance();
		MockConnectionFactory.resetInstance();
	}
	
	public void testGetInstance() {
		assertSame(plugin, ConnectorPlugin.getInstance());
	}

	public void testGetPluginApi() {
		ConnectorApi api = (ConnectorApi)plugin.getPluginApi(plugin, xcontext);
		assertNotNull(api);
	}
	
	public void getXWiki() {
		assertSame(xwiki, plugin.getXWiki());
	}

	public void testGetName() {
		assertEquals(ConnectorPlugin.PLUGIN_NAME, plugin.getName());
	}

	public void testGetConnectionCloseConnection() throws XWikiException {
		HandlerStub stub = plugin.getConnection(remotePeer);
		assertEquals(MockHandlerStub.class, stub.getClass());
		assertEquals(remotePeer, stub.getPeerName());
		assertFalse(((MockHandlerStub)stub).isClosed());
		plugin.closeConnection(stub);
		assertTrue(((MockHandlerStub)stub).isClosed());
	}
		
	public void testGetNames() {
		network.setLocalName("bubulici");
		network.setPeerName("bubuloi");
		assertEquals("bubuloi", plugin.getPeerName());
		assertEquals("bubulici", plugin.getCanonicalLocalName());
		assertEquals("bubulina", plugin.getCanonicalRemoteName("bubulina"));		
	}
}
