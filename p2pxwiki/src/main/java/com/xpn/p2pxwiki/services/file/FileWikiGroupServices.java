package com.xpn.p2pxwiki.services.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.services.WikiGroup;
import com.xpn.p2pxwiki.services.WikiGroupServices;
import com.xpn.xwiki.XWikiException;

public class FileWikiGroupServices implements WikiGroupServices {
	private static Log log = LogFactory.getFactory().getInstance(FileWikiGroupServices.class);
	private static HashMap cache = new HashMap(); // TODO Problem in the dynamic case: cache consistency across machines
	private ArrayList groups = new ArrayList();
	private static String configFileName = "groups.xml";

	protected static WikiGroupServices instance = null;

	protected FileWikiGroupServices() throws P2PXWikiException {
		log.debug("constructor");
		init();
	}

	protected void init() throws P2PXWikiException {
		log.debug("init");
		Document config;
		// TODO: why not use java .properties files like everywhere else in xwiki? They can store a simple list.
		try {
			log.debug("Reading config file " + configFileName + ".xml");
			config = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(configFileName + ".xml"));
			NodeList groups = config.getElementsByTagName("group");
			for (int i = 0; i < groups.getLength(); ++i) {
				this.groups.add(((Element) groups.item(i)).getAttribute("name"));
			}
			log.debug("Read " + this.groups.size() + " groups");
		} catch (FileNotFoundException ex) {
			throw new P2PXWikiException(XWikiException.ERROR_XWIKI_CONFIG_FILENOTFOUND, "Cannot open groups declaration file " + configFileName);
		} catch (ParserConfigurationException ex) {
			throw new P2PXWikiException(XWikiException.ERROR_DOC_XML_PARSING, "Error while parsing groups declaration file");
		} catch (IOException ex) {
			throw new P2PXWikiException(XWikiException.ERROR_DOC_XML_PARSING, "Error while parsing groups declaration file");
		} catch (SAXException ex) {
			throw new P2PXWikiException(XWikiException.ERROR_DOC_XML_PARSING, "Error while parsing groups declaration file");
		}
	}

	public WikiGroup createWikiGroup(String groupName) throws P2PXWikiException {
		log.debug("createGroup");
		throw new P2PXWikiException(P2PXWikiException.GROUP_NOT_ALLOWED, "No groups can be created using the current policy");
	}

	public String[] getAllGroupsNames() throws P2PXWikiException {
		log.debug("getAllGroupsNames");
		return (String[]) this.groups.toArray(new String[0]);
	}

	/**
	 * @see com.xpn.p2pxwiki.services.WikiGroupServices#getAllGroups()
	 */
	public WikiGroup[] getAllGroups() throws P2PXWikiException {
		String[] groupNames = this.getAllGroupsNames();
		ArrayList groups = new ArrayList();
		for (int i = 0; i < groupNames.length; ++i) {
			try {
				groups.add(this.getWikiGroup(groupNames[i]));
			} catch (P2PXWikiException ex) {
				log.error("Error retrieving group " + groupNames[i], ex);
			}
		}
		return (WikiGroup[]) groups.toArray(new WikiGroup[0]);
	}

	public WikiGroup getWikiGroup(String groupName) throws P2PXWikiException {
		log.debug("getGroup: " + groupName);
		if (!this.groups.contains(groupName)) {
			throw new P2PXWikiException(P2PXWikiException.NO_SUCH_GROUP, "Invalid group " + groupName);
		}
		if (cache.containsKey(groupName)) {
			log.debug("Found group " + groupName + " in cache");
			return (FileGroup) cache.get(groupName);
		}
		log.debug("Inserting group " + groupName + " in cache");
		FileGroup group = new FileGroup(groupName);
		cache.put(groupName, group);
		return group;
	}

	public static WikiGroupServices getInstance() {
		if (instance != null) {
			try {
				instance = new FileWikiGroupServices();
			} catch (XWikiException ex) {
				log.error("Cannot instantiate WikiGroupServices", ex);
			}
		}
		return instance;
	}
}
