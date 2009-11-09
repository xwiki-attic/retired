package com.xpn.p2pxwiki.mocks;

import java.util.LinkedList;
import java.util.List;

import com.xpn.p2pxwiki.services.P2PServer;
import com.xpn.p2pxwiki.services.WikiGroup;

public class MockWikiGroup implements WikiGroup {
	private String master;
	private List servers = new LinkedList();
	
	public boolean acquireMasterRights(P2PServer server) {
		master = server.getName();
	    return true;
	}
	
	public String[] getAllServers() {
	    return (String[])servers.toArray(new String[] {});
	}
	
	public String getMaster() {
	    return master;
	}
	
	public int getNumberOfServers() {
	    return servers.size();
	}
	
	public String getRegistry() {
	    return null;
	}
	
	public String[] getSlaves() {
		List slaves = new LinkedList(servers);
		slaves.remove(master);
	    return (String[])servers.toArray(new String[] {});
	}

	public boolean hasMaster() {
	    return true;
	}
	
	public boolean hasRegistry() {
	    return false;
	}
	
	public boolean isMember(String serverName) {
	    return servers.contains(serverName);
	}
	
	public boolean releaseMasterRights(P2PServer server) {
	    return false; // cannot do
	}
	
	void addServer(P2PServer server) {
		servers.add(server.getName());
	}
}
