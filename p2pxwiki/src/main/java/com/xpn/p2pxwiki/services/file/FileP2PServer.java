package com.xpn.p2pxwiki.services.file;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.communication.ConnectorPlugin;
import com.xpn.p2pxwiki.services.P2PServer;
import com.xpn.p2pxwiki.services.P2PServicesPlugin;
import com.xpn.p2pxwiki.services.WikiGroup;
import com.xpn.xwiki.XWikiException;

/**
 * 
 * @author sdumitriu
 */
public class FileP2PServer implements P2PServer {
	private static Log log = LogFactory.getFactory().getInstance(FileP2PServer.class);
	protected String name;

	protected static P2PServer instance = null;

	protected FileP2PServer() throws P2PXWikiException {
		log.debug("constructor");
		init();
	}

	protected void init() throws P2PXWikiException {
		log.debug("init");
		this.name = ConnectorPlugin.getInstance().getPeerName();
	}

	/**
	 * List all the groups this server is part of.
	 * Equivalent, what wikis does this server host. In order to avoid double
	 * configuration files, just filter the groups, since they know all their
	 * members.
	 * @return The virtual groups.
	 */
	public WikiGroup[] getGroups() throws XWikiException {
		log.debug("getGroups");
		WikiGroup[] allGroups = P2PServicesPlugin.getGroupServices().getAllGroups();
		ArrayList myGroups = new ArrayList();
		for(int i = 0; i < allGroups.length; ++i){
			if(allGroups[i].isMember(this.name)){
				myGroups.add(allGroups[i]);
			}
		}
		return (WikiGroup[])myGroups.toArray(new WikiGroup[0]);
	}

	/**
	 * List all the groups for which this server has master rights. In order to
	 * avoid double configuration files, just filter the groups, since they
	 * know all their members.
	 * @return The virtual groups mastered.
	 * @see com.xpn.p2pxwiki.services.P2PServer#getMasteredGroups()
	 */
	public WikiGroup[] getMasteredGroups() throws XWikiException {
		log.debug("getMasteredGroups");
		WikiGroup[] myGroups = this.getGroups();
		ArrayList masteredGroups = new ArrayList();
		for(int i = 0; i < myGroups.length; ++i){
			if(this.name.equals(myGroups[i].getMaster())){
				masteredGroups.add(myGroups[i]);
			}
		}
		return (WikiGroup[])masteredGroups.toArray(new WikiGroup[0]);
	}

	/**
	 * Join group method. Always fails, since basic file configurations are static.
	 * @param group The group to join.
	 * @return False if the server was part of the group. Exception otherwise.
	 * @throws XWikiException When trying to join a group.
	 */
	public boolean joinGroup(WikiGroup group) throws XWikiException {
		log.debug("joinGroup");
		if(group.isMember(this.name)){
			return false;
		}
		throw new P2PXWikiException(P2PXWikiException.ACCESS_DENIED, "Groups cannot be altered under the current policy");
	}

	/**
	 * Leave group method. Always fails, since basic file configurations are static.
	 * @param group The group to leave.
	 * @return False if the server was not part of the group. Exception otherwise.
	 * @throws XWikiException When trying to leave a group.
	 */
	public boolean leaveGroup(WikiGroup group) throws XWikiException {
		log.debug("leaveGroup");
		if(!group.isMember(this.name)){
			return false;
		}
		throw new P2PXWikiException(P2PXWikiException.ACCESS_DENIED, "Groups cannot be altered under the current policy");
	}

	/**
	 * Singleton method. makes sure there is only one instance of this class/
	 * @return The singleton instance
	 */
	public static P2PServer getInstance() throws XWikiException {
		log.debug("getInstance");
		if (instance == null) {
			try {
				instance = new FileP2PServer();
			} catch (XWikiException ex) {
				log.error("Cannot instantiate P2PServer", ex);
				throw new XWikiException();
			}
		}
		return instance;
	}

	/**
	 * @return The peer name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The new name to set.
	 */
	// TODO do you really allow this? will you notify all other servers of the name change?
//	public void setName(String name) {
//		this.name = name;
//	}
	
}
