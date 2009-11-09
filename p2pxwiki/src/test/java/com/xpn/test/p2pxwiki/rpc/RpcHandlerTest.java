package com.xpn.test.p2pxwiki.rpc;

import java.util.HashMap;

import com.xpn.p2pxwiki.rpc.RpcHandler;
import com.xpn.p2pxwiki.utils.P2PXWikiEngineContext;
import com.xpn.p2pxwiki.utils.P2PXWikiRequest;
import com.xpn.p2pxwiki.utils.P2PXWikiResponse;
import com.xpn.xwiki.test.HibernateTestCase;

public class RpcHandlerTest extends HibernateTestCase {
	private P2PXWikiEngineContext econtext; 
	protected void setUp() throws Exception {
		super.setUp();
		 econtext = new P2PXWikiEngineContext(); 
		 econtext.setAttribute("xwiki", getXWiki()); 
	}

	protected void tearDown()  {
		super.tearDown();
	}

	/*
	 * Test method for 'com.xpn.p2pxwiki.rpc.P2PXWikiRPCHandler.login(String, String)'
	 * 
	 * This is just server side test. @TODO: write cactus tests for the client side 
	 */
	public final void testLoginExistingUser() {
		RpcHandler rpchandler = new RpcHandler(new P2PXWikiRequest(), new P2PXWikiResponse(), econtext);
	//	P2PXWikiRPCHandler rpchandler = new P2PXWikiRPCHandler(new JXTAXWikiRequest(), new JXTAXWikiResponse(), new P2PXWikiEngineContext());
	//	P2PXWikiRPCHandler rpchandler = new P2PXWikiRPCHandler(new JXTAXWikiRequest(), new JXTAXWikiResponse(), getXWiki().getEngineContext());
		String token = ""; 
		try {
			token = rpchandler.login("guest", "guest","");
			assertTrue(token.length()>0); 
			token = rpchandler.login("bikash", "bikash",""); 
			assertTrue(token.length()==0);  
		} 
		catch(Exception e) { 
			e.printStackTrace(); 
			assertFalse(true); 
		}
	}
	public final void testLoginCreateUser() { 
		RpcHandler rpchandler = new RpcHandler(new P2PXWikiRequest(), new P2PXWikiResponse(), econtext);
		String token = ""; 
		try {
			token = rpchandler.login("superadmin", "superadmin","");
			assertTrue(token.length()==0); 
			getXWikiConfig().setProperty("xwiki.superadminpassword","superadmin"); 
			token = rpchandler.login("superadmin","superadmin",""); 
			assertTrue(token.length()>0); 
			token = rpchandler.login("bikash", "bikash",""); 
			assertTrue(token.length()==0);  
		    HashMap map = new HashMap();
		    map.put("password", "bikash");
		    int retval = getXWiki().createUser("bikash", map, "", "", "view, edit", getXWikiContext());
		    assertTrue(retval > 0); 
		    token = rpchandler.login("bikash", "bikash",""); 
		    assertTrue(token.length()>0);
		} 
		catch(Exception e) { 
			e.printStackTrace(); 
			assertFalse(true); 
		}
	}
}
