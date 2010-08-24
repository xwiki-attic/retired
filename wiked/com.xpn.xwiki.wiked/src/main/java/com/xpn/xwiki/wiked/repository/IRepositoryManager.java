
package com.xpn.xwiki.wiked.repository;

/**
 * The manager of repositories.
 * Allows registration of repository and coresponding listener. The 
 * interface is not intended to be implemented by implementors. The
 * interface implementation is acessible via WikedPlugin accessor.
 * 
 * @see com.xpn.xwiki.wiked.internal.WikedPlugin#getRepositoryManager()
 * @author Psenicka_Ja
 */
public interface IRepositoryManager {
    
    /**
     * @return all registered repository types, repository types are
     * registered via <code>com.xpn.xwiki.wiked.repository</code> extension
     * point.
     */
    String[] getRepositoryTypes();

    /**
     * @return a descriptor og given type or null if there is not 
     * such repository descriptor. 
     */
    RepositoryDescriptor getRepositoryDescriptor(String type);
    
    /**
     * @return all known repositories. Repositories are added using
     * getRepository() and removed by removeRepository() methods. The
     * IRepositoryListener is informed about every change, if registered. 
     */
    IRepository[] getRepositories();

    /**
     * Returns the repository entry that is mapped to the given name.
     * @param type the repository type to retrieve
     * @return the IRepository instance of <code>null</code> if there
     * is no such repository registered under given type. The method must
     * not return <code>null</code> for type previously returned from the
     * getRepositoryTypes() method.
     */
    IRepository getRepository(String type);

    /**
     * Create and add new repository
     * Locates registered factory for given type and invokes the 
     * <code>createRepository</code> with the dataobject parameter.
     * @param type a repository type
     * @param dataObject a data object
     * @return newly created repository
     */
    IRepository createRepository(String type, Object dataObject)
        throws RepositoryException;

    /**
     * Adds a new repository. 
     * If a configuration is already mapped to the name of the new 
     * configuration, it will be replaced. The IRepositoryListener 
     * is informed about every added repository, if registered.
     */
    void addRepository(IRepository repository);

    /**
     * Removes the specified repository.
     * The IRepositoryListener is informed about every added repository, 
     * if registered.
     * @param repositoryName the repository name
     */
    void removeRepository(String repositoryName);
     
    /**
	 * Adds a <code>IRepositoryListener</code> to the collection of 
     * listeners.
	 * @param listener the listener to be informed
	 */
    void addChangeListener(IRepositoryListener listener);

	/**
	 * Removes the IRepositoryListener from the collection of listeners,
	 * @param listener the listener to be removed
	 */
    void removeChangeListener(IRepositoryListener listener);

     /**
      * Reloads all repositories
      */
    void refresh() throws RepositoryException;

}
