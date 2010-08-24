
package com.xpn.xwiki.wiked.repository;

/**
 * Wiki page interface.
 * You typically implement the interface when writing new repository 
 * support, leaf objects should be pages, therefore implement this 
 * interface.
 * 
 * @author Psenicka_Ja
 */
public interface IPage extends IRepositoryObject {

    /** 
     * @return the space containing the page, the method must not
     * return <code>null</code>. All pages must know it's parent.
     */
	ISpace getSpace();
	
    /** 
     * @return the page content
     * @exception RepositoryException if the content cannot be retrieved,
     * e.g. when remote server haven't provided neccessary data.
     */
	String getContent() throws RepositoryException;

	/**
     * Sets new content
	 * @param content new content
     * @throws RepositoryException if the content cannot be set,
     * typically because of remote server error
	 */
    void setContent(String content) throws RepositoryException;
	
    /** 
     * @return the new, human-readable page title. The title is typically
     * derived from the file name (for local repository) or supported
     * by Wiki engine (for remote repositories).
     */
	String getTitle();
    
    /**
     * Sets new title
     * @param title a title
     * @throws RepositoryException if the content cannot be set,
     * typically because of remote server error.
     */
	void setTitle(String title) throws RepositoryException;
	
}
