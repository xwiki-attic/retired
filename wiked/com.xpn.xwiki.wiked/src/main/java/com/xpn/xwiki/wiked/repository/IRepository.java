
package com.xpn.xwiki.wiked.repository;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Repository interface.
 * Repository represents a root of data structure containing spaces
 * and pages. You have to implement this interface in order to write new
 * repository support.
 * 
 * @see com.xpn.xwiki.wiked.repository.IRemoteRepository
 * @author Psenicka_Ja
 */
public interface IRepository extends IRepositoryObject {
    
	/**
	 * @return the repository type (code). Repository type is specified
     * using plugin.xml.
	 */
    String getType();
    
    /**
     * Return all spaces in the repository.
     * @param monitor the monitor to be used
     * @return all spaces 
     * @throws RepositoryException if the space set cannot be retrieved,
     * typically because of remote server error
     */
    ISpace[] getSpaces(IProgressMonitor monitor) 
        throws RepositoryException;

    /**
     * Create a new space.
     * @param name the space name to be used
     * @return newly created space
     * @throws RepositoryException if the space set cannot be created,
     * typically because of remote server error
     */
    ISpace createSpace(String name) throws RepositoryException;
    
    /**
     * Remove a space.
     * @param name the space name to be removed
     * @return removed space
     * @throws RepositoryException if the space set cannot be removed,
     * typically because of remote server error
     */
	ISpace removeSpace(String name) throws RepositoryException;

	/**
     * Render content of given page
	 * @return HTML data
	 * @throws RepositoryException
	 */
	String renderContent(String content) throws RepositoryException;
}