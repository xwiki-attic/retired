
package com.xpn.xwiki.wiked.repository;

/**
 * A repository instance wll be created using this interface.
 * The factory implementation will be registered in plugin descriptor
 * file, the platform core will lookup it and instantiate during the 
 * system starup.
 * 
 * @author Psenicka_Ja
 */
public interface IRepositoryFactory {

    /**
     * Create a repository object
     * @param type the repository type (copied from plugin descriptor)
     * @param dataObject Map of attributes read from plugin descriptor or
     * IMemento object.
     * @return newly created repository
     * @throws RepositoryException if the repository cannot be cnnected
     */
    IRepository createRepository(String type, Object dataObject) 
        throws RepositoryException;
}
