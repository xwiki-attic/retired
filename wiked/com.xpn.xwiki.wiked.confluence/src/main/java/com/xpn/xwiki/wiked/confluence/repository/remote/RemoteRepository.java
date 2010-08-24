
package com.xpn.xwiki.wiked.confluence.repository.remote;

import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.repository.AbstractRepository;
import com.xpn.xwiki.wiked.internal.ui.WikedImageRegistry;
import com.xpn.xwiki.wiked.repository.IBookmarkSaveParticipant;
import com.xpn.xwiki.wiked.repository.IRemoteRepository;
import com.xpn.xwiki.wiked.repository.ISpace;
import com.xpn.xwiki.wiked.repository.RepositoryDescriptor;
import com.xpn.xwiki.wiked.repository.RepositoryException;
import com.xpn.xwiki.wiked.repository.RepositoryObjectChangeEvent;

public class RemoteRepository extends AbstractRepository 
    implements IRemoteRepository, IWorkbenchAdapter, IPropertySource, 
    IBookmarkSaveParticipant {

    private String type;
	private String name;
    private String userName;
    private String password;

    private Map serverInfo;
    private Map spaces;

    private String errorMessage; 
    private RemoteRepositoryClient rpcClient;

    private IPropertyDescriptor[] properties;
    
	public RemoteRepository(String type, String name, URL url) {
		super(url);
        this.type = type;
        this.name = name;
        PropertyDescriptor repositoryType = new PropertyDescriptor("repositoryType", "type");
        repositoryType.setCategory("Repository");
        PropertyDescriptor repositoryName = new PropertyDescriptor("repositoryName", "name");
        repositoryName.setCategory("Repository");
        PropertyDescriptor serverName = new PropertyDescriptor("serverName", "name");
        serverName.setCategory("Server");
        PropertyDescriptor serverUrl = new PropertyDescriptor("url", "url");
        serverUrl.setCategory("Server");
        PropertyDescriptor loginName = new PropertyDescriptor("userName", "user");
        loginName.setCategory("User");
        this.properties = new IPropertyDescriptor[] {
            repositoryType, repositoryName, serverName, serverUrl, loginName
        };
	}
    
	public String getRepositoryName() {
		RepositoryDescriptor desc = getManager().getRepositoryDescriptor(getType());
		return (desc != null) ? desc.getName() : "";
	}

	public String getRepositoryDescription() {
		RepositoryDescriptor desc = getManager().getRepositoryDescriptor(getType());
		return (desc != null) ? desc.getDescription() : "";
	}
	
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        notifyListeners(new RepositoryObjectChangeEvent(this, 
        	RepositoryObjectChangeEvent.MODIFY, "name"));
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        readServerInfo();
        notifyListeners(new RepositoryObjectChangeEvent(this, 
        	RepositoryObjectChangeEvent.MODIFY, "userName"));
    }

    public String getPassword() {
		return this.password;
	}

    public void setPassword(String password) {
    	this.password = password;
        readServerInfo();
        notifyListeners(new RepositoryObjectChangeEvent(this, 
        	RepositoryObjectChangeEvent.MODIFY, "password"));
    }

    public String getType() {
		return this.type;
	}

    public void refresh() {
        this.errorMessage = null;
        this.rpcClient = null;
    	this.spaces = null;
    	readServerInfo();
    	readSpaces();
    }

    /**
     * @see com.xpn.xwiki.wiked.repository.IRepository#isReadOnly()
     */
    public boolean isReadOnly() {
        return false;
    }

    public String getErrorMessage() {
    	return this.errorMessage;
    }
    
    public ISpace[] getSpaces(IProgressMonitor monitor) {
        if (this.spaces == null) {
        	readSpaces();
        }
        ISpace[] arr = new ISpace[this.spaces.size()];
		return (ISpace[])this.spaces.values().toArray(arr);
    }

	/**
	 * @see com.xpn.xwiki.wiked.repository.IRepository#createSpace(java.lang.String)
	 */
	public ISpace createSpace(String name) throws RepositoryException {
        Hashtable spaceData = new Hashtable();
        spaceData.put("key", name);
        spaceData.put("name", name);
        spaceData.put("description", "");
        Map data = (Map)getRemoteRepositoryClient().execute("addSpace", 
            new Object[] {spaceData});
        ISpace space = new RemoteSpace(this, data); 
        this.spaces.put(space.getName(), space);
		return space;
	}

    public String renderContent(String content) throws RepositoryException {
        RemoteRepositoryClient client = getRemoteRepositoryClient();
        return (String)client.execute("renderContent", new Object[] {
            "", "", content});
    }

    /**
     * @see com.xpn.xwiki.wiked.repository.IRepository#storeBookmark(org.eclipse.ui.IMemento)
     */
    public void storeBookmark(IMemento bookmarkData) {
        bookmarkData.putString(ATTR_USERNAME, getUserName());
        bookmarkData.putString(ATTR_PASSWORD, getPassword());
        bookmarkData.putTextData(getURL().toExternalForm());
    }
   
    public RemoteRepositoryClient getRemoteRepositoryClient() {
    	if (this.rpcClient == null) {
            this.rpcClient = new RemoteRepositoryClient(getURL(), this.userName, this.password);
        }
        
        return this.rpcClient;
    }
    
	/**
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object object) {
        if (this.errorMessage == null) {
   			return getSpaces(null);
        }
        
		return new Object[0];
	}

    /**
	 * @see com.xpn.xwiki.wiked.repository.IRepository#removeSpace(java.lang.String)
	 */
	public ISpace removeSpace(String name) throws RepositoryException {
        if (name == null) {
        	throw new IllegalArgumentException("no space");
        }
        getRemoteRepositoryClient().execute("removeSpace", new Object[] {name});
        return (ISpace)this.spaces.remove(name);
	}
    
	/**
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object) {
		ImageRegistry imageRegistry = WikedPlugin.getInstance().getImageRegistry();
		return imageRegistry.getDescriptor((this.errorMessage == null) ? 
            WikedImageRegistry.REPOSITORY : WikedImageRegistry.REPOSITORY_ERR);
	}

	/**
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
	 */
	public String getLabel(Object object) {
		return getName();
	}

	/**
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
	 */
	public Object getParent(Object object) {
		return null;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		return this;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if (this.errorMessage == null) {
			return this.properties;
        }
        
        PropertyDescriptor error = new PropertyDescriptor("error", "error");
        error.setCategory("Server");
        int size = this.properties.length;
        IPropertyDescriptor[] descs = new IPropertyDescriptor[size+1];
        System.arraycopy(this.properties, 0, descs, 0, size);
        descs[size] = error;
        return descs;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
        if ("repositoryType".equals(id)) {
        	return getType();
        } else if ("repositoryName".equals(id)) {
        	return getRepositoryName();
        } else if ("serverName".equals(id)) {
        	return getName();
        } else if ("url".equals(id)) {
            return getURL().toExternalForm();
        } else if ("userName".equals(id)) {
            return getUserName();
        } else if ("error".equals(id)) {
            return this.errorMessage;
        }
        
        return null;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		return false;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
	}

    private void readServerInfo() {
    	if (this.userName == null || this.password == null) {
    		return;
    	}
        try {
// TODO Not supported by current repository
//        	this.serverInfo = (Map)getRemoteRepositoryClient().execute("getServerInfo");
        	getRemoteRepositoryClient().login();
	        this.errorMessage = null;
		} catch (Exception ex) {
            this.errorMessage = ex.getMessage();
			WikedPlugin.logError("cannot read repository children", ex);
			notifyListeners(new RepositoryObjectChangeEvent(this, 
				RepositoryObjectChangeEvent.ERROR));
		}
    }
	
    private void readSpaces() {
        this.spaces = new HashMap();
        try {
	        List data = (List)getRemoteRepositoryClient().execute("getSpaces");
	        Iterator spacei = data.iterator(); 
	        while (spacei.hasNext()) {
	            ISpace space = new RemoteSpace(this, (Map)spacei.next()); 
	            this.spaces.put(space.getName(), space);
	        }
	        this.errorMessage = null;
		} catch (Exception ex) {
            this.errorMessage = "Error reading spaces: "+ex.getMessage();
			WikedPlugin.logError("cannot read repository children", ex);
			notifyListeners(new RepositoryObjectChangeEvent(this, 
				RepositoryObjectChangeEvent.ERROR));
		}
    }

}
