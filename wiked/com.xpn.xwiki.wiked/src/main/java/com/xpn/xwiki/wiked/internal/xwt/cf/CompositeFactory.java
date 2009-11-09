
package com.xpn.xwiki.wiked.internal.xwt.cf;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.w3c.dom.Element;

import com.xpn.xwiki.wiked.internal.xwt.AbstractSWTFactory;

/**
 * Composite factory for JavaBeans
 */
public class CompositeFactory extends AbstractSWTFactory {

    protected Object createObject(Widget parent, Element element) {
        if (parent instanceof Composite) {
	        int flags = SWTPropertyReader.getDefault().parse(element);
	        return new Composite((Composite)parent, flags);    
        }
        
        return null;
	}

}