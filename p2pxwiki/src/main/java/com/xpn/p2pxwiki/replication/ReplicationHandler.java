package com.xpn.p2pxwiki.replication;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.p2pxwiki.rpc.RpcHandler;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.xmlrpc.RequestInitializableHandler;

public class ReplicationHandler extends RpcHandler implements RequestInitializableHandler {
	private static Log log = LogFactory.getFactory().getInstance(
			ReplicationHandler.class);


	public final static int REPLICATION_IMPOSSIBLE = 0;

	public final static int REPLICATION_ALREADY_EXIST = 1;

	public final static int REPLICATION_OK = 2;

	public final static int REPLICATION_OLDVERSION_EXISTS = 4;

	public final static int REPLICATION_LOCALMODIFICATIONS_EXISTS = 5;

	/*
	 * @EXTENSIONS: take care of the case when local modification exists to a
	 * wiki doc and replication has received an old copy.
	 */

	public final static int REPLICATION_ERROR = 6;

	private boolean withVersions = false;

	public Vector getAllPagesAsXML(String token, String wikiapp) {
		try {
			XWikiContext context = getXWikiContext();
			return getAllPagesAsXML(token, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Vector();
	}

	public Vector getAllPagesAsXML(String token, XWikiContext context) {
		// Verify authentication token
		if (!checkToken(token, context)) {
			log.error("Authentication token verification failed for token "
					+ token + "\n");
			return new Vector();
		}
		XWiki wiki = context.getWiki();
		Vector res = new Vector();
		try {
			List spaces = wiki.getSpaces(context);
			for (int i = 0; i < spaces.size(); i++) {
				List DocsName = wiki.getSpaceDocsName((String) spaces.get(i),
						context);
				for (int j = 0; j < DocsName.size(); j++) {
					String docFullName = spaces.get(i) + "." + DocsName.get(j);
					XWikiDocument doc = wiki.getDocument(docFullName, context);
					res.add(doc.toXML(true, false, true, withVersions,
							context));

					/*
					 * @TODO: this may be unnecessary, and attachments may be
					 * getting added multiple times !
					 */
					List languages = doc.getTranslationList(context);
					for (int k = 0; k < languages.size(); k++) {
						String language = (String) languages.get(i);
						if (!((language == null) || (language.equals("")) || (language
								.equals(doc.getDefaultLanguage())))) {
							XWikiDocument translatedDoc = doc.getTranslatedDocument(language, context);
							res.add(translatedDoc.toXML(true, false, true,
									withVersions, context));
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new Vector();
		}
		return res;
	}	public Vector getAllPagesModifiedSinceAsXML(String token, String date, String wikiapp) {
		try {
			XWikiContext context = getXWikiContext();
			return getAllPagesModifiedSinceAsXML(token, date, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Vector();
	}

	/*
	 * date is in MMDDYYYY format @TODO take into account timezones too in the
	 * date
	 */
	public Vector getAllPagesModifiedSinceAsXML(String token, String date,
			XWikiContext context) {
		if (date.length() != 8) {
			log.error("incorrect date fomat.");
			return new Vector();
		}
		int month = Integer.parseInt(date.substring(0, 2));
		int day = Integer.parseInt(date.substring(2, 4));
		int year = Integer.parseInt(date.substring(4));
		log.error("looking for all docs since "+month+":"+day+":"+year);
		Date curDate = new Date(year-1900, month, day,0,0,0);
		Date nowDate = new Date(); 
		if (curDate.getTime() - nowDate.getTime() <= 2000) { 
			log.error("the time requested is too close to present time! ");
		}
		return getAllPagesModifiedSinceAsXML(token, curDate, context);

	}
	public Vector getAllPagesModifiedSinceAsXML(String token, long sinceWhenInMinutes,
			String wikiapp) { 
		try {
			XWikiContext context = getXWikiContext();
			return getAllPagesModifiedSinceAsXML(token, sinceWhenInMinutes, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Vector();
	}

	public Vector getAllPagesModifiedSinceAsXML(String token, long sinceWhenInMinutes,
			XWikiContext context) {

		Date curDate = new Date();
		long wantedMSec = curDate.getTime() - sinceWhenInMinutes * 60 * 1000; 
		Date wantedDate = new Date(wantedMSec); 
		return getAllPagesModifiedSinceAsXML(token, wantedDate, context);

	}

	public Vector getAllPagesModifiedSinceAsXML(String token, Date date, String wikiapp) {
		try {
			XWikiContext context = getXWikiContext();
			return getAllPagesModifiedSinceAsXML(token, date, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Vector();
	}

	/* returns all pages modified after given date */
	public Vector getAllPagesModifiedSinceAsXML(String token, Date date,
			XWikiContext context) {
		if (!checkToken(token, context)) {
			log.error("Authentication token verification failed for token "
					+ token + "\n");
			return new Vector();
		}
		XWiki wiki = context.getWiki();
		Vector res = new Vector();
		try {
			List spaces = wiki.getSpaces(context);
			for (int i = 0; i < spaces.size(); i++) {
				List DocsName = wiki.getSpaceDocsName((String) spaces.get(i),
						context);
				for (int j = 0; j < DocsName.size(); j++) {
					String docFullName = spaces.get(i) + "." + DocsName.get(j);
					/*
					 * @EXTENSION: there may be a better way then checking
					 * through *each document and each attachment* to see if it
					 * has been modified since the date
					 */
					XWikiDocument doc = wiki.getDocument(docFullName, context);
					Date updateDate = doc.getDate();
					log.error("existing Doc time in ms:"+updateDate.getTime()+
							" looking for everything after:"+ date.getTime());
					if (updateDate.after(date)) {
						/*
						 * @EXTENSION: currently the complete document is
						 * converted to xml future extension may include only
						 * copying the versions of the document after the
						 * specific date.
						 */
						res.add(doc.toXML(true, false, true, withVersions,
								context));
					}
					/*
					 * @TODO: this may be unnecessary, and attachments may be
					 * getting added multiple times !
					 */
					List languages = doc.getTranslationList(context);
					for (int k = 0; k < languages.size(); k++) {
						String language = (String) languages.get(i);
						if (!((language == null) || (language.equals("")) || (language
								.equals(doc.getDefaultLanguage())))) {
							XWikiDocument translatedDoc = doc
							.getTranslatedDocument(language, context);
							if (translatedDoc.getDate().after(date)) {
								res.add(translatedDoc.toXML(true, false, true,
										withVersions, context));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new Vector();
		}
		return res;
	}
	public boolean replicatePageFromXML(String token, String docXML, String wikiapp) {
		log.error("At the server side: inside replication handler "); 
		try {
			XWikiContext context = getXWikiContext();
			boolean res = replicatePageFromXML(token, docXML, context);
			log.error("done with replication at the serverside");
			return res; 
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.error("done with replication at the serverside: returning false after an error");
		return false;
	}
	public boolean replicatePageFromXML(String token, String docXML,
			XWikiContext context) {
		log.error("inside replicateAllPagesFromXML"); 
		if (!checkToken(token, context)) {
			log.error("Authentication token verification failed for token "
					+ token + "\n");
			return false;
		}
		boolean res = true;
		try {
			XWikiDocument doc = new XWikiDocument();
			doc.fromXML(docXML, withVersions);
			/*
			 * check if this document already exists and can be installed in
			 * the local wiki
			 */
			res |= replicateDocument(doc, context);
		} catch (Exception e) {
			log.error("error in replicating all pages from xml");
			e.printStackTrace();
			return false;
		}
		return res;
	}

	public boolean replicateAllPagesFromXML(String token, Vector docsXML, String wikiapp) {
		log.error("At the server side: inside replication handler "); 
		try {
			XWikiContext context = getXWikiContext();
			boolean res = replicateAllPagesFromXML(token, docsXML, context);
			log.error("done with replication at the serverside");
			return res; 
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.error("done with replication at the serverside: returning false after an error");
		return false;
	}
	public boolean replicateAllPagesFromXML(String token, Vector docsXML,
			XWikiContext context) {
		log.error("inside replicateAllPagesFromXML"); 
		if (!checkToken(token, context)) {
			log.error("Authentication token verification failed for token "
					+ token + "\n");
			return false;
		}
		log.error("trying to look at all xml documents, total size:"+ docsXML.size()); 
		boolean res = true;
		try {
			for (int i = 0; i < docsXML.size(); i++) {
				log.error("looking at document #"+i+" for replication"); 
				String xmlDoc = (String) docsXML.get(i);

				XWikiDocument doc = new XWikiDocument();
				doc.fromXML(xmlDoc, withVersions);
				/*
				 * check if this document already exists and can be installed in
				 * the local wiki
				 */
				res |= replicateDocument(doc, context);
				log.error("looked at document #"+i+" for replication"); 
			}
			log.error("done looking at all docs for replication"); 
		} catch (Exception e) {
			log.error("error in replicating all pages from xml");
			e.printStackTrace();
			return false;
		}
		return res;
	}

	private String getReplicableStatusString(int replicable) {

		switch(replicable){  
		case REPLICATION_ALREADY_EXIST: return "REPLICATION_ALREADY_EXIST"; 
		case REPLICATION_IMPOSSIBLE: return "REPLICATION_IMPOSSIBLE"; 
		case REPLICATION_LOCALMODIFICATIONS_EXISTS: return "REPLICATION_LOCALMODIFICATIONS_EXISTS"; 
		case REPLICATION_OK: return "REPLICATION_OK"; 
		case REPLICATION_OLDVERSION_EXISTS: return "REPLICATION_OLDVERSION_EXISTS"; 
		case REPLICATION_ERROR: return "REPLICATION_ERROR";
		}
		return "NOT_IMPLEMENTED"; 
	}

	private boolean replicateDocument(XWikiDocument doc, XWikiContext context) {
		try {
			String fullName = doc.getFullName();
			XWiki wiki = context.getWiki();
			log.error("replicating document: " + fullName);
			int replicable = testReplication(doc, context);
			log.error("result of testing for document:"+ fullName+ " is: "+ getReplicableStatusString(replicable));
			if (replicable == REPLICATION_OK
					|| replicable == REPLICATION_OLDVERSION_EXISTS) {
				if (replicable == REPLICATION_OLDVERSION_EXISTS) {
					log
					.error("local copy for "
							+ fullName
							+ " document exists and is older, updating it with newer version from the replica");
					/*
					 * @EXTENSION: apply diffs instead of deleting and
					 * recreating
					 */
					XWikiDocument deleteddoc = wiki.getDocument(fullName,
							context);
					wiki.deleteDocument(deleteddoc, context);
				} else {
					log.error("local copy for " + fullName
							+ " document doesn't exists, creating replica");
				}
				// We don't want date and version to change
				// So we need to cancel the dirty status
				doc.setContentDirty(false);
				doc.setMetaDataDirty(false);
				wiki.saveDocument(doc, context);
				doc.saveAllAttachments(context);
			} else if (replicable == REPLICATION_IMPOSSIBLE
					|| replicable == REPLICATION_ERROR) {
				return false;
			} else if (replicable == REPLICATION_ALREADY_EXIST) {
				log
				.error("local copy for "
						+ fullName
						+ " document is already uptodate, no changes needed in replication !");
				return true;
			} else if (replicable == REPLICATION_LOCALMODIFICATIONS_EXISTS) {
				log
				.error("local copy for "
						+ fullName
						+ " has been modified and is newer !  no replication done, p2pxwiki doesn't handle this case currently!");
				return false;
			}

		} catch (Exception e) {
			log.error("Exception in replicating document ");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private int testReplication(XWikiDocument doc, XWikiContext context) {
		int replicable = REPLICATION_IMPOSSIBLE;
		try {
			XWiki wiki = context.getWiki();
			String fullName = doc.getFullName();
			if (!wiki.checkAccess("edit", doc, context)) {
				log
				.error("do not have edit access to the wiki, replication of "
						+ fullName + " failed! ");
				return REPLICATION_IMPOSSIBLE;
			}
			XWikiDocument localdoc = wiki.getDocument(fullName, context);
			boolean isNew = localdoc.isNew();
			if (!isNew) {
				if ((doc.getLanguage() != null)
						&& (!doc.getLanguage().equals("")))
					isNew = !localdoc.getTranslationList(context).contains(
							doc.getLanguage());
			}
			if (!isNew) {
				Date docDate = doc.getDate();
				/* this call returns a newDate() if the updateDate is not there,
				 * so, a hack is to check for the returned date, and if it is within a few seconds say 2 sec
				 * then assume overwrite 
				 */
				Date curDate = new Date(); 
				Date localdocDate = localdoc.getDate();
				log.error("local doc modification date:"+ localdocDate+" the replica date is: "+ docDate); 
				if(Math.abs(curDate.getTime() - localdocDate.getTime()) <= 2000) { 
					log.error("the local document most likely doesn't have an updateDate, replacing with replica ");
					replicable =  REPLICATION_OLDVERSION_EXISTS;

				} else if (localdocDate.before(docDate)) {
					replicable = REPLICATION_OLDVERSION_EXISTS;
				} else if (localdocDate.getTime() == docDate.getTime()) {
					replicable = REPLICATION_ALREADY_EXIST;
				} else {
					/* @EXTENSION: take care of writable local copies */
					replicable = REPLICATION_LOCALMODIFICATIONS_EXISTS;
					log.error("local modifications exists ! -- EXTENSION --"); 
				}
				return replicable;
			}
		} catch (Exception e) {
			log.error("Exception in replicating document ");
			e.printStackTrace();
			return REPLICATION_ERROR;
		}
		return REPLICATION_OK;
	}	
}
