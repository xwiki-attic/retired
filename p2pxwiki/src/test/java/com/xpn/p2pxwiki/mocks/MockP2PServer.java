package com.xpn.p2pxwiki.mocks;

import java.util.LinkedList;
import java.util.List;

import com.xpn.p2pxwiki.services.P2PServer;
import com.xpn.p2pxwiki.services.WikiGroup;
import com.xpn.xwiki.XWikiException;

public class MockP2PServer implements P2PServer {
	private static MockP2PServer instance;
	private List groups = new LinkedList();
	private List mgroups = new LinkedList();
	private String name;

	protected MockP2PServer() {
	}

	public WikiGroup[] getGroups() {
		return (WikiGroup[]) groups.toArray(new WikiGroup[] {});
	}

	public WikiGroup[] getMasteredGroups() {
		return (WikiGroup[]) mgroups.toArray(new WikiGroup[] {});
	}

	public boolean joinGroup(WikiGroup group) throws XWikiException {
		((MockWikiGroup) group).addServer(this);
		return groups.add(group);
	}

	public boolean leaveGroup(WikiGroup group) throws XWikiException {
		return groups.remove(group);
	}

	public static MockP2PServer getInstance() {
		if (instance == null) {
			instance = new MockP2PServer();
		}
		return instance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
