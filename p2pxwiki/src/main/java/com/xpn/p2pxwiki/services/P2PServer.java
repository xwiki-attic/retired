package com.xpn.p2pxwiki.services;

import com.xpn.xwiki.XWikiException;

/**
 * A server hosts wikis; it serves request from clients like reads from and/or
 * updates to the wikis it hosts.
 * 
 * An instance of a class implementing P2PServer represents a peer as part of
 * all {@link WikiGroup wiki groups}, so implementing classes should be
 * singletons, and should contain a static method named <tt>getInstance()</tt>. 
 * 
 * A server can have the role of <strong>Group Master</strong> for some wiki
 * groups. A group master is the designated server that coordinates the activity
 * in a server group. It can have more knowledge about the group than the
 * other servers, and hosts the authoritative version of the wiki. Only one
 * master exists in a group at a given time. A server is a master only with
 * respect to a particular group/wiki, meaning that there is no absolute
 * master for all wikis.
 * 
 * @author sdumitriu
 */
public interface P2PServer {
	/**
	 * List all the groups this server is part of.
	 * Equivalent, what wikis does this server host.
	 * @return The virtual groups.
	 */
	WikiGroup[] getGroups() throws XWikiException;

	/**
	 * List all the groups for which this server has master rights. 
	 * @return The virtual groups mastered.
	 */
	WikiGroup[] getMasteredGroups() throws XWikiException;

	/**
	 * Try to join a wiki group. 
	 * @param group The group to join.
	 * @return True on success. False if the server
	 * was already a member of the group. Exception otherwise.
	 * @throws XWikiException If the server was not part of the group, but
	 * failed to join.
	 */
	boolean joinGroup(WikiGroup group) throws XWikiException;

	/**
	 * Leave a group.
	 * @param group The group to leave. 
	 * @return true if the server succesfully left the group. False if the
	 * server was not part of the group. Exception otherwise.
	 * @throws XWikiException If the server could not leave the group.
	 */
	boolean leaveGroup(WikiGroup group) throws XWikiException;
	
	String getName();
	// TODO assure that this name is the same as the peer name in ConnectorPlugin
	// TODO this name should never change
}
