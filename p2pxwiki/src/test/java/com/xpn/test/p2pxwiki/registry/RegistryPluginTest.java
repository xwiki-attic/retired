package com.xpn.test.p2pxwiki.registry;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.p2psockets.P2PNetwork;

import com.xpn.p2pxwiki.registry.RegistryClient;
import com.xpn.p2pxwiki.rpc.RpcClient;
import com.xpn.xwiki.XWikiContext;
/* simple JUnit test which contacts a xwiki running at a fixed location 
 * and queries for/update the ip addresses using the apis. 
 * 
 * A correct approach would be to develop a cactus test. 
 */
public class RegistryPluginTest extends TestCase {
	 private static Log log = LogFactory.getFactory().getInstance(
				RegistryPluginTest.class);
	 RegistryClient reg ; 
	 RpcClient rpcclient;
	 String group = "ratusca"; // P2PXWikiRPCCommons.getJxtaXWikiRegistryAddress()
	 
	 protected void setUp() throws Exception {
		super.setUp();
		reg = new RegistryClient(new XWikiContext());
		rpcclient = new RpcClient(new XWikiContext());
		String username ="bikash-test"; 
		try {
	        P2PNetwork.autoSignin(username, "");
        }
        catch (Exception e) { 
        	e.printStackTrace(); 
        }
	}


	public void testLogin() { 
		String q = "bikash.p2pxwiki.com"; 
		String ip1 = "69.180.20.68"; 
		String ip2 = "130.207.4.300"; 

		
		String dummyToken = "dummy"; 
		log.error("result of querying: "+q+" is:--"+reg.queryRegistry(q)+"--"); 
		log.error("updating registry with "+q+" and ip = "+ ip1); 
		reg.updateRegistry(dummyToken,q,ip1); 
		log.error("result of querying: "+q+" is:--"+reg.queryRegistry(q)+"--"); 	
		log.error("updating registry with "+q+" and ip = "+ ip2); 
		reg.updateMasterIP(dummyToken,q,ip2); 
		log.error("final update done for wiki: "+ q + " with ip "+ ip2);
		
		log.error("trying to login as user guest") ; 
		String token = rpcclient.login("guest","guest",group);
		if (token.length() > 0) log.error("login as guest successful!!"); 
		log.error("trying to login as user bikash") ; 
		token = rpcclient.login("bikash","bikash",group);
		if (token != null) log.error("login as bikash successful!!"); 
		else log.error("login as bikash failed!!"); 
	}
}
