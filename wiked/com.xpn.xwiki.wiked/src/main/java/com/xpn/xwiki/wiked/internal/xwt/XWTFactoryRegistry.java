
package com.xpn.xwiki.wiked.internal.xwt;

import org.w3c.dom.Element;


public interface XWTFactoryRegistry {

    AbstractSWTFactory getFactory(Element element);
}
