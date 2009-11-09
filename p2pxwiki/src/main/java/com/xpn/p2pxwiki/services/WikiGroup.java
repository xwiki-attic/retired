package com.xpn.p2pxwiki.services;

/**
 * A Wiki group is a virtual group of servers that host the same wiki by means
 * of replication. The servers in a group are synchronizing and coordinating
 * between each other.
 * 
 * The WikiGroup class is a helper class offering various  
 * @author sdumitriu
 */
//TODO add the name of the wiki for identification 
//TODO add a way to add new servers!?
//TODO can this class store anything other than names for remote servers? can it deal with anything other than names?
public interface WikiGroup {
	/**
	 * Returns all the known servers in this group at that moment.
	 * The resultset contains all servers that are knwon to host this wiki
	 * and be online
	 * 
	 * @return the list of server names
	 */
	String[] getAllServers();

	/**
	 * Check if this wiki has a master.
	 * @see com.xpn.p2pxwiki.services.P2PServer for the definition of a wiki master.
	 * @return true if the group has a master, false otherwise.
	 */
	boolean hasMaster();

	/**
	 * 
	 * @return
	 */
	String getMaster();

	String[] getSlaves();

	boolean hasRegistry();

	String getRegistry();

	boolean acquireMasterRights(P2PServer server);

	boolean releaseMasterRights(P2PServer server);

	int getNumberOfServers();

	boolean isMember(String serverName);
}
