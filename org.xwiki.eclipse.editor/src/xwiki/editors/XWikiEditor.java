package xwiki.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class XWikiEditor extends TextEditor {

	private ColorManager colorManager;

	public XWikiEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XWikiConfiguration(colorManager));
		setDocumentProvider(new XWikiDocumentProvider());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
