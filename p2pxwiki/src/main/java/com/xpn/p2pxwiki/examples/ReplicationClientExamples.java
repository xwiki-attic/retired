package com.xpn.p2pxwiki.examples;

import java.util.Date;
import java.util.Vector;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.p2pxwiki.communication.ConnectorPlugin;
import com.xpn.p2pxwiki.replication.ReplicationClient;


public class ReplicationClientExamples extends ClientBaseExamples {
	private static Log log = LogFactory.getFactory().getInstance(
			ReplicationClientExamples.class);


	public void test() { 
		ReplicationClient client = new ReplicationClient(context);
		String wikiserver = "registry"; 
		
		log.error("trying to login as user bikash");
		String token = client.login("bikash", "bikash", wikiserver);
		
    Assert.assertTrue(token != null); 
			log.error("login as bikash successful!!");
	
		Vector allXML = client.getAllPagesAsXML(token,wikiserver, "http://bikash.p2pxwiki.com"); 
		log.error("got "+allXML.size()+" entries from the wiki at: "+ wikiserver ); 
		log.error("address of local jxta peer is : "+ ConnectorPlugin.getInstance().getCanonicalLocalName());
	
		token = client.login("Admin","admin"); 
		log.error("result of loggin in to local peer as Admin for replication is token:"+ token);
		log.error("trying replication: will see that all pages exist !"); 
		
		boolean res = client.replicateAllPagesFromXML(token,allXML, "http://bikash.p2pxwiki.com"); 
		log.error("tried replication, got result: "+ res); 
		
		log.error("WARN:: Trying to copy all the contents from bikash's wiki to db ludovic locally !!! "); 
		
		res = client.replicateAllPagesFromXML(token,allXML,"http://ludovic.p2pxwiki.com"); 
		log.error("tried replication to db ludovic, got result: "+ res); 
		
	/*  try individually  	
		for(int  i = 0; i < allXML.size(); i++) { 
			log.error("client trying to replicate doc #"+i ); 
			boolean res = client.replicatePageFromXML(token, (String) allXML.get(i)); 
			log.error("tried replication #"+i+" , got result: "+ res); 
		}
		*/ 
		
		allXML = client.getAllPagesModifiedSinceAsXML(token,new Date(),wikiserver, "http://bikash.p2pxwiki.com"); 
		log.error("got "+allXML.size()+" entries modified since Now from the wiki at: "+ wikiserver );
	}
	
	public void run() { 
		test() ; 
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ReplicationClientExamples examples = new ReplicationClientExamples(); 
		examples.initJXTA(); 
		examples.run(); 
	}


}
