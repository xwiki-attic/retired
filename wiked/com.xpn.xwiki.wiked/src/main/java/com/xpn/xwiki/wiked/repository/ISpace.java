
package com.xpn.xwiki.wiked.repository;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Interface representing the space
 * @author Psenicka_Ja
 */
public interface ISpace extends IRepositoryObject {
    
    /**
     * @return owning repository
     */
	IRepository getRepository();
    
    /**
     * Return all pages belonging to this space.
     * @param monitor the progress monitor, may be null.
     * @return all pages
     */
    IPage[] getPages(IProgressMonitor monitor);
    
    /**
     * Creates new page.
     * @param spaceName a space name
     * @return newly created space
     * @throws SecurityException
     * @throws RepositoryException
     */
	IPage createPage(String pageName) 
        throws RepositoryException;
	
    /**
     * Removes a page under given name
     * @param name the page name
     * @throws RepositoryException
     */
    IPage removePage(String name) 
        throws RepositoryException;
}
