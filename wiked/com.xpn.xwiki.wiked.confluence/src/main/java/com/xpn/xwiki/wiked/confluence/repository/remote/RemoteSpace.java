
package com.xpn.xwiki.wiked.confluence.repository.remote;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.WikedImageRegistry;
import com.xpn.xwiki.wiked.repository.IPage;
import com.xpn.xwiki.wiked.repository.IRepository;
import com.xpn.xwiki.wiked.repository.ISpace;
import com.xpn.xwiki.wiked.repository.RepositoryException;
import com.xpn.xwiki.wiked.repository.RepositoryObjectChangeEvent;

/**
 * Remote instance of the page
 * @author psenicka_ja
 */
public class RemoteSpace extends PlatformObject 
    implements ISpace, IWorkbenchAdapter, IPropertySource {

    private RemoteRepository repository;

    private String key;
    private Map data;
    private Map pages;
    
    private IPropertyDescriptor[] properties;

    private String errorMessage;
    
    /**
	 * @param data
	 * @param repository
     * @throws RepositoryException
	 * 
	 */
	public RemoteSpace(RemoteRepository repository, Map data) throws RepositoryException {
		this.repository = repository;
        this.key = (String)data.get("key");
        this.data = readData(this.key);
        this.properties = PropertyDescriptorFactory.create(this.data.keySet(), 
            new PropertyDescriptorFactory.Visitor() {
                public PropertyDescriptor create(String key) {
                    PropertyDescriptor pd = new PropertyDescriptor(key, key);
                    pd.setCategory("Space");
                    return pd;
                }
            }
        );
    }

	/**
	 * @param repository
	 * @param name
	 * @throws RepositoryException
	 */
	public RemoteSpace(RemoteRepository repository, String name) throws RepositoryException {
        this.repository = repository;
        this.key = name;
        this.data = readData(this.key);
        this.properties = PropertyDescriptorFactory.create(this.data.keySet(), 
            new PropertyDescriptorFactory.Visitor() {
                public PropertyDescriptor create(String key) {
                    PropertyDescriptor pd = new PropertyDescriptor(key, key);
                    pd.setCategory("Space");
                    return pd;
                }
            }
        );
	}

    /**
     * @see com.xpn.xwiki.wiked.repository.IPage#getSpace()
     */
    public IRepository getRepository() {
        return this.repository;
    }

    /**
	 * @see com.xpn.xwiki.wiked.repository.ISpace#createPage(java.lang.String)
	 */
	public IPage createPage(String name) throws SecurityException, RepositoryException {
        Hashtable pageData = new Hashtable();
        pageData.put("space", this.getName());
        pageData.put("id", this.getName()+"."+name);
        pageData.put("title", this.getName()+"."+name);
        pageData.put("content", "");
        pageData.put("version", new Integer(0));
        pageData.put("homepage", new Boolean(false));
        pageData.put("locks", new Integer(0));
        pageData.put("creator", "XWiki.slavek");
        pageData.put("modifier", "XWiki.slavek");
        pageData.put("modified", new Date());
        pageData.put("url", "http://localhost:8080/xwiki/bin/view");
        pageData.put("created", new Date());
        pageData.put("parentId", "Sandbox");
        return addPage((Map)this.repository.getRemoteRepositoryClient().execute("storePage", 
            new Object[] {pageData}));
    }

	/**
	 * @throws RepositoryException
	 * @see com.xpn.xwiki.wiked.repository.ISpace#getPages(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IPage[] getPages(IProgressMonitor monitor) {
        if (this.pages == null) {
            try {
            	readPages();
            } catch (RepositoryException ex) {
            	WikedPlugin.logError("cannot get pages of "+this.key, ex);
            }
        }
        return (IPage[])this.pages.values().toArray(new IPage[this.pages.size()]);
	}

	/**
	 * @see com.xpn.xwiki.wiked.repository.IRepositoryObject#getName()
	 */
	public String getName() {
		return (String)this.data.get("name");
	}

	/**
	 * @throws RepositoryException
	 * @see com.xpn.xwiki.wiked.repository.IRepositoryObject#setName(java.lang.String)
	 */
	public void setName(String name) throws RepositoryException {
//        Assert.isNotNull(name);
//        String oldName = this.name;
//        this.name = name;
//		if (name.equals(oldName) == false) {
//			this.repository.getRemoteRepositoryClient().execute("confluence1.storePage", 
//                new Object[] {this});
//        }        
	}

	/**
	 * @throws RepositoryException
	 * @see com.xpn.xwiki.wiked.repository.IRepositoryObject#refresh()
	 */
	public void refresh() {
        this.data = readData(this.key);
		this.pages = null;
	}

	/**
	 * @see com.xpn.xwiki.wiked.repository.IRepositoryObject#isReadOnly()
	 */
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

    public RemoteRepository getRemoteRepository() {
    	return this.repository;
    }
    
    /**
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object element) {
        try {
            return getPages(null);
        } catch (Exception ex) {
            WikedPlugin.logError("cannot read space children", ex);
            return new Object[0];
        }
    }

    /**
	 * @see com.xpn.xwiki.wiked.repository.ISpace#removePage(java.lang.String)
	 */
	public IPage removePage(String name) throws RepositoryException {
        if (name == null) {
        	throw new IllegalArgumentException("no space");
        }
        this.repository.getRemoteRepositoryClient().execute("removePage", new Object[] {name});
        return (IPage)this.pages.remove(name);
	}
    
    /**
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
     */
    public ImageDescriptor getImageDescriptor(Object element) {
        return WikedPlugin.getInstance().getImageRegistry().getDescriptor(
            WikedImageRegistry.SPACE);
    }

    /**
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
     */
    public String getLabel(Object arg0) {
        return getName();
    }

    /**
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
        return this.repository;
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
        return this.properties;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     */
    public Object getPropertyValue(Object id) {
        return this.data.get(id);
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
    
    private Map readData(String id) {
    	try {
	        RemoteRepositoryClient client = this.repository.getRemoteRepositoryClient();
	        return (Map)client.execute("getSpace", new Object[] {id});
		} catch (Exception ex) {
            this.errorMessage = ex.getMessage();
			WikedPlugin.logError("cannot read page", ex);
			notifyListeners(new RepositoryObjectChangeEvent(this, 
				RepositoryObjectChangeEvent.ERROR));
		}
		
		return new HashMap();
    }

    private void readPages() throws RepositoryException {
        this.pages = new HashMap();
    	try {
	        List data = (List)this.repository.getRemoteRepositoryClient().execute(
	            "getPages", new Object[] {this.key});
	        Iterator spacei = data.iterator(); 
	        while (spacei.hasNext()) {
	            addPage((Map)spacei.next());
	        }
		} catch (Exception ex) {
            this.errorMessage = "Error reading pages: "+ex.getMessage();
			WikedPlugin.logError("cannot read space children", ex);
			notifyListeners(new RepositoryObjectChangeEvent(this, 
				RepositoryObjectChangeEvent.ERROR));
		}
    }
    
    private IPage addPage(Map pageData) throws RepositoryException {
        IPage page = new RemotePage(this, pageData);
        this.pages.put(page.getName(), page);
        return page;
    }

    protected void notifyListeners(RepositoryObjectChangeEvent event) {
    	getRemoteRepository().notifyListeners(event);
    }
}
