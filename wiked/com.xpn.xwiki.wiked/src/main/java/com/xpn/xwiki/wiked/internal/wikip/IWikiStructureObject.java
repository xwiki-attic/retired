
package com.xpn.xwiki.wiked.internal.wikip;

/**
 * Generic, structure-keeping object
 * @author psenicka_ja
 */
public interface IWikiStructureObject {
	/** @return an object title */
    String getTitle();
	/** @return an object parent */
    IWikiStructureObject getParent();
	/** @return an object children */
    IWikiStructureObject[] getChildren();
	/**
	 * Adds a child 
	 * @param child an object to be added
	 */
    void addChild(IWikiStructureObject child);
}
