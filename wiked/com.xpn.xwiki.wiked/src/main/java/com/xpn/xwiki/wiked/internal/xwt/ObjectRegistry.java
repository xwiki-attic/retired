
package com.xpn.xwiki.wiked.internal.xwt;

/**
 * A registry of objects identified by unique ID.
 * @author psenicka_ja
 */
public interface ObjectRegistry {
	
	/** @return all object's ids */
    String[] getObjectIds();
    /** @return an object identified by given id or <code>null</code>. */
    Object getObject(String id);
    
}
