package com.xpn.p2pxwiki.replication;

import java.util.Date;
import java.util.Vector;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;

public class ReplicationClientApi extends Api {
	private ReplicationClient repClient = null;

	public ReplicationClientApi(XWikiContext context) {
		super(context);
		if (repClient == null)
			repClient = new ReplicationClient(context);
	}

	public String login(String username, String password, String peerserver) {
		return repClient.login(username, password, peerserver);
	}

	public boolean logout(String token, String peerserver) {
		return repClient.logout(token, peerserver);
	}

	/** TODO: all these calls should take a wikiapp to replicate as an argument */

	public Vector getAllPagesAsXML(String token, String peerserver, String srcwiki) {
		return repClient.getAllPagesAsXML(token, peerserver, srcwiki);
	}

	/* date is in MMDDYYYY format */
	public Vector getAllPagesModifiedSinceAsXML(String token, String date,
			String peerserver, String srcwiki) {
		return repClient.getAllPagesModifiedSinceAsXML(token, date, peerserver, srcwiki);
	}

	public Vector getAllPagesModifiedSinceAsXML(String token, Date date,
			String peerserver, String srcwiki) {
		return repClient.getAllPagesModifiedSinceAsXML(token, date, peerserver, srcwiki);
	}

	/* apis to login to local replicas and getting a token */  
	public String login(String username, String password) {
		return repClient.login(username, password);
	}

	public boolean logout(String token) {
		return repClient.logout(token);
	}

	public boolean replicatePageFromXML(String token, String docsXML, String destwiki) { 
		return repClient.replicatePageFromXML(token,docsXML, destwiki); 
	}
	
	public boolean replicatePageFromXML(String token, String docsXML,
			String peerserver, String destwiki) {
		return repClient.replicatePageFromXML(token, docsXML, peerserver, destwiki);
	}
	
	/* api to replicate locally, make sure to pass a token to the  local p2pxwiki */ 
	public boolean replicateAllPagesFromXML(String token, Vector docsXML, String destwiki) { 
		return repClient.replicateAllPagesFromXML(token,docsXML, destwiki); 
	}
	
	/*
	 * @EXTENSION Replicating to any peer is supported ! make sure to pass a token 
	 * for the appropriate peerserver while doing replication. 
	 * 
	 */
	public boolean replicateAllPagesFromXML(String token, Vector docsXML,
			String peerserver, String destwiki) {
		return repClient.replicateAllPagesFromXML(token, docsXML, peerserver, destwiki);
	}
	
	/* single replication api */ 
	public boolean replicateAllPagesFromXML(String srctoken,
			String srcpeer, String srcwiki, String desttoken, 
			String destpeer, String destwiki) {
		return repClient.replicateAllPagesFromXML(srctoken, srcpeer, srcwiki, desttoken, 
				destpeer, destwiki);
	}
	public boolean replicateAllPagesFromXML(String srctoken,
			String srcpeer, String srcwiki, String desttoken, 
			String destwiki) {
		return repClient.replicateAllPagesFromXML(srctoken, srcpeer, srcwiki, desttoken, 
				destwiki);
	}
	public boolean replicateAllPagesModifiedSinceAsXML(String srctoken,
			String srcpeer, String srcwiki, String sinceDate, String desttoken, 
			String destpeer, String destwiki) { 
		return repClient.replicateAllPagesModifiedSinceAsXML(srctoken,srcpeer,srcwiki, 
				sinceDate, desttoken, destpeer, destwiki); 
	}
}
