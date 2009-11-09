/**
 * 
 */
package com.xpn.xwiki.wikimodel;

import groovy.lang.Writable;
import groovy.text.Template;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.semanticdesktop.wiki.core.WikiResource;
import org.semanticdesktop.wiki.core.WikiResourceAdapter;
import org.semanticdesktop.wiki.core.adapter.html.DefaultWikiLinkFormatter;
import org.semanticdesktop.wiki.core.adapter.html.DocumentFormatter;
import org.semanticdesktop.wiki.core.adapter.html.IWikiLinkFormatter;
import org.semanticdesktop.wikimodel.impl.WikiPageUtil;
import org.semanticdesktop.wikimodel.wom.IWikiBlockPlugin;
import org.semanticdesktop.wikimodel.wom.IWikiDocumentInclusion;
import org.semanticdesktop.wikimodel.wom.IWikiDocumentSection;
import org.semanticdesktop.wikimodel.wom.IWikiInlinePlugin;
import org.semanticdesktop.wikimodel.wom.IWikiNode;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Context;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.api.XWiki;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.render.groovy.GroovyTemplateEngine;

/**
 * @author sebastocha
 * 
 */
public class XWikiAdapter extends WikiResourceAdapter {

	private final class XWikiDocumentFormatter extends DocumentFormatter {
		private final XWikiContext context;

		private final IWikiLinkFormatter formatter;

		private XWikiDocumentFormatter(XWikiContext context, IWikiLinkFormatter formatter) {
			this.context = context;
			this.formatter = formatter;
		}

		private String evaluate(String content, String name, Map gcontext) {
			try {
				GroovyTemplateEngine engine = new GroovyTemplateEngine();
				Template template = engine.createTemplate(content);
				Writable writable = template.make(gcontext);
				String result = writable.toString();
				return result;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
		}

		@Override
		protected void executePlugin(IWikiBlockPlugin blockPlugin, PrintWriter writer) {
			onPlugin(blockPlugin.getContent(), writer);
		}

		@Override
		protected void executePlugin(IWikiInlinePlugin inlinePlugin, PrintWriter writer) {
			onPlugin(inlinePlugin.getContent(), writer);
		}

		public void formatLink(String label, String uri, PrintWriter writer) {
			formatter.formatLink(label, uri, writer);
		}

		@Override
		protected void includeEntity(IWikiDocumentInclusion inclusion, PrintWriter writer) {
			// TODO Auto-generated method stub
			com.xpn.xwiki.XWiki xwiki = context.getWiki();
			try {
				String uri = inclusion.getReference();
				XWikiDocument includedDoc = xwiki.getDocument(uri, context);
				// :TODO: avoid infinite loops
				WikiResource resource = xwiki.getWikiResource(includedDoc);
				XWikiAdapter xresource = (XWikiAdapter) resource.getAdapter(XWikiAdapter.class);
				String includedDocumentHtml = xresource.toHtml(context);
				writer.write(includedDocumentHtml);
			} catch (XWikiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void onPlugin(String pluginContent, PrintWriter writer) {
			if (pluginContent.startsWith("groovy:")) {
				pluginContent = pluginContent.substring("groovy:".length());
				if (context != null && !context.getWiki().getRightService().hasProgrammingRights(xwikidoc, context)) {
					return;
				} else {
					Map gcontext = prepareContext(context);
					gcontext.put("doc", new Document(xwikidoc, context));
					gcontext.put("tdoc", new Document((XWikiDocument) context.get("tdoc"), context));
					String result = evaluate(pluginContent, "ubu", gcontext);
					writer.write(result);
		
				}
			} else if (pluginContent.startsWith("html:")) {
				pluginContent = pluginContent.substring("html:".length());
				writer.write(pluginContent);
			}
		}

		private Map prepareContext(XWikiContext context) {
			Map gcontext = (Map) context.get("gcontext");
			if (gcontext == null) {
				gcontext = new HashMap();
				gcontext.put("xwiki", new XWiki(context.getWiki(), context));
				gcontext.put("request", context.getRequest());
				gcontext.put("response", context.getResponse());
				gcontext.put("context", new Context(context));
				// Put the Grrovy Context in the context
				// so that includes can use it..
				context.put("gcontext", gcontext);
				// add XWikiMessageTool to the context
				if (context.get("msg") != null)
					gcontext.put("msg", context.get("msg"));
				else
					context.getWiki().prepareResources(context);
			}
			return gcontext;
		}
	}

	private XWikiDocument xwikidoc;

	/**
	 * @param wikiResource
	 */
	public XWikiAdapter(WikiResource wikiResource) {
		super(wikiResource);
	}

	public String getAdapterName() {
		return "xresource";
	}

	public String getHtml(String sectionKey, XWikiContext context) {
		IWikiDocumentSection section = fWikiResource.getSection(sectionKey);
		return toHtml(section, false, context);
	}
	
	public String formatLink(String uri, XWikiContext context) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(baos);
		IWikiLinkFormatter linkFormatter = getLinkFormatter(context);
		linkFormatter.formatLink(uri, uri, pw);
		pw.flush();
		String result = baos.toString();
		return result;
	}

	public IWikiLinkFormatter getLinkFormatter(XWikiContext context) {
		IWikiLinkFormatter linkFormatter = new DefaultWikiLinkFormatter() {
			@Override
			protected String formatInternalLink(String link) {
				if (link.indexOf("image:") == 0) {
					link = link.substring("image:".length());
					link = "../../download/" + xwikidoc.getWeb() + "/" + xwikidoc.getName() + "/" + link;
					return link;
				}

				if (link.indexOf('.') > 0) {
					link = link.replace('.', '/');
					link = "../" + link;
				}
				return link;
			}
			


			@Override
			public void formatLink(String label, String uri, PrintWriter writer) {
				if (uri == null)
					return;
				if (label == null || "".equals(label))
					label = uri;
				label = WikiPageUtil.escapeXmlString(label);
				// uri = WikiPageUtil.escapeXmlString(uri, true);
				uri = WikiPageUtil.encodeHttpParams(uri);

				String schema = getSchema(uri);
				boolean external = isExternal(schema);
				String link = uri;
				boolean isImageLink = link.indexOf("image:") == 0;

				if (!external)
					link = formatInternalLink(uri);
				if (isImageLink) {
					writer.print("<img src='" + link + "'/>");
				} else
					writer.print("<a href='" + link + "'" + (external ? " target='_blank'" : "") + ">"
							+ label + "</a>");
			}

			/**
			 * @param uri
			 * @return the schema for the given uri
			 */
			public String getSchema(String uri) {
				int schemaPos = uri.indexOf(':');
				String schema = "";
				if (schemaPos >= 0) {
					schema = uri.substring(0, schemaPos);
				}
				return schema;
			}
		};
		return linkFormatter;
	}

	public void setXWikiDocument(XWikiDocument doc) {
		xwikidoc = doc;
	}

	/**
	 * @param node
	 * @param includeNode
	 * @return the html representation of the given node
	 */
	public String toHtml(IWikiNode node, boolean includeNode, final XWikiContext context) {
		if (node == null)
			return "";
		final IWikiLinkFormatter linkFormatter = getLinkFormatter(context);
		return new XWikiDocumentFormatter(context, linkFormatter).toHtml(node, includeNode);

	}

	public String toHtml(XWikiContext context) {
		StringBuffer buffer = new StringBuffer();
		String[] sections = fWikiResource.getAllSectionKeys();
		for (int i = 0; sections != null && i < sections.length; i++) {
			buffer.append(getHtml(sections[i], context));
		}
		return buffer.toString();

	}
	
	public String[] getAllSectionKeys() {
		return fWikiResource.getAllSectionKeys();
	}

}
