
package com.xpn.xwiki.wiked.confluence.repository.remote;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.WikedImageRegistry;
import com.xpn.xwiki.wiked.repository.IPage;
import com.xpn.xwiki.wiked.repository.ISpace;
import com.xpn.xwiki.wiked.repository.RepositoryException;
import com.xpn.xwiki.wiked.repository.RepositoryObjectChangeEvent;

/**
 * 
 * @author psenicka_ja
 */
public class RemotePage extends PlatformObject
    implements IPage, IWorkbenchAdapter, IPropertySource {

    private RemoteSpace space;
    private String id;
    private Map data;
    private IPropertyDescriptor[] properties;

    private String errorMessage;
    
    /**
	 * @param svo
	 * @param space
	 * @throws RepositoryException
	 */
	public RemotePage(RemoteSpace space, Map data) throws RepositoryException {
		this.space = space;
        this.id = (String)data.get("id");
        this.data = readData(this.id);
        this.properties = PropertyDescriptorFactory.create(this.data.keySet(), 
            new PropertyDescriptorFactory.Visitor() {
            	public PropertyDescriptor create(String key) {
                    if ("content".equals(key)) {
                    	return null;
                    }
                    PropertyDescriptor pd = new PropertyDescriptor(key, key);
                    pd.setCategory("Page");
                    return pd;
                }
            }
        );
    }

	/**
	 * @see com.xpn.xwiki.wiked.repository.IPage#getContent()
	 */
	public String getContent() throws RepositoryException {
		return (String)this.data.get("content");
	}

    public void setContent(String content) throws RepositoryException {
        if (content == null) {
        	throw new IllegalArgumentException("no content");
        }
        Map map = new Hashtable(this.data);
        map.put("content", content);
    	RemoteRepositoryClient client = this.space.getRemoteRepository().getRemoteRepositoryClient();
    	this.data = (Map)client.execute("storePage", new Object[] {map});
    }
    
    /**
     * @see com.xpn.xwiki.wiked.repository.IPage#getSpace()
     */
    public ISpace getSpace() {
        return this.space;
    }

	/**
	 * @see com.xpn.xwiki.wiked.repository.IPage#getTitle()
	 */
	public String getTitle() {
        String space = (String)this.data.get("space");
        String title = (String)this.data.get("title");
        if (title != null) {
        	return (title.startsWith(space+".")) ? 
                title.substring(space.length()+1) : title;
        } 
        return "";
	}

	/**
	 * @see com.xpn.xwiki.wiked.repository.IPage#setTitle(java.lang.String)
	 */
	public void setTitle(String title) throws RepositoryException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see com.xpn.xwiki.wiked.repository.IRepositoryObject#getName()
	 */
	public String getName() {
        return (String)this.data.get("id");
	}

	/**
	 * @see com.xpn.xwiki.wiked.repository.IRepositoryObject#setName(java.lang.String)
	 */
	public void setName(String name) {
		// TODO Auto-generated method stub

	}

	/**
	 * @throws RepositoryException
	 * @see com.xpn.xwiki.wiked.repository.IRepositoryObject#refresh()
	 */
	public void refresh() {
		this.data = readData(this.id);	
    }

	/**
	 * @see com.xpn.xwiki.wiked.repository.IRepositoryObject#isReadOnly()
	 */
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

    /**
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object element) {
        return new Object[0];
    }

    /**
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
     */
    public ImageDescriptor getImageDescriptor(Object element) {
        return WikedPlugin.getInstance().getImageRegistry().getDescriptor(
            (errorMessage == null) ? WikedImageRegistry.PAGE : 
            WikedImageRegistry.PAGE_ERR);
    }

    /**
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
     */
    public String getLabel(Object element) {
        return getTitle();
    }

    /**
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
        return this.space;
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

    /**
     * @param id
     * @throws RepositoryException
     */
    private Map readData(String id) {
    	try {
	        RemoteRepositoryClient client = this.space.getRemoteRepository().
	            getRemoteRepositoryClient();
	        return (Map)client.execute("getPage", new Object[] {id});
		} catch (Exception ex) {
            this.errorMessage = ex.getMessage();
			WikedPlugin.logError("cannot read page", ex);
			notifyListeners(new RepositoryObjectChangeEvent(this, 
				RepositoryObjectChangeEvent.ERROR));
		}
		
		return new HashMap();
    }

    protected void notifyListeners(RepositoryObjectChangeEvent event) {
    	this.space.getRemoteRepository().notifyListeners(event);
    }

}
