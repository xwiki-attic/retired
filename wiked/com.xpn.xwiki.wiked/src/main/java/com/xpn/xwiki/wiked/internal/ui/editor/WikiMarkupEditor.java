
package com.xpn.xwiki.wiked.internal.ui.editor;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.WikedEditorPreferencePage;

/**
 * The editor of wiki markup code.
 * Supports project-based IPage objects only. Sets WordWrap if the
 * preference key WikedPreferencePage.USE_WORD_WRAP is set.
 * @author psenicka_ja
 */
public class WikiMarkupEditor extends TextEditor {

	public WikiMarkupEditor(boolean remote) {
        IDocumentProvider provider = (remote) ?
            (IDocumentProvider)new WikiRemoteDocumentProvider() : 
            (IDocumentProvider)new WikiDocumentProvider();
		setDocumentProvider(provider);
		setSourceViewerConfiguration(new WikiSourceViewerConfiguration());
	}

    /** Creates a control and sets the wordwrap */
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		setWordWrap();
	}

	private void setWordWrap() {
		if (getSourceViewer() != null) {
            IPreferenceStore store = WikedPlugin.getInstance().getPreferenceStore();
            boolean wrap = store.getBoolean(WikedEditorPreferencePage.USE_WORD_WRAP);
			getSourceViewer().getTextWidget().setWordWrap(wrap);		
        }
	}

}