package com.xpn.p2pxwiki.services;

import com.xpn.p2pxwiki.mocks.MockP2PServer;
import com.xpn.p2pxwiki.mocks.MockWikiGroup;

public class MockP2PServerTest extends P2PServerAbstractTest {
	public MockP2PServerTest(String arg0) {
		super(arg0);
	}
	
	public Class getP2PServerClass() {
		return MockP2PServer.class;
	}
	
	public WikiGroup getNewWikiGroup() {
	    return new MockWikiGroup();
	}
}
