/*
 * Copyright 2006, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * @author ludovic
 * @author vmassol
 * @author sdumitriu
 * @author thomas
 */

package com.xpn.xwiki.wikimodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.semanticdesktop.rdf.RdfException;
import org.semanticdesktop.rdf.impl.ISesame2Const;
import org.semanticdesktop.rdf.impl.RdfRepository;
import org.semanticdesktop.wiki.core.WikiAdapterManager;
import org.semanticdesktop.wiki.core.WikiResource;
import org.semanticdesktop.wiki.core.adapter.semantic.ILinkTranslator;
import org.semanticdesktop.wiki.core.adapter.semantic.SemanticWikiPageFactory;
import org.semanticdesktop.wiki.core.adapter.semantic.TripleIndex;
import org.semanticdesktop.wiki.core.adapter.source.SourceWikiPage;
import org.semanticdesktop.wiki.core.adapter.source.SourceWikiPageFactory;
import org.semanticdesktop.wikimodel.WikiParserException;
import org.semanticdesktop.wikimodel.wom.IWikiNodeFactory;
import org.semanticdesktop.wikimodel.wom.impl.WikiNodeFactory;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiConfig;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.DBStringListProperty;
import com.xpn.xwiki.objects.LargeStringProperty;
import com.xpn.xwiki.objects.StringProperty;
import com.xpn.xwiki.web.XWikiEngineContext;

public class XWikiToWikiModelConverter {
	public static final String HIB_LOCATION = "/hibernate.cfg.xml";

	protected XWiki xwiki;

	protected XWikiConfig config;

	protected XWikiContext context;

	protected int iCount = 0;

	public void init(String databaseName, final String rootPath) throws Exception {
		getConfig();

		this.context = new XWikiContext();
		this.context.setDatabase(databaseName);

		XWikiEngineContext engineContext = new XWikiEngineContext() {

			public Object getAttribute(String name) {
				// TODO Auto-generated method stub
				return null;
			}

			public String getMimeType(String filename) {
				// TODO Auto-generated method stub
				return null;
			}

			public String getRealPath(String path) {
				// TODO Auto-generated method stub
				return rootPath + path;
			}

			public URL getResource(String name) throws MalformedURLException {
				// TODO Auto-generated method stub
				return null;
			}

			public InputStream getResourceAsStream(String name) {
				String path = getRealPath(name);
				File f = new File(path);
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(f);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return fis;

			}

			public void setAttribute(String name, Object value) {
				// TODO Auto-generated method stub

			}

		};

		this.xwiki = new XWiki(this.config, this.context, engineContext, false);
		this.xwiki.setDatabase(databaseName);
		this.context.setWiki(this.xwiki);
		xwiki.setEngineContext(engineContext);

	}

	protected void getConfig() {
		this.config = new XWikiConfig();
		this.config.put("xwiki.store.class", "com.xpn.xwiki.store.XWikiHibernateStore");
		URL resource = getClass().getResource(XWikiToWikiModelConverter.HIB_LOCATION);
		this.config.put("xwiki.store.hibernate.path", resource.getFile());
		this.config.put("xwiki.backlinks", "0");
		this.config.put("xwiki.store.cache", "1");
		this.config.put("xwiki.store.cache.capacity", "100");
		this.config.put("xwiki.virtual", "1");
	}

	public static void main(String[] args) {
		XWikiToWikiModelConverter converter = new XWikiToWikiModelConverter();
		try {

			converter.init("nepomuk", "/home/sebastocha/Workspace1/com.xpn.xwiki/src/main/web/");
			String sql = "where doc.name not in ('Menu','Toolbar','MyInfo','TagListBox') order by doc.name";
			converter.convert(sql, 0, 0, "SemDesk.Tag", "SemDesk.Tags");
			
			// converter.convert(sql, 0, 0, "KB.Tag", "KB.Tags");
			// converter.convert(sql, 0, 0, "Beagle.Tag", "Beagle.Tags");

			/*RdfRepository fRepository = converter.newRdfRepository("/home/sebastocha/test.db","rdfindex.bin");
			TripleIndex index = new TripleIndex(fRepository, converter.new XWikiLinkTranslator());
			WikiAdapterManager.register(new SemanticWikiPageFactory(index));
			WikiAdapterManager.register(new SourceWikiPageFactory());
			WikiAdapterManager.register(new XWikiAdapterFactory());*/

			// String sql = "where doc.web='KB' and doc.name not in
			// ('Menu','Toolbar','WebHome') order by doc.name";
			
			//List<String> documentNames = converter.fetchDocumentNames(sql, 0, 0);
			//Iterator<String> itDoc = documentNames.iterator();
			//converter.indexDocuments(itDoc);
			
			// converter.convertTagToProperty("Association", "Type", itDoc);

			
			//fRepository.close();
			

			// converter.init("nepomuk");
			// converter.convert("SemDesk.Tag", "SemDesk.Tags");
			System.exit(1);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private RdfRepository newRdfRepository(String path, String path1) throws RdfException {
		RdfRepository fRepository = new RdfRepository();
		Properties properties = new Properties();
		File file = new File(path, path1);
		file.getParentFile().mkdirs();
		properties.put(ISesame2Const.KEY_SESAME_FILE_NAME, file.getAbsolutePath());
		fRepository.open(properties);
		return fRepository;

	}

	public void convert(String sql, int skip, int max, String tagClassName, String taglistClassName)
			throws XWikiException {
		List<String> documentNames = fetchDocumentNames(sql, skip, max);

		Iterator<String> itDoc = documentNames.iterator();
		int counter = 0;
		while (itDoc.hasNext()) {
			counter++;
			String fullName = itDoc.next();
			XWikiDocument doc;
			try {
				doc = xwiki.getDocument(fullName, context);

				System.out.println("*********************** Handling document " + fullName);

				// System.out.println("Content: " + doc.getContent());
				reformatContent(doc);
				convertTagsToProperties(doc, tagClassName, taglistClassName);

				System.out.println("-----------------------------------");
				System.out.println("Converted content:" + doc.getContent());
				System.out.println("-----------------------------------");
				System.out.println("Counter:" + counter);
				//xwiki.saveDocument(doc, context);
			} catch (XWikiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private List<String> fetchDocumentNames(String sql, int skip, int max) throws XWikiException {
		List<String> result = (List<String>) xwiki.getStore().searchDocumentsNames(sql, max, skip, context);
		return result;
	}

	public void reformatContent(XWikiDocument document) {
		String reformattedContent = null;
		
		WikiResource resource = getWikiResource(document);
		SourceWikiPage source = (SourceWikiPage)resource.getAdapter(SourceWikiPage.class);
		
		document.setContent(source.getSource());

	}

	public void convertTagsToProperties(XWikiDocument document, String tagClassName, String taglistClassName) {

		StringBuffer newcontent = new StringBuffer();
		newcontent.append(document.getContent());
		newcontent.append("\n\n");

		try {
			Vector tags = document.getObjects(taglistClassName);
			Vector tagVector = document.getObjects(tagClassName);

			if (tags != null && tags.size() > 0) {
				newcontent.append("@[Tags]");
				Iterator it = tags.iterator();
				while (it.hasNext()) {
					BaseObject tagObject = (BaseObject) it.next();
					DBStringListProperty prop = (DBStringListProperty) tagObject.getField("list");
					List list = prop.getList();
					for (int j = 0; list != null && j < list.size(); j++) {
						String tag = (String) list.get(j);
						newcontent.append(" [" + tag + "]");
					}
				}
				document.setContent(newcontent.toString());

			} else

			if (tagVector != null && tagVector.size() > 0) {
				BaseObject tagObject = (BaseObject) tagVector.get(0);
				StringProperty name = (StringProperty) tagObject.getField("name");
				newcontent.append("\n\n@[Type] [Class]");
				LargeStringProperty description = (LargeStringProperty) tagObject.getField("description");
				if (description != null && description.getValue() != null
						&& description.getValue().toString().length() > 0)
					newcontent.append("\n\n@[Description] " + description.getValue());
				document.setContent(newcontent.toString());

			}

		} catch (Exception e) {

			System.out.println("Exception: " + e.getMessage());
		}

	}

	public WikiResource getWikiResource(XWikiDocument doc) {
		try {
			WikiResource resource = (WikiResource) doc.getCustomData("wikiResource");
			if (resource == null) {
				URI uri = getDocUri(doc);
				IWikiNodeFactory nodeFactory = new WikiNodeFactory();
				resource = new WikiResource(nodeFactory, uri);
				String content = doc.getContent();
				SourceWikiPage source = (SourceWikiPage) resource.getAdapter(SourceWikiPage.class);
				source.setSource(new StringReader(content));
				doc.setCustomData("wikiResource", resource);
				XWikiAdapter adapter = (XWikiAdapter) resource.getAdapter(XWikiAdapter.class);
				adapter.setXWikiDocument(doc);
			}
			return resource;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private URI getDocUri(XWikiDocument doc) {
		String uri = URLEncoder.encode(doc.getFullName());
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			return null;
		}
	}

	public void indexDocuments(Iterator<String> itDoc) {
		int counter = 0;
		while (itDoc.hasNext()) {
			String documentName = itDoc.next();
			counter++;
			System.out.println("Indexing: " + counter + " - " + documentName);
			try {
				XWikiDocument document = xwiki.getDocument(documentName, context);
				WikiResource resource = getWikiResource(document);
				// SemanticWikiPage page =
				// (SemanticWikiPage)resource.getAdapter(SemanticWikiPage.class);
				String content = document.getContent();
				SourceWikiPage source = (SourceWikiPage) resource.getAdapter(SourceWikiPage.class);
				
				if (source != null) {
					source.setSource(new StringReader(content));
					resource.store(null);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

		}

	}

	public void convertTagToProperty(String tagValue, String targetProperty, Iterator<String> itDoc) {
		int counter = 0;
		while (itDoc.hasNext()) {
			String documentName = itDoc.next();
			counter++;

			try {
				XWikiDocument document = xwiki.getDocument(documentName, context);
				String content = document.getContent();
				String newcontent = content;
				// DocumentWrapper dw =
				// DocumentWrapperManager.getInstance().getWrapper(document,
				// context);

				String[] references = null;// dw.getReferences("Tags");
				List<String> refList = Arrays.asList(references);
				if (refList.contains("Main." + tagValue)) {
					newcontent = newcontent.replaceAll("\\[Type\\] \\[" + tagValue + "\\]", "");
					newcontent = newcontent.replaceAll("@\\[Tags\\] \\[Main." + tagValue + "\\]", "");
					newcontent = newcontent.replaceAll("\\[" + tagValue + "\\]", "");
					newcontent = newcontent.replaceAll("\\[Main." + tagValue + "\\]", "");
					newcontent = newcontent + ("\n\n@[" + targetProperty + "] [" + tagValue + "]");
					document.setContent(newcontent);
					System.out.println(counter + " - " + documentName);
					System.out.println("-----------------------------------");
					System.out.println(content);
					System.out.println("-----------------------------------");
					System.out.println(newcontent);
					System.out.println("-----------------------------------");
					//xwiki.saveDocument(document, context);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public class XWikiLinkTranslator implements ILinkTranslator {

		public String getLinkFromUri(String uri) {
			System.out.println("getLinkFromUri:" + uri);
			if (uri.startsWith("wiki:")) {
				uri = uri.substring("wiki:".length());
				if (uri.startsWith("Main."))
					uri = uri.substring("Main.".length());
			}
			return uri;
		}

		public String getUriFromLink(String link) {
			System.out.println("getUriFromLink:" + link);
			if (link.startsWith("http:") || link.startsWith("https:") || link.startsWith("ftp:")
					|| link.startsWith("mailto:"))
				return link;
			if (link.indexOf("Main.") < 0)
				link = "Main." + link;
			return "wiki:" + link;
		}

	}

}
