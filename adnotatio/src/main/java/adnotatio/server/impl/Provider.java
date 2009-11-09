/**
 * 
 */
package adnotatio.server.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.w3c.dom.Document;

import adnotatio.common.data.IPropertiesContainer;
import adnotatio.common.xml.IXmlDocument;
import adnotatio.common.xml.SerializationUtil;
import adnotatio.server.DOMUtil;
import adnotatio.server.xml.dom.DomXmlDocument;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.doc.XWikiLock;
import com.xpn.xwiki.gwt.api.client.*;
import com.xpn.xwiki.objects.*;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.objects.classes.ListClass;
import com.xpn.xwiki.render.XWikiVelocityRenderer;
import com.xpn.xwiki.user.api.XWikiUser;
import com.xpn.xwiki.web.Utils;
import com.xpn.xwiki.web.XWikiEngineContext;
import com.xpn.xwiki.web.XWikiMessageTool;
import com.xpn.xwiki.web.XWikiRequest;
import com.xpn.xwiki.web.XWikiResponse;
import com.xpn.xwiki.web.XWikiServletContext;
import com.xpn.xwiki.web.XWikiServletRequest;
import com.xpn.xwiki.web.XWikiURLFactory;
import com.xpn.xwiki.xmlrpc.XWikiXmlRpcResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.*;
import java.lang.reflect.*;

/**
 * @author kotelnikov
 */

class Provider {

   protected ServletConfig fConfig;

   XWikiEngineContext engine;

    public Provider(ServletConfig config) {
        fConfig = config;
    }

    protected XWikiContext getXWikiContext(HttpServletRequest req, HttpServletResponse resp) throws XWikiException {
        if (this.engine==null) {
            ServletContext sContext = null;
            try {
                sContext = fConfig.getServletContext();
            } catch (Exception ignore) { }
            if (sContext != null) {
                engine = new XWikiServletContext(sContext);
            } else {
                // use fake server context (created as dynamic proxy)
                ServletContext contextDummy = (ServletContext)generateDummy(ServletContext.class);
                engine = new XWikiServletContext(contextDummy);
            }
        }

        XWikiRequest  request = new XWikiServletRequest(req); 
        XWikiResponse response = new XWikiXmlRpcResponse(resp);
        XWikiContext context = Utils.prepareContext("", request, response, engine);
        context.setMode(XWikiContext.MODE_GWT);
        context.setDatabase("xwiki");

        XWiki xwiki = XWiki.getXWiki(context);
        XWikiURLFactory urlf = xwiki.getURLFactoryService().createURLFactory(context.getMode(), context);
        context.setURLFactory(urlf);
        XWikiVelocityRenderer.prepareContext(context);
        xwiki.prepareResources(context);

        String username = "XWiki.XWikiGuest";
        if (context.getMode() == XWikiContext.MODE_GWT_DEBUG)
            username = "XWiki.superadmin";
        XWikiUser user = context.getWiki().checkAuth(context);
        if (user != null)
            username = user.getUser();
        context.setUser(username);        

        if (context.getDoc() == null)
            context.setDoc(new XWikiDocument("Fake", "Document"));

        context.put("ajax", new Boolean(true));
        return context;
    }

    private Object generateDummy(Class someClass)
    {
        ClassLoader loader = someClass.getClassLoader();
        InvocationHandler handler = new InvocationHandler()
        {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
            {
                return null;
            }
        };
        Class[] interfaces = new Class[] {someClass};
        return Proxy.newProxyInstance(loader, interfaces, handler);
    }
   
    protected XWikiGWTException getXWikiGWTException(Exception e) {
        // let's make sure we are informed
        // if (log.isErrorEnabled()) {
        //    log.error("Unhandled exception on the server", e);
        // }

        if (e instanceof XWikiGWTException){
            return (XWikiGWTException) e;
        }

        XWikiException exp;
        if (e instanceof XWikiException)
         exp = (XWikiException) e;
        else
         exp = new XWikiException(XWikiException.MODULE_XWIKI_GWT_API, XWikiException.ERROR_XWIKI_UNKNOWN, "Unknown GWT Exception", e);

        return new XWikiGWTException(exp.getMessage(), exp.getFullMessage(), exp.getCode(), exp.getModule());
    }

    /**
     * @param name
     * @return
     */
    private InputStream getResourceStream(String name) {
        return fConfig.getServletContext().getResourceAsStream("/WEB-INF/" + name);
      /*
        Class<?> cls = getClass();
        String path = cls.getPackage().getName();
        path = path.replace('.', '/');
        path = "/" + path;
        path += "/" + name;
        return cls.getResourceAsStream(path); */
    }

    protected Document readDocument(String name) throws IOException {
        InputStream input = getResourceStream(name);
        try {
            return DOMUtil.readXML(input);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        } finally {
            input.close();
        }
    }

    protected Document readDocument(
        String name,
        Map<String, String> replacements) throws IOException {
        String str = readResource(name);
        if (str.equals("")) {
          str = "<xml></xml>";
        }
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String from = entry.getKey();
            String to = entry.getValue();
            str = str.replace(from, to);
        }
        StringReader reader = new StringReader(str);
        try {
            return DOMUtil.readXML(reader);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        } finally {
            reader.close();
        }
    }

    protected List<IPropertiesContainer> readPropertiesContainerList(String name)
        throws IOException {
        Document doc = readDocument(name);
        IXmlDocument d = new DomXmlDocument(doc);
        List<IPropertiesContainer> list = new ArrayList<IPropertiesContainer>();
        SerializationUtil util = SerializationUtil.getInstance();
        util.deserializePropertiesList(d.getRootElement(), list);
        return list;
    }

    protected String readResource(String name) throws IOException {
       ByteArrayOutputStream out = new ByteArrayOutputStream();
       readResource(name, out);
       return new String(out.toByteArray(), "UTF-8");
    }

    protected void readResource(String name, OutputStream out)
        throws IOException {
        InputStream input = getResourceStream(name);
        try {
            try {
               if (input!=null) {
                byte[] buf = new byte[1024 * 10];
                int len;
                while ((len = input.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
               }
            } finally {
                out.flush();
            }
        } finally {
               if (input!=null) 
            input.close();
        }
    }
}
