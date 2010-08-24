
package com.xpn.xwiki.wiked.repository;

/**
 * Get informed about repository change. 
 * The listener receives notifications related to repository, spaces and 
 * pages creation, modification and removal. The listener interface will
 * be used for error notification (for all repository objects) as well.
 * 
 * @see com.xpn.xwiki.wiked.repository.RepositoryObjectChangeEvent
 * @author Psenicka_Ja
 */
public interface IRepositoryListener {

    /**
	 * A repository changed event. All subclasses of
	 * RepositoryObjectChangeEvent (repository/space/page) may be
	 * used here.  
	 */
	void repositoryChanged(RepositoryObjectChangeEvent event);

}
