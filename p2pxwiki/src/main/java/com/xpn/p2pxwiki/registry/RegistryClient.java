package com.xpn.p2pxwiki.registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.p2pxwiki.client.BaseClient;
import com.xpn.p2pxwiki.communication.ConnectorPlugin;
import com.xpn.p2pxwiki.communication.HandlerStub;
import com.xpn.xwiki.XWikiContext;

public class RegistryClient extends BaseClient {
	private static Log log = LogFactory.getFactory().getInstance(
			RegistryClient.class);

	public RegistryClient(XWikiContext context){
		super(context);
	}
	private String P2PXWIKIREGISTRYADDRESS = ConnectorPlugin.getInstance().getCanonicalRemoteName("registry");

	private String HANDLERCLASS = "registry";

	public String login(String username, String password) {
		return super.login(username, password, P2PXWIKIREGISTRYADDRESS,
				HANDLERCLASS);
	}

	public boolean logout(String token) {
		return super.logout(token, P2PXWIKIREGISTRYADDRESS, HANDLERCLASS);
	}

	public boolean updateRegistry(String token, String wikiname, String IP) {
		/*
		 * TODO Talk to the jxta network and find out the registry and update
		 * the IP of the master wiki, the registry will then check user exists,
		 * password is correct, check wiki exists, then update the IP
		 */
		HandlerStub client = getConnection(P2PXWIKIREGISTRYADDRESS);
		try {
			Boolean res = (Boolean) client.execute(HANDLERCLASS
					+ ".updateRegistry", new Object[] {token, wikiname, IP});
			return res.booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/* TODO Api to check a user is going offline and updating the IP */

	/*
	 * TODO Apis to update the IP of a masterwiki because user is moving to a
	 * new place on a per wiki basis (which checks that the user is infact the
	 * master) and contacts the registry All the APIs below are going to be
	 * JXTA-RPC/RMI. A first cut will be local update assuming we are working at
	 * the registry.
	 */

	public boolean updateMasterIP(String token, String wikiname, String IP) {
		return updateRegistry(token, wikiname, IP);
	}

	/*
	 * TODO Assuming that every user machine has a unique single instance of
	 * p2pxwiki, this means the user name can be used to migrate all locally
	 * hosted Wiki to new IP in the registry make sure that only the registry
	 * has the other database and its associated mapping. One way is to make two
	 * versions of source code, one for the "registry" node and one for others.
	 * another approach will be to conditionally create db etc.
	 */
	/*
	 * public int updateAllMastersIPbyUser(String token, String IP) { return 0; }
	 */
	/*
	 * TODO This version of the update API allows to migrate a set of wikis
	 * which were already in an old ip, and owned by the master, to a new IP
	 */
	/*
	 * public int updateAllMastersfromOldIP(String username, String password,
	 * String oldIP, String newIP) { return 0; }
	 */

	public String queryRegistry(String wikiname) {
		HandlerStub client = getConnection(P2PXWIKIREGISTRYADDRESS);
		log.error("Going to query for wikiname:"+ wikiname+" using registry:"+ 
				P2PXWIKIREGISTRYADDRESS); 
		try {
			String res = (String) client.execute(HANDLERCLASS
					+ ".queryRegistry", new Object[] {wikiname});
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public boolean existsWikiapp(String wikiapp) {
		HandlerStub client = getConnection(P2PXWIKIREGISTRYADDRESS);
		try {
			boolean res = ((Boolean) client.execute(HANDLERCLASS
					+ ".existsWikiapp", new Object[] {wikiapp})).booleanValue();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
