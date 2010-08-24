
package com.xpn.xwiki.wiked.internal.xwt.cf;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.eclipse.swt.widgets.Widget;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.xpn.xwiki.wiked.internal.xwt.AbstractSWTFactory;
import com.xpn.xwiki.wiked.internal.xwt.ObjectRegistry;
import com.xpn.xwiki.wiked.internal.xwt.XWTException;

/**
 * Composite factory for JavaBeans
 */
public class InitFactory extends AbstractSWTFactory {

    protected Object createObject(Widget parent, Element element) 
        throws XWTException {
		try {
			Node node = XPathAPI.selectSingleNode(element, "text()");
            if (node != null && node instanceof Text) {
                String code = ((Text)node).getData();
                if (code != null && code.length() > 0) {
                	GroovyExecutor executor = new GroovyExecutor(
                        getObjectRegistry(), getClassLoader());
                	executor.execute(code);
                }
            }
            return null;
		} catch (TransformerException ex) {
			throw new XWTException(ex);
		}
	}

    private static class GroovyExecutor {

        private GroovyShell shell;
    
		public GroovyExecutor(ObjectRegistry widgetRegistry, ClassLoader loader) {
            Binding binding = new Binding();
            String[] ids = widgetRegistry.getObjectIds();
            for (int i = 0; i < ids.length; i++) {
            	binding.setVariable(ids[i], widgetRegistry.getObject(ids[i]));
			}
            this.shell = new GroovyShell(loader, binding);
		}

		public void execute(String code) {
            try {
            	this.shell.evaluate(code);
            } catch (Exception ex) {
                IllegalStateException isex = new IllegalStateException();
                isex.initCause(ex);
            	throw isex;
            }
    	}
    }
    
}