
package com.xpn.xwiki.wiked.internal.repository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
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

/**
 * 
 * @author psenicka_ja
 */
public class FilePage extends PlatformObject 
    implements IPage, IWorkbenchAdapter, IPropertySource {

	private FolderSpace space;
    private IFile file;
    private String title;

    private IPropertyDescriptor[] properties;
    
    public FilePage(FolderSpace space, IFile file) {
        if (space == null) {
        	throw new IllegalArgumentException("no space");
        }
    	this.space = space;
        if (file == null) {
        	throw new IllegalArgumentException("no file");
        }
        this.file = file;
        String filename = file.getName();
        this.title = filename.substring(0, filename.lastIndexOf('.'));
        PropertyDescriptor folderName = new PropertyDescriptor("fileName", "name");
        folderName.setCategory("File");
        this.properties = new IPropertyDescriptor[] { folderName };
	}

    /**
	 * @see com.xpn.xwiki.wiked.repository.IPage#getSpace()
	 */
	public ISpace getSpace() {
		return this.space;
	}
    
	/**
	 * @see com.xpn.xwiki.wiked.repository.IPage#getContent()
	 */
	public String getContent() throws RepositoryException {
        String line = null;
		StringBuffer buff = new StringBuffer();
        try {
        	InputStream stream = this.file.getContents();
            try {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stream));
                while ((line = reader.readLine()) != null) {
                	buff.append(line).append('\n');
                }
            } finally {
            	stream.close();
            }
            
            return buff.toString();
            
        } catch (Exception ex) {
        	return null;
        }
	}

    /**
     * @see com.xpn.xwiki.wiked.repository.IPage#getContent()
     */
    public void setContent(String content) throws RepositoryException {
    	throw new UnsupportedOperationException();
    }

    /**
	 * @see com.xpn.xwiki.wiked.repository.IPage#getTitle()
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @see com.xpn.xwiki.wiked.repository.IPage#setTitle(java.lang.String)
	 */
	public void setTitle(String title) throws RepositoryException {
		this.title = title;
	}

	/**
	 * @see com.xpn.xwiki.wiked.repository.IRepositoryObject#getName()
	 */
	public String getName() {
		return this.file.getName();
	}

	/**
	 * @see com.xpn.xwiki.wiked.repository.IRepositoryObject#setName(java.lang.String)
	 */
	public void setName(String name) {
	}

	/**
	 * @see com.xpn.xwiki.wiked.repository.IRepositoryObject#refresh()
	 */
	public void refresh() {
	}

	/**
	 * @see com.xpn.xwiki.wiked.repository.IRepositoryObject#isReadOnly()
	 */
	public boolean isReadOnly() {
		return this.file.isReadOnly();
	}

    /**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class clazz) {
        return (clazz.isInstance(file)) ? file : super.getAdapter(clazz);
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
            WikedImageRegistry.PAGE);
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
	 * @return
	 */
	public IFile getFile() {
		return this.file;
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
		if ("fileName".equals(id)) {
			return this.file.getName();
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
}
