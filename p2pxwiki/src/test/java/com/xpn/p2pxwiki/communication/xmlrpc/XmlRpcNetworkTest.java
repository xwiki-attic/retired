package com.xpn.p2pxwiki.communication.xmlrpc;

import junit.framework.TestCase;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.communication.AbstractNetwork;
import com.xpn.p2pxwiki.communication.ConnectionFactory;
import com.xpn.p2pxwiki.communication.ConnectorPlugin;
import com.xpn.p2pxwiki.communication.Network;
import com.xpn.p2pxwiki.mocks.MockConnectionFactory;
import com.xpn.p2pxwiki.mocks.MockNetwork;
import com.xpn.p2pxwiki.utils.P2PUtil;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiConfig;
import com.xpn.xwiki.XWikiContext;

public class XmlRpcNetworkTest extends TestCase {

	protected XWikiConfig config;
	protected XWikiContext xcontext;
	protected XWiki xwiki;
	protected ConnectorPlugin plugin;
	protected XmlRpcNetwork network;
	protected MockConnectionFactory factory;

	protected String getNetworkType() {
		return XmlRpcNetwork.class.getName();
	}
	
	protected String getPeerName() {
		return "123.123.123.123";
	}
	
	public void setUp() throws Exception {
		this.config = new XWikiConfig();
		this.config.put(ConnectorPlugin.NETWORK_TYPE, getNetworkType());
		this.config.put(AbstractNetwork.CONNECTOR_FACTORY, MockConnectionFactory.class.getName());
		this.config.put(AbstractNetwork.SERVER_PORT, "5555");
		this.config.put(AbstractNetwork.PEER_NAME, getPeerName());
		

		this.xcontext = new XWikiContext();
		this.xwiki = new XWiki(this.config, this.xcontext);
		this.xcontext.setWiki(this.xwiki);

		plugin = new ConnectorPlugin(ConnectorPlugin.PLUGIN_NAME, ConnectorPlugin.class.getName(), xcontext);
		
		factory = MockConnectionFactory.getInstance();
		network = (XmlRpcNetwork)factory.getNetwork();
	}

	public void tearDown() throws Exception {
		MockNetwork.resetInstance();
		MockConnectionFactory.resetInstance();
	}
	
	public void testGetConnectionFactory() {
		assertEquals(factory, network.getConnectionFactory());
	}

	public void testDefaultConnectorFactory() throws P2PXWikiException {
		String factoryType = network.defaultConnectorFactory();
		ConnectionFactory newFactory = (ConnectionFactory)P2PUtil.getInstance(factoryType,
				new Object[] {network}, new Class[] {Network.class});
		assertNotNull(newFactory);
	}

	public void testDefaultServerPort() {
		assertTrue(1024 < network.defaultServerPort());
		assertTrue(network.defaultServerPort() < 65567);
	}

	public void testGetPeerName() {
		assertEquals(getPeerName(), network.getPeerName());
	}

	public void testGetServerPort() {
		assertEquals(5555, network.getServerPort());
	}
	
	public void testGetCanonicalLocalName() {
		// TODO - does not work this way
		assertEquals("http://123.123.123.123:5555", network.getCanonicalLocalName());
	}
	
	public void testGetCanonicalRemoteName() {
		assertEquals("http://321.321.321.321:5555", network.getCanonicalRemoteName("321.321.321.321"));
	}
}
