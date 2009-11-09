/**
 * 
 */
package adnotatio.server.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.http.*;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

import adnotatio.common.data.IPropertiesContainer;
import adnotatio.server.IAnnotationProvider;

import com.xpn.xwiki.*;
import com.xpn.xwiki.doc.*;
/**
 * @author kotelnikov
 */
public class AnnotationProvider extends Provider implements IAnnotationProvider {

	private List<IPropertiesContainer> fList;

	/**
	 * @param config
	 */
	public AnnotationProvider(ServletConfig config) {
		super(config);
	}

	/**
	 * @see adnotatio.server.IAnnotationProvider#getContent(java.lang.String,HttpServletRequest)
	 */
	public String getContent(String docId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
	  try {
                XWikiContext context = getXWikiContext(req,resp);
		return context.getWiki().getDocument(docId, context).getRenderedContent(context);
          } catch (XWikiException e) {
                return e.getFullMessage();
          }
	}

	/**
	 * @see adnotatio.server.IAnnotationProvider#loadAnnotations(java.lang.String)
	 */
	public List<IPropertiesContainer> loadAnnotations(String docId)
			throws IOException {
		if (fList == null) {
			fList = new ArrayList<IPropertiesContainer>(); // readPropertiesContainerList("HtmlAnnotator-annotations.xml");
		}
		return fList;
	}

	/**
	 * @see adnotatio.server.IAnnotationProvider#storeAnnotations(java.lang.String,
	 *      java.util.List)
	 */
	public List<IPropertiesContainer> storeAnnotations(String docId,
			List<IPropertiesContainer> annotations) throws IOException {
		List<IPropertiesContainer> list = loadAnnotations(docId);
		list.addAll(0, annotations);
		return annotations;
	}

}
