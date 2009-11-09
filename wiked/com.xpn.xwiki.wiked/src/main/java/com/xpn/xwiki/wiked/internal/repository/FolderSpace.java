
package com.xpn.xwiki.wiked.internal.repository;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
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
 * 
 * @author psenicka_ja
 */
public class FolderSpace extends PlatformObject 
    implements ISpace, IWorkbenchAdapter, IPropertySource {

	private ProjectRepository repository;
    private IFolder folder;
    private Map pages;

    private String errorMessage;
    private IPropertyDescriptor[] properties;
    
    public static final String WIKI_EXT = "wiki";

    public FolderSpace(ProjectRepository repository, IFolder file) {
		this.repository = repository;
        this.folder = file;
        PropertyDescriptor folderName = new PropertyDescriptor("folderName", "name");
        folderName.setCategory("Folder");
        this.properties = new IPropertyDescriptor[] { folderName };
        this.errorMessage = null;
    }

    /**
	 * @see com.xpn.xwiki.wiked.repository.ISpace#getReopository()
	 */
	public IRepository getRepository() {
		return this.repository;
	}
    
	public IPage createPage(String name) throws SecurityException, RepositoryException {
    	if (name == null || name.length() == 0) {
    		throw new IllegalArgumentException("no name");
    	}
        if (this.pages != null && this.pages.containsKey(name)) {
        	return (IPage)this.pages.get(name);
        }
        try {
			IFile file = this.folder.getFile(name+".wiki");
			if (file.exists() == false) {
				ByteArrayInputStream bis = new ByteArrayInputStream("".getBytes());
				file.create(bis, true, null);
				IPage page = new FilePage(this, file);
		        notifyListeners(new RepositoryObjectChangeEvent(page, 
	                RepositoryObjectChangeEvent.CREATE));
				return page;
			}
		} catch (CoreException ex) {
			throw new RepositoryException(ex);
		}
        
        throw new IllegalStateException("file "+name+" exists, but not referred as page");
	}

    public IPage[] getPages(IProgressMonitor monitor) {
    	if (this.pages == null) {
            try {
            	readPages();
            } catch (CoreException ex) {
                this.pages = new HashMap();
            	WikedPlugin.logError("cannor read children of "+this.folder, ex);
            }
        }
        return (IPage[])this.pages.values().toArray(new IPage[this.pages.size()]);
    }
    
    /**
	 * @see com.xpn.xwiki.wiked.repository.ISpace#removePage(java.lang.String)
	 */
	public IPage removePage(String name) throws RepositoryException {
        if (name == null) {
        	throw new IllegalArgumentException("no space");
        }
    	if (this.pages == null) {
            try {
            	readPages();
            } catch (CoreException ex) {
                this.pages = new HashMap();
            	WikedPlugin.logError("cannor read children of "+this.folder, ex);
            }
        }
        FilePage page = (FilePage)this.pages.get(name);
        if (page != null) {
            try {
                IFile file = page.getFile();
                folder.delete(false, true, null);
                this.pages.remove(name);
		        notifyListeners(new RepositoryObjectChangeEvent(page, 
	                RepositoryObjectChangeEvent.REMOVE));
            } catch (CoreException ex) {
                throw new RepositoryException(ex);
            }
        }
        
        return page;
	}
        
	/**
	 * @see com.xpn.xwiki.wiked.repository.ISpace#getName()
	 */
	public String getName() {
		return this.folder.getName();
	}

    /**
     * @see com.xpn.xwiki.wiked.repository.IRepositoryObject#setName(java.lang.String)
     */
    public void setName(String name) {
    }

    /**
	 * @see com.xpn.xwiki.wiked.repository.ISpace#isReadOnly()
	 */
	public boolean isReadOnly() {
		ResourceAttributes attrs = this.folder.getResourceAttributes();
		return (attrs != null) ? attrs.isReadOnly() : true;
	}

    /**
	 * @see com.xpn.xwiki.wiked.repository.ISpace#refresh()
	 */
	public void refresh() {
		this.errorMessage = null;
		this.pages = null;
    }
    
    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class clazz) {
        return (clazz.isInstance(folder)) ? folder : super.getAdapter(clazz);
    }

    /**
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object element) {
        return getPages(null);
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

    public IFolder getFile() {
    	return this.folder;
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
		if ("folderName".equals(id)) {
			return this.folder.getName();
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
    
    private void readPages() throws CoreException {
    	try {
	        IResource[] files = this.folder.members();
	        this.pages = new HashMap();
	        for (int i = 0; i < files.length; i++) {
				IResource resource = files[i];
				if (resource.getType() == IResource.FILE && 
	                FolderSpace.WIKI_EXT.equals(resource.getFileExtension())) {
					String name = resource.getName().substring(0, resource.getName().lastIndexOf('.'));
					this.pages.put(name, new FilePage(this, (IFile)resource));
	            }
			}
    	} catch (Exception ex) {
            this.errorMessage = ex.getMessage();
			WikedPlugin.logError("cannot read space children", ex);
			notifyListeners(new RepositoryObjectChangeEvent(this, 
				RepositoryObjectChangeEvent.ERROR));
    	}
    }

    private void notifyListeners(RepositoryObjectChangeEvent event) {
        if (this.repository != null) {
        	this.repository.notifyListeners(event);
        }
    }
}
