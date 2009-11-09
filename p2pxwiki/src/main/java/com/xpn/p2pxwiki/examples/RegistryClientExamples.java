package com.xpn.p2pxwiki.examples;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.p2pxwiki.registry.RegistryClient;

/* this is an example of stand-alone client which uses the registryclient side apis. This is for 
 * demonstration purposes
 *  */ 
public class RegistryClientExamples extends ClientBaseExamples {
	private static Log log = LogFactory.getFactory().getInstance(
			RegistryClientExamples.class);

	
	/* @TODO: refactor it to a junit test */
	public void test() {
		String q = "bikash.p2pxwiki.com";
		String ip1 = "130.207.5.200";
		String ip2 = "130.207.4.300";
		RegistryClient reg = new RegistryClient(context);

		String dummyToken = "dummy";
		log.error("result of querying: " + q + " is:--"
				+ (ip1 = reg.queryRegistry(q)) + "--");
		log.error("updating registry with " + q + " and ip = " + ip2);
		boolean result = reg.updateRegistry(dummyToken, q, ip2);
		Assert.assertTrue(result == false);
		log.error("update to the registry failed, as expected"); 
		log.error("result of querying: " + q + " is:--" + reg.queryRegistry(q)
				+ "--");
	
		log.error("updating registry with " + q + " and ip = " + ip2);
		result = reg.updateMasterIP(dummyToken, q, ip2);
		Assert.assertTrue(result == false); 

		log.error("trying to login as user guest");
		String token = reg.login("guest", "guest");
		
    Assert.assertTrue(token.length() > 0); 
		log.error("login as guest successful!!");
		
		log.error("trying to login as user bikash");
		token = reg.login("bikash", "bikash"); 
		
		if (token != null)
			log.error("login as bikash successful!!");
		else
			log.error("login as bikash failed!!");
		
		if (token != null) {
			log.error("trying to update the registry with username bikash");
			boolean res = reg.updateRegistry(token, q, ip1);
      Assert.assertTrue(res == true); 
			log.error("update successful");
			
			log.error("result of querying after update of: " + q + " is:--"
					+ reg.queryRegistry(q) + "--");
		}
		System.out.println("test done !! ");
	}
	

	
	public void run() { 
		test() ; 
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RegistryClientExamples examples = new RegistryClientExamples(); 
		examples.initJXTA(); 
		examples.run(); 
	}

}
