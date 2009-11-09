/**
 * 
 */
package adnotatio.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

import adnotatio.common.data.IPropertiesContainer;
import adnotatio.common.xml.IXmlDocument;
import adnotatio.common.xml.SerializationUtil;
import adnotatio.server.impl.AnnotationProvider;
import adnotatio.server.impl.TemplateProvider;
import adnotatio.server.xml.dom.DomXmlDocument;

/**
 * @author kotelnikov
 */
public class ServiceServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1093812091998169567L;

    private IAnnotationProvider fAnnotationProvider;

    private ITemplateProvider fTemplateProvider;

    /**
     * 
     */
    public ServiceServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        fAnnotationProvider = new AnnotationProvider(config);
        fTemplateProvider = new TemplateProvider(config);
    }

    private void printAnnotations(
        ServletOutputStream out,
        List<IPropertiesContainer> annotations) throws IOException {
        try {
            SerializationUtil util = SerializationUtil.getInstance();
            Document doc = DOMUtil.newDocument();
            IXmlDocument d = new DomXmlDocument(doc);
            util.serializeAnnotations(d, annotations);
            printDocument(out, d);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    private void printDocument(ServletOutputStream out, IXmlDocument d)
        throws IOException {
        SerializationUtil util = SerializationUtil.getInstance();
        StringBuffer buf = new StringBuffer();
        util.serializeNode(d.getRootElement(), buf);
        out.print(buf.toString());
    }

    protected void printTemplate(ServletOutputStream out, Document template)
        throws IOException {
        IXmlDocument d = new DomXmlDocument(template);
        printDocument(out, d);
    }

    private List<IPropertiesContainer> readAnnotations(ServletInputStream input)
        throws IOException {
        try {
            Document doc = DOMUtil.readXML(input);
            IXmlDocument d = new DomXmlDocument(doc);
            SerializationUtil util = SerializationUtil.getInstance();
            List<IPropertiesContainer> list = new ArrayList<IPropertiesContainer>();
            util.deserializePropertiesList(d.getRootElement(), list);
            return list;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
    try {
        String docId = req.getParameter("docname");
        if (docId == null) {
           docId = "";
        }

        String action = req.getParameter("action");
        if (action == null) {
            action = "main";
        }
        resp.setContentType("text/html");
        ServletOutputStream out = resp.getOutputStream();
        if ("templateView".equals(action)) {
            Document template = fTemplateProvider.getViewTemplate(docId);
            printTemplate(out, template);
        } else if ("templateForm".equals(action)) {
            Document template = fTemplateProvider.getEditTemplate(docId);
            printTemplate(out, template);
        } else if ("content".equals(action)) {
            String content = fAnnotationProvider.getContent(docId, req, resp);
            out.print(content);
        } else if ("read".equals(action)) {
            List<IPropertiesContainer> annotations = fAnnotationProvider
                .loadAnnotations(docId);
            printAnnotations(out, annotations);
        } else if ("write".equals(action)) {
            ServletInputStream input = req.getInputStream();
            List<IPropertiesContainer> annotations = readAnnotations(input);
            annotations = fAnnotationProvider.storeAnnotations(
                docId,
                annotations);
            printAnnotations(out, annotations);
        } else {
            Document template = fTemplateProvider.getMainPage(docId);
            printTemplate(out, template);
        }
   } catch (Exception e) {
   e.printStackTrace();
   throw new ServletException(e);
  }
    }

}
