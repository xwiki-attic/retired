
package com.xpn.xwiki.wiked.repository;

/**
 * Generic wiki-positive object
 * @author Psenicka_Ja
 */
public interface IRepositoryObject {

    /**
     * @return the object name
     */
	String getName();
    
    /**
     * Sets new page name
     * @param name new name
     * @throws RepositoryException if the name cannot be set,
     * typically because of remote server error
     */
    void setName(String name) throws RepositoryException;

    /**
     * Refreshes the object from the source
     * @throws RepositoryException if the object cannot be refreshed,
     * typically because of remote server error
     */
	void refresh();
    
    /**
     * @return true if the object cannot be modified
     * @throws RepositoryException
     */
    boolean isReadOnly();

}