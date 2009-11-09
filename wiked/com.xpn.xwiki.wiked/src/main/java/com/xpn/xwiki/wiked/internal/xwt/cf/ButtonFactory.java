
package com.xpn.xwiki.wiked.internal.xwt.cf;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.w3c.dom.Element;

import com.xpn.xwiki.wiked.internal.xwt.AbstractSWTFactory;
import com.xpn.xwiki.wiked.internal.xwt.XWTException;

public class ButtonFactory extends AbstractSWTFactory {

    protected Object createObject(Widget parent, Element element) 
		throws XWTException {
	    if (parent instanceof Composite) {
	        int flags = SWTPropertyReader.getDefault().parse(element);
	        return new Button((Composite)parent, flags);
	    }
	    
	    return null;
    }
}
