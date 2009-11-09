package com.xpn.p2pxwiki.synchronization.protocols;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.communication.ConnectorPlugin;
import com.xpn.p2pxwiki.communication.HandlerStub;
import com.xpn.p2pxwiki.synchronization.SynchronizationElement;
import com.xpn.p2pxwiki.synchronization.SynchronizationElementQueue;
import com.xpn.p2pxwiki.synchronization.SynchronizationInterface;
import com.xpn.p2pxwiki.synchronization.SynchronizationProtocol;
import com.xpn.p2pxwiki.synchronization.SynchronizationRequest;
import com.xpn.p2pxwiki.synchronization.WrappedSynchronizationElement;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;

public class SimpleSynchronization implements SynchronizationProtocol, Runnable {
	private static Log log = LogFactory.getFactory().getInstance(com.xpn.p2pxwiki.synchronization.protocols.SimpleSynchronization.class);
	private static final int DEFAULT_THREADS = 10;
	public static final String THREADS = "p2pxwiki.synchronization.threads";

	protected static int threads;
	protected Thread[] threadPool;
	protected XWiki wiki;
	protected XWikiContext context;

	private Map map = new TreeMap();
	private SynchronizationElementQueue queue = new SynchronizationElementQueue();
	private Set woset = new HashSet();

	public SimpleSynchronization() {
		log.debug("constructor");
	}

	public void init(XWikiContext context) {
		log.debug("init");
		this.wiki = context.getWiki();
		this.context = context;
		log.debug("Configuring number of threads");
		threads = (int) context.getWiki().ParamAsLong(THREADS, DEFAULT_THREADS);
		log.debug("Starting " + threads + " threads");
		this.threadPool = new Thread[threads];
		for (int i = 0; i < threads; ++i) {
			threadPool[i] = new Thread(this);
			threadPool[i].start();
		}
	}

	public void processRequest(SynchronizationRequest elements) {
		if (elements.isAsync()) {
			synchronizeAsync(elements);
		} else {
			synchronizeSync(elements);
		}
	}

	private void synchronizeSync(SynchronizationRequest elements) {
		List wobjects = addRequest(elements);
		WrappedSynchronizationElement we;
		for (Iterator it = wobjects.iterator(); it.hasNext();) {
			we = (WrappedSynchronizationElement) it.next();
			log.debug("Request " + elements + " waiting for " + we);
			while (true) {
				if (!contains(we)) {
					break;
				}
				try {
					synchronized (we) {
						log.debug("Request " + elements + " sleeping for " + we);
						we.wait();
						log.debug("Request " + elements + " woke up " + we);
					}
				} catch (InterruptedException ignore) {
				}
			}
		}
	}

	private void synchronizeAsync(SynchronizationRequest elements) {
		addAsyncRequest(elements);
	}

	protected synchronized List addRequest(SynchronizationRequest req) {
		log.debug("addRequest");
		List wobjects = new LinkedList();
		for (Iterator it = req.getElements().iterator(); it.hasNext();) {
			SynchronizationElement el = (SynchronizationElement) it.next();
			WrappedSynchronizationElement we = null;
			if (!map.keySet().contains(el)) {
				we = queue.add(el);
			} else {
				we = new WrappedSynchronizationElement(null, 0);
			}
			List list = (List) map.get(el);
			if (list == null) {
				list = new LinkedList();
			}
			list.add(we);
			map.put(el, list);
			woset.add(we);
			wobjects.add(we);
		}
		return wobjects;
	}

	protected synchronized void addAsyncRequest(SynchronizationRequest req) {
		for (Iterator it = req.getElements().iterator(); it.hasNext();) {
			SynchronizationElement el = (SynchronizationElement) it.next();
			if (!map.keySet().contains(el)) {
				queue.add(el);
			}
			List list = (List) map.get(el);
			if (list == null) {
				list = new LinkedList();
				map.put(el, list);
			}
		}
	}

	protected synchronized boolean contains(WrappedSynchronizationElement we) {
		return woset.contains(we);
	}

	public synchronized boolean hasTasks() {
		return !queue.isEmpty();
	}

	protected synchronized SynchronizationElement pollTask() {
		return queue.poll();
	}

	protected void doTask(SynchronizationElement element) {
		log.debug("Updating task " + element);
		try {
			String docName = element.getDocumentName();
			String docLang = element.getDocumentLanguage();
			XWikiDocument doc = this.wiki.getDocument(docName, context).getTranslatedDocument(docLang, context);

			HandlerStub connection = ConnectorPlugin.getInstance().getConnection(element.getServerName());
			SynchronizationInterface syncProxy = (SynchronizationInterface) connection.getDynamicProxy("synchronization", SynchronizationInterface.class, true);

			Object[] response = syncProxy.sync(docName, docLang, doc.getVersion(), doc.getVersionHashCode(context));
			int responseLength = response.length;
			if (responseLength < 1 || !(response[0] instanceof String)) {
				throw new P2PXWikiException(P2PXWikiException.REMOTE_CALL, "Invalid response");
			}
			String serverStatusResponse = (String) response[0];
			if (serverStatusResponse.equals("ok")) {
				element.setUpdated(false);
				return;
			}
			else if(!serverStatusResponse.equals("nok")){
				throw new P2PXWikiException(P2PXWikiException.REMOTE_CALL, "Invalid response");
			}

			int branchPoint = findBranchPoint(doc, response);
			int crtVersionNumber = Integer.parseInt(doc.getVersion().substring(doc.getVersion().indexOf(".") + 1));
			Object branch[] = new Object[crtVersionNumber - branchPoint];
			for (int i = branchPoint; i < crtVersionNumber; ++i) {
				XWikiDocument version = this.wiki.getDocument(doc, "1." + i, context);
				branch[i] = version.toXML(true, false, true, false, context);
			}

			Object[] mergedDocs = syncProxy.merge(docName, docLang, branch);

			for (int i = 0; i < mergedDocs.length; ++i) {
				try {
					XWikiDocument docVersion = new XWikiDocument();
					docVersion.fromXML((String) mergedDocs[i], false);
					// The documents should be stored exactly as received
					// Needed in order to preserve the version
					docVersion.setContentDirty(false);
					docVersion.setMetaDataDirty(false);
					this.wiki.saveDocument(docVersion, doc, context);
				} catch (XWikiException ex) {
					log.error("XWiki exception", ex);
					// TODO ... handle it?
				}
			}
			element.setUpdated(true);
		} catch (P2PXWikiException ex) {
			// TODO: This is a serious error. What to do now?
			// TODO: Mark the element as synchronized?
			// TODO: Retry with another server?
			// TODO: The network is damaged, a new master should be elected?
			log.error("Cannot communicate with server", ex);
		} catch (XWikiException ex) {
			log.error("XWiki exception", ex);
		} catch (ClassCastException ex) {
			log.error("Invalid response from server", ex);
		} catch (Exception ex) {
			log.error("Unhandled exception", ex);
		}
		return;
	}

	protected synchronized void doneTask(SynchronizationElement element) {
		List list = (List) map.remove(element);
		if (list == null) {
			return;
		}
		for (Iterator it = list.iterator(); it.hasNext();) {
			WrappedSynchronizationElement we = (WrappedSynchronizationElement) it.next();
			woset.remove(we);
			log.debug("Notifying " + we);
			synchronized (we) {
				we.notify();
			}
		}
	}

	protected int findBranchPoint(XWikiDocument doc, Object[] remoteBranch) {
		int crtVersionNumber = Integer.parseInt(doc.getVersion().substring(doc.getVersion().indexOf(".") + 1));
		int min = crtVersionNumber < remoteBranch.length - 1 ? crtVersionNumber : remoteBranch.length - 1;
		int i = 1;
		try{
			for (i = 1; i <= min; ++i) {
				String localVersionHash = wiki.getDocument(doc, "1." + i, context).getVersionHashCode(context);
				if (!localVersionHash.equals(remoteBranch[i])) {
					return i;
				}
			}
		}
		catch(XWikiException ex){
			log.error("Unhandled exception", ex);
			return i;
		}
		return min;
	}

	public void run() {
		while (true) {
			SynchronizationElement element = pollTask();
			doTask(element);
			doneTask(element);
		}
	}
}
