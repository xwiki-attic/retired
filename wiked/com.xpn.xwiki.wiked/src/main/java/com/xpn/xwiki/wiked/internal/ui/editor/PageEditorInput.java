
package com.xpn.xwiki.wiked.internal.ui.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.editors.text.ILocationProvider;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.WikedImageRegistry;
import com.xpn.xwiki.wiked.repository.IPage;

/**
 * The page editor input.
 * @author psenicka_ja
 */
public class PageEditorInput extends PlatformObject 
    implements IEditorInput, ILocationProvider {

    /** The page to be edited */
	private IPage page;
    /** If the page is project-based, the file points to the root */
	private IFile file;

    /**
     * The constructor
     * @param page to be displayed
     */
	public PageEditorInput(IPage page) {
		if (page == null) {
			throw new IllegalArgumentException("no page");
		}
        this.page = page;
		if (page instanceof IAdaptable) {
			this.file = (IFile) ((IAdaptable) page).getAdapter(IFile.class);
		}
	}

    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
	public Object getAdapter(Class adapter) {
        if (adapter == IFile.class) {
            return this.file;
        }
        if (adapter == IPage.class) {
            return this.page;
        }
		return super.getAdapter(adapter);
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists() {
		return (this.file != null) ? this.file.exists() : true;
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return WikedPlugin.getInstance().getImageRegistry().getDescriptor(
            WikedImageRegistry.PAGE);
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public String getName() {
		return this.page.getName();
	}

	/**
     * No workbench persitence here.
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return null;
	}

	/**
     * Shows the page title as the tooltip.
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return page.getTitle();
	}

	/**
     * 
	 * @see org.eclipse.ui.editors.text.ILocationProvider#getPath(java.lang.Object)
	 */
	public IPath getPath(Object element) {
        if (element instanceof IAdaptable) {
        	IFile elementFile = (IFile) ((IAdaptable) page).getAdapter(IFile.class);
            return (elementFile != null) ? elementFile.getLocation() : null; 
        }
 		return null;
	}

	/**
	 * @return
	 */
	public boolean isRemotePage() {
		return (file == null);
	}

}
