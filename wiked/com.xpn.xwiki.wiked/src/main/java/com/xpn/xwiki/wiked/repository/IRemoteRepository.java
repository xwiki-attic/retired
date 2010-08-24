
package com.xpn.xwiki.wiked.repository;

import java.net.URL;

/**
 * Remote repository interface.
 * You have to implement this interface (plus ISpace and IPage, at least)
 * in order to add a new repository support. The instance will be created
 * using IRepositoryFactory (created by the Eclipse core according to the
 * plugin specification.
 * 
 * The interface adds remote support for generic IRepository in form of
 * the URL and login information.
 * 
 * @author Psenicka_Ja
 */
public interface IRemoteRepository extends IRepository {

    String ATTR_NAME = "name";
    String ATTR_URL = "url";
    String ATTR_USERNAME = "user";
    String ATTR_PASSWORD = "password";

    /**
     * @return the repository URL
     */
    URL getURL();

	/**
	 * @return the user name
	 */
	String getUserName();

	/**
	 * @return the password
	 */
	String getPassword();

	/**
     * Sets the user name
	 * @param userName the user name to be set
     * @throws RepositoryException if the username set cannot be set,
     * typically because of remote server error
	 */
	void setUserName(String userName)
        throws RepositoryException;

	/**
     * Sets the password
	 * @param password the password to be set
     * @throws RepositoryException if the password set cannot be set,
     * typically because of remote server error
	 */
	void setPassword(String password)
        throws RepositoryException;

	/**
     * Sets the url 
	 * @param url the ulr to be set
     * @throws RepositoryException if the url set cannot be set,
     * typically because of remote server error
	 */
	void setURL(URL url) throws RepositoryException;
}