
package com.xpn.xwiki.wiked.internal.xwt.cf;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.xpn.xwiki.wiked.internal.xwt.AbstractSWTFactory;
import com.xpn.xwiki.wiked.internal.xwt.XWTException;


public class LabelFactory extends AbstractSWTFactory {

    protected Object createObject(Widget parent, Element element) 
    	throws XWTException {
        if (parent instanceof Composite) {
            try {
		        int flags = SWTPropertyReader.getDefault().parse(element);
		        Label label = new Label((Composite)parent, flags);
		        label.setText(readText(element));
		        return label;
            } catch (TransformerException ex) {
                throw new XWTException(ex);
            }
        }
        
        return null;
    }

    private String readText(Element element) throws TransformerException {
        Node node = XPathAPI.selectSingleNode(element, "text/text()");
        if (node != null) {
            String text = ((Text)node).getData();
            return (text != null) ? text : "";
        }
        
        return "";
    }

}
