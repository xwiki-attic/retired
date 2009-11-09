package com.xpn.p2pxwiki.services;

import junit.framework.TestCase;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.xwiki.XWikiException;

abstract public class P2PServerAbstractTest extends TestCase {
	private P2PServer server;
	private WikiGroup group1;
	private WikiGroup group2;

	public P2PServerAbstractTest(String arg0) {
		super(arg0);
	}
	
	public abstract Class getP2PServerClass();
	public abstract WikiGroup getNewWikiGroup() throws P2PXWikiException;

	public void setUp() throws Exception {
		server = (P2PServer)getP2PServerClass().getMethod("getInstance", (Class[])null).invoke(null, (Object[])null);
		if (server.getGroups().length > 0) {
			tearDown(); // make the group empty
		}
		group1 = getNewWikiGroup();
		group2 = getNewWikiGroup();
	}

	public void tearDown() throws Exception {
		WikiGroup[] group = server.getGroups();
		for (int i = 0; i < group.length; i++) {
			server.leaveGroup(group[i]);
		}
	}


	public void testInitialState() throws XWikiException {
		assertEquals(0, server.getGroups().length);
		assertEquals(0, server.getMasteredGroups().length);
	}

	public void testJoinLeaveGroup() throws XWikiException {
		server.joinGroup(group1);
		assertEquals(1, server.getGroups().length);
		assertEquals(group1, server.getGroups()[0]);
		assertEquals(1, group1.getNumberOfServers());
		assertTrue(group1.isMember(server.getName()));
	}

	public void testGroups() throws XWikiException {
		server.joinGroup(group1);
		server.joinGroup(group2);
		assertEquals(2, server.getGroups().length);
		assertTrue(server.getGroups()[0] == group1 || server.getGroups()[1] == group1);
		assertTrue(server.getGroups()[0] == group2 || server.getGroups()[1] == group2);
		server.leaveGroup(group1);
		assertEquals(1, server.getGroups().length);
		assertTrue(server.getGroups()[0] == group2);
		server.leaveGroup(group2);
		assertEquals(0, server.getGroups().length);
	}	
}
