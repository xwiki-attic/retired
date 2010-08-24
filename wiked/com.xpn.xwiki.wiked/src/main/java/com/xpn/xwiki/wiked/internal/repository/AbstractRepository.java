
package com.xpn.xwiki.wiked.internal.repository;

import java.net.URL;

import org.eclipse.core.runtime.PlatformObject;

import com.xpn.xwiki.wiked.repository.IRepository;
import com.xpn.xwiki.wiked.repository.IRepositoryManager;
import com.xpn.xwiki.wiked.repository.RepositoryObjectChangeEvent;

public abstract class AbstractRepository extends PlatformObject implements IRepository {

    private RepositoryManager manager;
    private URL url;

	public AbstractRepository(URL url) {
        this.url = url;
	}

    /**
	 * @see com.xpn.xwiki.wiked.repository.IRepository#setManager(com.xpn.xwiki.wiked.repository.IRepositoryManager)
	 */
	public void setManager(IRepositoryManager manager) {
        if (manager instanceof RepositoryManager == false) {
        	throw new IllegalArgumentException("unknown manager");
        }
        this.manager = (RepositoryManager)manager;
	}
    
    public URL getURL() {
        return this.url;
    }
    
    public void setURL(URL url) {
    	this.url = url;
        notifyListeners(new RepositoryObjectChangeEvent(this, 
        	RepositoryObjectChangeEvent.MODIFY, "url"));
    }
    
    public void notifyListeners(RepositoryObjectChangeEvent event) {
        if (this.manager != null) {
        	this.manager.notifyListeners(event);
        }
    }

    protected RepositoryManager getManager() {
    	return manager;
    }
}
