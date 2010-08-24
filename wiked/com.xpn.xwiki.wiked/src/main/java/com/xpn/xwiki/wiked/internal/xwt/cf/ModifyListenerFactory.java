
package com.xpn.xwiki.wiked.internal.xwt.cf;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.TypedListener;
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
public class ModifyListenerFactory extends AbstractSWTFactory {

    protected Object createObject(Widget parent, Element element) 
        throws XWTException {
		try {
			Node node = XPathAPI.selectSingleNode(element, "text()");
            if (node != null && node instanceof Text) {
                String code = ((Text)node).getData();
                if (code != null && code.length() > 0) {
                    GroovyModifyListner listener = new GroovyModifyListner(code, 
                        this.getObjectRegistry(), getClassLoader()); 
                    TypedListener typedListener = new TypedListener (listener);
                    parent.addListener (SWT.Modify, typedListener);
                }
            }
            return null;
		} catch (TransformerException ex) {
			throw new XWTException(ex);
		}
	}

    private static class GroovyModifyListner implements ModifyListener {

        private String code;
        private GroovyShell shell;
    
		public GroovyModifyListner(String code, ObjectRegistry widgetRegistry,
            ClassLoader classLoader) {
			this.code = code;
            Binding binding = new Binding();
            String[] ids = widgetRegistry.getObjectIds();
            for (int i = 0; i < ids.length; i++) {
            	binding.setVariable(ids[i], widgetRegistry.getObject(ids[i]));
			}
            this.shell = new GroovyShell(classLoader, binding);
		}

		public void modifyText(ModifyEvent event) {
            try {
                this.shell.setVariable("event", event);
            	this.shell.evaluate(this.code);
            } catch (Exception ex) {
                IllegalStateException isex = new IllegalStateException();
                isex.initCause(ex);
            	throw isex;
            }
    	}
    }
    
}