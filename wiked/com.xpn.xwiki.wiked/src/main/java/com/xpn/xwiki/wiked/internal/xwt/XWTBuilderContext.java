
package com.xpn.xwiki.wiked.internal.xwt;

/**
 * Context passed to SWT factories.
 * @author psenicka_ja
 */
public interface XWTBuilderContext {

	/** @return the classloader used */
	ClassLoader getClassLoader();
    
	/** 
	 * @return factory registry, use to lookup for factory for 
	 * given W3C element.
	 */
    XWTFactoryRegistry getFactoryRegistry();
    
	/** 
	 * @return object registry, use to lookup for object for 
	 * given id.
	 */
    ObjectRegistry getObjectRegistry();
}
