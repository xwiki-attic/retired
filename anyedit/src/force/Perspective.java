/**
 * Copyright (c) Kevin Chiu
 * Licensed under LGPL
*/
package force;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		IFolderLayout folder = layout.createFolder("messages", IPageLayout.TOP, .5f , editorArea);
		folder.addPlaceholder(View.ID + ":*");
		folder.addView(View.ID);
		
	}
}
