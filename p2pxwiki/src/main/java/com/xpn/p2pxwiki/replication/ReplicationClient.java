package com.xpn.p2pxwiki.replication;

import java.util.Date;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.p2pxwiki.client.BaseClient;
import com.xpn.p2pxwiki.communication.ConnectorPlugin;
import com.xpn.p2pxwiki.communication.HandlerStub;
import com.xpn.xwiki.XWikiContext;

public class ReplicationClient extends BaseClient {
	private final String HANDLERCLASS = "replication";
	private static Log log = LogFactory.getFactory().getInstance(
			ReplicationClient.class);

	public ReplicationClient(XWikiContext context){
		super(context);
	}
	public String login(String username, String password, String peerserver) {
		return super.login(username, password, peerserver, HANDLERCLASS);
	}

	public boolean logout(String token, String peerserver) {
		return super.logout(token, peerserver, HANDLERCLASS);
	}

	public String login(String username, String password) {
		String peerserver = ConnectorPlugin.getInstance().getCanonicalLocalName(); 
		if (peerserver == null) { 
			log.error("error in getting local JXTA server address: ---ERRROR --- ") ;
			return null; 
		}
		return super.login(username, password, peerserver, HANDLERCLASS);
	}

	public boolean logout(String token) {
		String peerserver = ConnectorPlugin.getInstance().getCanonicalLocalName();
		if (peerserver == null) { 
			log.error("error in getting local JXTA server address: ---ERRROR --- ") ;
			return false; 
		}
		return super.logout(token, peerserver, HANDLERCLASS);
	}
	
	/* @TODO: all these calls should take a wikiapp to replicate as an argument */

	public Vector getAllPagesAsXML(String token, String peerserver, String wikiapp) {
		HandlerStub client = getConnection(peerserver);
		try {
			Vector result = (Vector) client.execute(HANDLERCLASS
					+ ".getAllPagesAsXML", new Object[] {token, wikiapp});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		/* 
		 *@EXTENSION: distinguish between 0 result and an error from server 
		 */  
		return new Vector(); 
	}

	/* date is in MMDDYYYY format */
	public Vector getAllPagesModifiedSinceAsXML(String token, String date,
			String peerserver, String wikiapp) {
		HandlerStub client = getConnection(peerserver);
		try {
			Vector result = (Vector) client.execute(HANDLERCLASS
					+ ".getAllPagesModifiedSinceAsXML", new Object[] {token, date, wikiapp});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Vector(); 
	}

	public Vector getAllPagesModifiedSinceAsXML(String token, long sinceWhen,
			String peerserver, String wikiapp) {
		HandlerStub client = getConnection(peerserver);
		try {
			Vector result = (Vector) client.execute(HANDLERCLASS
					+ ".getAllPagesModifiedSinceAsXML",
					new Object[] {token, new Long(sinceWhen), wikiapp});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Vector(); 
	}
	
	public Vector getAllPagesModifiedSinceAsXML(String token, Date date,
			String peerserver, String wikiapp) {

		HandlerStub client = getConnection(peerserver);
		try {
			Vector result = (Vector) client.execute(HANDLERCLASS
					+ ".getAllPagesModifiedSinceAsXML", new Object[] {token, date, wikiapp});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Vector(); 
	}	
	
	public boolean replicatePageFromXML(String token, String docXML, String peerserver, String wikiapp) { 
		HandlerStub client = getConnection(peerserver);
		try {
			boolean result = ((Boolean) client.execute(HANDLERCLASS
					+ ".replicatePageFromXML", new Object[] {token, docXML, wikiapp})).booleanValue();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/* api to replicate locally */ 
	public boolean replicatePageFromXML(String token, String docsXML, String wikiapp) { 
		String peerserver = ConnectorPlugin.getInstance().getCanonicalLocalName();
		if (peerserver == null) { 
			log.error("error in getting local JXTA server address: ---ERRROR --- ") ;
			return false; 
		}
		log.debug("connecting to local peer:"+ peerserver+" for replication"); 
		return replicatePageFromXML(token,docsXML, peerserver, wikiapp); 
	}
	
	/* api to replicate locally */ 
	public boolean replicateAllPagesFromXML(String token, Vector docsXML, String wikiapp) { 
		String peerserver = ConnectorPlugin.getInstance().getCanonicalLocalName(); 
		if (peerserver == null) { 
			log.error("error in getting local JXTA server address: ---ERRROR --- ") ;
			return false; 
		}
		log.error("connecting to local peer:"+ peerserver+" for replication"); 
		return replicateAllPagesFromXML(token,docsXML, peerserver, wikiapp); 
	}
	
	/*
	 * @EXTENSION: This api has future and testing purpose use when the
	 * replication can be done at another wiki as well
	 * 
	 * local replication is supported by providing the name of the local
	 * peerserver as the peerserver argument, and a local user token
	 */
	public boolean replicateAllPagesFromXML(String token, Vector docsXML,
			String peerserver, String wikiapp) {
		log.error("calling replication for "+ peerserver+" and wikiapp "+wikiapp);
		HandlerStub client = getConnection(peerserver);
		try {
			boolean result = ((Boolean) client.execute(HANDLERCLASS
					+ ".replicateAllPagesFromXML",
					new Object[] {token, docsXML, wikiapp})).booleanValue();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean replicateAllPagesFromXML(String srctoken,
			String srcpeer, String srcwiki, String desttoken, 
			String destpeer, String destwiki) { 
		log.error("getting all pages from "+ srcpeer+" for wiki: "+ srcwiki); 
		Vector allPages = getAllPagesAsXML(srctoken,srcpeer,srcwiki); 
		log.error("got "+allPages.size()+" pages"); 
		return replicateAllPagesFromXML(desttoken,allPages,destpeer,destwiki); 
	}
	
	public boolean replicateAllPagesFromXML(String srctoken,
			String srcpeer, String srcwiki, String desttoken, String destwiki) { 
		
		Vector allPages = getAllPagesAsXML(srctoken,srcpeer,srcwiki); 
		
		return replicateAllPagesFromXML(desttoken,allPages,destwiki); 
	}
	
	public boolean replicateAllPagesModifiedSinceAsXML(String srctoken,
			String srcpeer, String srcwiki, String sinceDate, String desttoken, 
			String destpeer, String destwiki) { 
		Vector allPages = getAllPagesModifiedSinceAsXML(srctoken, sinceDate, srcpeer,srcwiki); 
		
		return replicateAllPagesFromXML(desttoken,allPages,destwiki); 
	}
}
