package com.xpn.p2pxwiki.services.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.services.P2PServer;
import com.xpn.xwiki.XWikiException;

public class FileGroup implements com.xpn.p2pxwiki.services.WikiGroup {
	private static Log log = LogFactory.getFactory().getInstance(FileGroup.class);
	private String name;
	private ArrayList servers = new ArrayList();
	private String master = null;

	FileGroup(String name) throws P2PXWikiException {
		log.debug("constructor");
		Document config;
		try {
			// TODO why not use java .properties files like everywhere else in xwiki? They can store a simple list.
			// TODO add sample configuration file
			log.debug("Reading config file " + name + ".grp.xml");
			config = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(name + ".grp.xml"));
			this.name = name;
			if (this.name != config.getDocumentElement().getAttribute("name")) {
				// TODO redundancy evil ... avoid redundancy
				log.warn("The group name inside the file is not equal to the requested group name");
			}
			NodeList servers = config.getElementsByTagName("server");
			for (int i = 0; i < servers.getLength(); ++i) {
				this.servers.add(((Element) servers.item(i)).getAttribute("name"));
			}
			log.debug("Read " + this.servers.size() + " servers");
			this.master = config.getDocumentElement().getAttribute("master");
			if ("".equals(this.master)) {
				log.warn("No specified master node for group \"" + name + "\". Selecting the first server as master.");
				this.master = (String) this.servers.get(0);
			}
			log.debug("Group master: " + this.master);
		} catch (FileNotFoundException ex) {
			throw new P2PXWikiException(P2PXWikiException.NO_SUCH_GROUP, "Invalid group " + name);
		} catch (ParserConfigurationException ex) {
			throw new P2PXWikiException(XWikiException.ERROR_DOC_XML_PARSING, "Error while parsing group definition file for \"" + name + "\"");
		} catch (IOException ex) {
			throw new P2PXWikiException(XWikiException.ERROR_DOC_XML_PARSING, "Error while parsing group definition file for \"" + name + "\"");
		} catch (SAXException ex) {
			throw new P2PXWikiException(XWikiException.ERROR_DOC_XML_PARSING, "Error while parsing group definition file for \"" + name + "\"");
		}
	}

	public boolean acquireMasterRights(P2PServer server) {
		// This is a static configuration; no changes allowed
		log.debug(this.name + ": acquireMasterRights");
		return false;
	}

	public boolean releaseMasterRights(P2PServer group) {
		// This is a static configuration; no changes allowed
		log.debug(this.name + ": releaseMasterRights");
		return false;
	}

	public int getNumberOfServers() {
		log.debug(this.name + ": getNumberOfServers");
		return this.servers.size();
	}

	public String[] getAllServers() {
		log.debug(this.name + ": getAllServers");
		String[] result = new String[this.getNumberOfServers()];
		int i = 0;
		for (Iterator it = this.servers.iterator(); it.hasNext(); ++i) {
			result[i] = (String) it.next();
		}
		return result;
	}

	public boolean hasMaster() {
		return true;
	}

	public String getMaster() {
		return this.master;
	}

	public boolean hasRegistry() {
		// No registry needed
		return false;
	}

	public String getRegistry() {
		// No registry needed
		return null;
	}

	public String[] getSlaves() {
		String[] result = new String[this.getNumberOfServers() - 1];
		int i = 0;
		for (Iterator it = this.servers.iterator(); it.hasNext();) {
			result[i] = (String) it.next();
			if (!result[i].equals(this.master)) {
				++i;
			}
		}
		return result;
	}

	public boolean isMember(String serverName) {
		if (this.servers.contains(serverName)) {
			return true;
		}
		return false;
	}
}
