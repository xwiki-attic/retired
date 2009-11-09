package com.xpn.p2pxwiki.synchronization.protocols;

import junit.framework.TestCase;

import com.xpn.p2pxwiki.communication.ConnectorPlugin;
import com.xpn.p2pxwiki.mocks.MockNetwork;
import com.xpn.p2pxwiki.synchronization.SynchronizationElement;
import com.xpn.p2pxwiki.synchronization.SynchronizationRequest;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiConfig;
import com.xpn.xwiki.XWikiContext;

public class SimpleSynchronizationTest extends TestCase {

	private SimpleSynchronization sync;
	private XWikiConfig config;
	private XWikiContext xcontext;
	private XWiki xwiki;
	private SynchronizationElement ase1;
	private SynchronizationRequest req1;
	private ConnectorPlugin connectorPlugin;
	private MockNetwork network;

	public SimpleSynchronizationTest(String arg0) throws Exception {
		super(arg0);
		this.config = new XWikiConfig();
		this.config.put(SimpleSynchronization.THREADS, "0");
		this.config.put(ConnectorPlugin.NETWORK_TYPE, MyNetwork.class.getName());
//		this.config.put("xwiki.store.class", MockStore.class.getName());

		this.xcontext = new XWikiContext();
		this.xwiki = new MyXWiki(this.config, this.xcontext);
		this.xcontext.setWiki(this.xwiki);

		connectorPlugin = new ConnectorPlugin(ConnectorPlugin.PLUGIN_NAME, ConnectorPlugin.class.getName(), xcontext);
		// this won't happen in real life, but it's useful for tests
		network = MockNetwork.getInstance();
    }
	
	public void setUp() throws Exception {


		sync = new SimpleSynchronization();
		sync.init(xcontext);
		
		ase1 = new SynchronizationElement("doc1", "server1", "language1");
		req1 = new SynchronizationRequest(new SynchronizationElement[] {ase1}, true);
		
	}

	public void tearDown() throws Exception {
	}

	public void testProcessRequestAsync() {
		fail("Not yet implemented");
	}

	public void testProcessRequestSync() {
		fail("Not yet implemented");
	}

	public void testHasTasks() {
		assertFalse(sync.hasTasks());
		sync.addAsyncRequest(req1);
		assertTrue(sync.hasTasks());
		SynchronizationElement ase = sync.pollTask();
		assertSame(ase1, ase);
		sync.doneTask(ase);
		assertFalse(sync.hasTasks());
	}
	
	public void testDoTask() {
		sync.addAsyncRequest(req1);
		SynchronizationElement ase = sync.pollTask();
		sync.doTask(ase);
		sync.doneTask(ase);
		assertTrue("Not saved", ((MyXWiki)xwiki).isSaved());
	}

	public void testRun() {
		fail("Not yet implemented");
	}
}
