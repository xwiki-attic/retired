
package com.xpn.xwiki.wiked.internal.repository;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.WikedImageRegistry;
import com.xpn.xwiki.wiked.repository.IBookmarkSaveParticipant;
import com.xpn.xwiki.wiked.repository.IRepository;
import com.xpn.xwiki.wiked.repository.IRepositoryManager;
import com.xpn.xwiki.wiked.repository.ISpace;
import com.xpn.xwiki.wiked.repository.RepositoryException;
import com.xpn.xwiki.wiked.repository.RepositoryObjectChangeEvent;

public class ProjectRepository extends AbstractRepository 
    implements IRepository, IWorkbenchAdapter, IPropertySource, 
    IBookmarkSaveParticipant {

	private IProject project;
    private Map spaces;

    private String errorMessage; 
    private IPropertyDescriptor[] properties;

    public static final String TYPE = "project";
    private static final QualifiedName PREVIEW_SERVER_KEY = 
    	new QualifiedName(WikedPlugin.ID, "previewServerName");
    
	public ProjectRepository(IProject project) 
        throws MalformedURLException {
        super(project.getLocation().toFile().toURL());
		this.project = project;
        PropertyDescriptor projectName = new PropertyDescriptor("projectName", "name");
        projectName.setCategory("Project");
        this.properties = new IPropertyDescriptor[] { projectName };
	    this.errorMessage = null;
	}

    public String getName() {
    	return this.project.getName();
    }
    
    public void setName(String name) {
    	if (name == null) {
    		throw new IllegalArgumentException("no name");
    	}
        try {
			IProjectDescription pd = project.getDescription();
			pd.setName(name);
			project.move(pd, true, null);
		} catch (CoreException ex) {
			IllegalArgumentException iaex = new IllegalArgumentException("cannot set name");
            iaex.initCause(ex);
            throw iaex;
		}
    }
    
    public IProject getProject() {
    	return this.project;
    }
    
	public String getType() {
		return TYPE;
	}

	public String getPreviewServerName() throws RepositoryException {
		try {
			return this.project.getPersistentProperty(PREVIEW_SERVER_KEY);
		} catch (CoreException ex) {
			throw new RepositoryException(ex);
		}
	}

	public void setPreviewServerName(String previewServerName) 
		throws RepositoryException {
		try {
			this.project.setPersistentProperty(PREVIEW_SERVER_KEY, previewServerName);
		} catch (CoreException ex) {
			throw new RepositoryException(ex);
		}
	}
	
    /**
	 * @see com.xpn.xwiki.wiked.repository.IRepository#isReadOnly()
	 */
	public boolean isReadOnly() {
		ResourceAttributes attrs = this.project.getResourceAttributes();
		return (attrs != null) ? attrs.isReadOnly() : true;
	}
    
    public void refresh() {
    	this.errorMessage = null;
        this.spaces = null;
    }

    /**
     * @see com.xpn.xwiki.wiked.repository.IRepository#createSpace()
     */
    public ISpace createSpace(String name) throws RepositoryException {
    	if (name == null || name.length() == 0) {
    		throw new IllegalArgumentException("no name");
    	}
        if (this.spaces != null && this.spaces.containsKey(name)) {
        	return (ISpace)this.spaces.get(name);
        }
        try {
			IFolder folder = project.getFolder(name);
			if (folder.exists() == false) {
				folder.create(true, true, null);
				ISpace space = new FolderSpace(this, folder);
		        notifyListeners(new RepositoryObjectChangeEvent(space, 
	                RepositoryObjectChangeEvent.CREATE));
		        return space;
			} else {
				throw new IllegalStateException("folder "+name+" exists, but not referred as space");
			}
		} catch (CoreException ex) {
			throw new RepositoryException(ex);
		}
        
    }

    /**
     * @see com.xpn.xwiki.wiked.repository.IRepository#addSpace(com.xpn.xwiki.wiked.repository.ISpace)
     */
    public void addSpace(ISpace space) {
    	if (space == null) {
    		throw new IllegalArgumentException("no space");
    	}
        if (this.spaces == null) {
            readSpaces();
       }
       this.spaces.put(space.getName(), space); 
    }

    public ISpace[] getSpaces(IProgressMonitor monitor) throws RepositoryException {
        if (this.spaces == null) {
             try {
                readSpaces();
            } catch (Exception ex) {
                this.spaces = new HashMap();
                WikedPlugin.logError("cannor read children of "+this.project, ex);
            }
       }
    	return (ISpace[])this.spaces.values().toArray(new ISpace[this.spaces.size()]);
    }

    /**
	 * @see com.xpn.xwiki.wiked.repository.IRepository#removeSpace(java.lang.String)
	 */
	public ISpace removeSpace(String name) throws RepositoryException {
    	if (name == null || name.length() == 0) {
    		throw new IllegalArgumentException("no name");
    	}
        if (this.spaces == null) {
            readSpaces();
       }
       FolderSpace space = (FolderSpace)this.spaces.get(name);
       if (space != null) {
            try {
                IFolder folder = space.getFile();
                folder.delete(false, true, null);
                this.spaces.remove(name);
		        notifyListeners(new RepositoryObjectChangeEvent(space, 
	                RepositoryObjectChangeEvent.REMOVE));
            } catch (CoreException ex) {
            	throw new RepositoryException(ex);
            }
        }
       
       return space;
	}

	public String renderContent(String content) throws RepositoryException {
		String previewServerName = getPreviewServerName();
		if (previewServerName != null && previewServerName.length() > 0) {
			if (this.getName().equals(previewServerName)) {
				throw new IllegalArgumentException("recursive call");
			}
			IRepositoryManager manager = WikedPlugin.getInstance().getRepositoryManager();
			IRepository previewServer = manager.getRepository(previewServerName);
			if (previewServer != null) {
				return previewServer.renderContent(content);
			}
		}
		
		return null;
	}
	
    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class clazz) {
        return (clazz.isInstance(project)) ? project : super.getAdapter(clazz);
    }

    /**
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object object) {
        try {
            return getSpaces(null);
        } catch (RepositoryException ex) {
            WikedPlugin.logError("cannot read repository children", ex);
            return new Object[0];
        }
    }

    /**
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
     */
    public ImageDescriptor getImageDescriptor(Object object) {
        return WikedPlugin.getInstance().getImageRegistry().getDescriptor(
            this.isReadOnly() ? WikedImageRegistry.REPOSITORY_RO : 
                WikedImageRegistry.REPOSITORY);
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
	 * @see com.xpn.xwiki.wiked.repository.IRepository#storeBookmark(org.eclipse.ui.IMemento)
	 */
	public void storeBookmark(IMemento bookmarkData) {
        bookmarkData.putTextData(this.project.getName());
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
		if ("projectName".equals(id)) {
			return project.getName();
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

    private void readSpaces() {
    	try {
	        IResource[] files = this.project.members();
	        this.spaces = new HashMap();
	        for (int i = 0; i < files.length; i++) {
	            IResource resource = files[i];
	            if (resource.getType() == IResource.FOLDER) {
	                String name = resource.getName();
	                this.spaces.put(name, new FolderSpace(this, (IFolder)resource));
	            }
	        }
	        this.errorMessage = null;
    	} catch (Exception ex) {
            this.errorMessage = ex.getMessage();
			WikedPlugin.logError("cannot read repository children", ex);
			notifyListeners(new RepositoryObjectChangeEvent(this, 
				RepositoryObjectChangeEvent.ERROR));
    	}
	}

}
