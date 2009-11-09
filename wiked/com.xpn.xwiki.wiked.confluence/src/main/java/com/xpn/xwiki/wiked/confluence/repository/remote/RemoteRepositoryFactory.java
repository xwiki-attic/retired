
package com.xpn.xwiki.wiked.confluence.repository.remote;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.eclipse.ui.IMemento;

import com.xpn.xwiki.wiked.repository.IRemoteRepository;
import com.xpn.xwiki.wiked.repository.IRepository;
import com.xpn.xwiki.wiked.repository.IRepositoryFactory;
import com.xpn.xwiki.wiked.repository.RepositoryException;

/**
 * 
 * @author Psenicka_Ja
 */
public class RemoteRepositoryFactory implements IRepositoryFactory {

	/**
	 * @see com.xpn.xwiki.wiked.repository.IRepositoryFactory#createRepository(java.lang.Object)
	 */
	public IRepository createRepository(String type, Object dataObject)
		throws RepositoryException {
        if (dataObject instanceof IMemento) {
            try {
                IMemento bookmark = (IMemento)dataObject;
                String url = bookmark.getTextData();
                RemoteRepository r = new RemoteRepository(type, 
                    bookmark.getString(IRemoteRepository.ATTR_NAME), new URL(url));
                r.setUserName(bookmark.getString(IRemoteRepository.ATTR_USERNAME));
                r.setPassword(bookmark.getString(IRemoteRepository.ATTR_PASSWORD));
                return r;
            } catch (Exception ex) {
            	throw new RepositoryException(ex);
            }
        } else if (dataObject instanceof Map) {
            try {
                Map map = (Map)dataObject;
                RemoteRepository r = new RemoteRepository(type, 
                    (String)map.get(IRemoteRepository.ATTR_NAME), 
                    new URL((String)map.get(IRemoteRepository.ATTR_URL)));
                r.setUserName((String)map.get(IRemoteRepository.ATTR_USERNAME));
                r.setPassword((String)map.get(IRemoteRepository.ATTR_PASSWORD));
                return r;
            } catch (MalformedURLException ex) {
            	throw new RepositoryException(ex);
            }
        }
		return null;
	}

}
