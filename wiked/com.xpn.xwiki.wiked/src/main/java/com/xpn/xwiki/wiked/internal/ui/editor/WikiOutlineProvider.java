
package com.xpn.xwiki.wiked.internal.ui.editor;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.WikedImageRegistry;
import com.xpn.xwiki.wiked.internal.wikip.IWikiStructureObject;
import com.xpn.xwiki.wiked.internal.wikip.WikiDocument;
import com.xpn.xwiki.wiked.internal.wikip.WikiParser;

/**
 * 
 * @author psenicka_ja
 */
public class WikiOutlineProvider extends LabelProvider 
    implements ITreeContentProvider {

    private WikiParser parser;
    private WikiDocument document;
    private ImageRegistry imageRegistry;
    
    public WikiOutlineProvider() {
        this.parser = new WikiParser();
    	this.imageRegistry = WikedPlugin.getInstance().getImageRegistry();
    }
    
    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldSource, Object newSource) {
        if (newSource instanceof IAdaptable) {
            IFile file = (IFile)((IAdaptable)newSource).getAdapter(IFile.class);
            if (file != null) {
                try {
                	this.document = this.parser.parse(file);
                } catch (IOException ex) {
                	WikedPlugin.handleError("cannot read input "+newSource, ex);
                }
                return;
            } 
        } else if (newSource != null) {
        	throw new IllegalStateException("unknown input "+newSource);
        }
        
    }

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parent) {
		return getElements(parent);
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object object) {
		return (object instanceof IWikiStructureObject) ? 
            ((IWikiStructureObject)object).getParent() : null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object parent) {
		return true;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object object) {
        if (object instanceof IWikiStructureObject) { 
            return ((IWikiStructureObject)object).getChildren();
        } else if (object instanceof PageEditorInput) {
        	return document.getChildren();
        }
            
        return new Object[0];
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		this.imageRegistry = null;
	}

    /**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object object) {
        return object.toString();
	}
    
    /**
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object object) {
		return (object instanceof IWikiStructureObject) ? 
            imageRegistry.get(WikedImageRegistry.NODE) : super.getImage(object);
	}
}
