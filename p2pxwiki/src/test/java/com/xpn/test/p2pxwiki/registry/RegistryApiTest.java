package com.xpn.test.p2pxwiki.registry;

import java.util.HashMap;

import junit.framework.Assert;

import com.xpn.p2pxwiki.registry.RegistryHandler;
import com.xpn.p2pxwiki.utils.P2PXWikiEngineContext;
import com.xpn.p2pxwiki.utils.P2PXWikiRequest;
import com.xpn.p2pxwiki.utils.P2PXWikiResponse;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.test.HibernateTestCase;

public class RegistryApiTest extends HibernateTestCase {
	RegistryHandler registryAPI;

	protected void setUp() throws Exception {
		super.setUp();
		P2PXWikiEngineContext econtext = new P2PXWikiEngineContext();
		econtext.setAttribute("xwiki", getXWiki());
		P2PXWikiRequest request = new P2PXWikiRequest();
		P2PXWikiResponse response = new P2PXWikiResponse();
		getXWikiContext().setRequest(request);
		getXWikiContext().setEngineContext(econtext);
		getXWiki().getConfig().setProperty("xwiki.virtual", "1");
		//getXWikiContext().setVirtual(true); 
		
		registryAPI = new RegistryHandler(request, response, econtext);
		assertNotNull(registryAPI.getServerClass(getXWikiContext())); 
	}

	public void testLogin() { 
		try { 
		XWikiContext context = getXWikiContext(); 
		// create user and try update
        String token = registryAPI.login("Admin","admin",""); 
        assertTrue (token.length()== 0); 
		XWiki xwiki = getXWiki(); 
        HashMap map = new HashMap();
        map.put("password", "admin");
        xwiki.createUser("Admin", map, "", "", "view, edit", context);
        
        token = registryAPI.login("Admin","admin",""); 
        assertTrue (token.length()> 0); 
        
		token = registryAPI.login("bikash", "bikash","");
		assertTrue(token.length()==0);
		
	        map = new HashMap();
	        map.put("password", "bikash");
	        int retval = getXWiki().createUser("bikash", map, "", "", "view, edit", context);
	        assertTrue(retval > 0); 
		
		token = registryAPI.login("bikash", "bikash","");
		assertTrue(token.length() >0);
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
	}
	

	public void testExistsHostName() {
		assertEquals(false, registryAPI.existsWikiapp("bikash.p2pxwiki.com",
				getXWikiContext()));
	}

	 
	public void testUpdateRegistryCreateNewUser() { 
		XWikiContext context = getXWikiContext(); 
		// create user and try update 
		try { 
	       HashMap map = new HashMap();
	        map.put("password", "bikash");
	        int retval = getXWiki().createUser("bikash", map, "", "", "view, edit", context);
          Assert.assertTrue(retval > 0); 
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
		String token = registryAPI.login("bikash", "bikash","");
		assertTrue(token.length() >0);
		assertEquals(false, registryAPI.updateRegistry(token,
				"bikash.p2pxwiki.com", "130.207.117.201", context));
		assertEquals(false, registryAPI.existsWikiapp("bikash.p2pxwiki.com",
				context));
		
		// create a server class and then try updating as the owner of the server class
		token = registryAPI.login("bikash", "bikash", "");
		assertTrue(token.length() > 0);
		boolean result = registryAPI.createServerClass("bikash", "bikash",
				"bikash.p2pxwiki.com", "bikash's p2pxwiki", null,context);
		assertEquals(true, result); 
		assertEquals(true, registryAPI.updateRegistry(token,
				"bikash.p2pxwiki.com", "130.207.117.201", context));
		assertEquals(true, registryAPI.existsWikiapp("bikash.p2pxwiki.com",
				context));
	}
	
	
	public void testUpdateRegistryFailure() {
		XWikiContext context = getXWikiContext(); 
		assertEquals(false, registryAPI.existsWikiapp("bikash.p2pxwiki.com",
				context));
		// update as guest  
		String token = registryAPI.login("guest", "guest","");
		assertTrue(token.length() > 0);
		assertEquals(false, registryAPI.updateRegistry(token,
				"bikash.p2pxwiki.com", "130.207.117.201", context));
		// user doesn't exist  
		token = registryAPI.login("bikash", "bikash","");
		assertTrue(token.length() == 0);
		assertEquals(false, registryAPI.updateRegistry(token,
				"bikash.p2pxwiki.com", "130.207.117.201", context));
	}

	
	public void testQueryRegistryExists() {
		assertEquals(false, registryAPI.existsWikiapp(
				"http://bikash.p2pxwiki.com", getXWikiContext()));
		try { 
		       HashMap map = new HashMap();
		        map.put("password", "bikash");
		        int retval = getXWiki().createUser("bikash", map, "", "", "view, edit", getXWikiContext());
            Assert.assertTrue(retval > 0); 
			} catch (Exception e) { 
				e.printStackTrace(); 
			}
			
		String token = registryAPI.login("bikash", "bikash","");
		assertTrue(token.length() > 0);
		boolean result = registryAPI.createServerClass("bikash", "bikash",
				"bikash.p2pxwiki.com", "bikash's p2pxwiki", null, getXWikiContext());
		assertEquals(true, result); 
		boolean updateres = registryAPI.updateRegistry(token,
				"http://bikash.p2pxwiki.com", "130.207.117.200",
				getXWikiContext());
		assertEquals(true, updateres);
		assertEquals(registryAPI.queryRegistry("http://bikash.p2pxwiki.com",
				getXWikiContext()), "130.207.117.200");
		updateres = registryAPI.updateRegistry(token,
				"http://bikash.p2pxwiki.com", "130.207.117.201",
				getXWikiContext());
		assertEquals(true, updateres);
		assertEquals(registryAPI.queryRegistry("http://bikash.p2pxwiki.com",
				getXWikiContext()), "130.207.117.201");
	}

	public void testQueryRegistryNotExist() {
		assertEquals(registryAPI.queryRegistry("http://bikash.p2pxwiki.com",
				getXWikiContext()), "");
	}
	
}
