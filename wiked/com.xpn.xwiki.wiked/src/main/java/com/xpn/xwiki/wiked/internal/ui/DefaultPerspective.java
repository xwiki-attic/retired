
package com.xpn.xwiki.wiked.internal.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Perspective with erpository view, property sheet and problems view.
 * @author Psenicka_Ja
 */
public class DefaultPerspective implements IPerspectiveFactory {

    public static final String ID = "com.xpn.xwiki.wiked.DefaultPerspective";
	
    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        IFolderLayout left = layout.createFolder("left", 
        	IPageLayout.LEFT, 0.3f, editorArea);
        left.addView(RepositoryView.ID);

        IFolderLayout bottomleft = layout.createFolder("bottom-left", 
        	IPageLayout.BOTTOM, 0.5f, "left");
        bottomleft.addView(IPageLayout.ID_PROP_SHEET);
        
        IFolderLayout bottom = layout.createFolder("bottom", 
        	IPageLayout.BOTTOM, 0.6f, editorArea);
        bottom.addPlaceholder(IPageLayout.ID_TASK_LIST);
        bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
    }

}

