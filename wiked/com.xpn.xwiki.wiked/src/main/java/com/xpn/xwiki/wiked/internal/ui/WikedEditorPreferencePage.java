
package com.xpn.xwiki.wiked.internal.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.xpn.xwiki.wiked.internal.WikedPlugin;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>,
 * we can use the field support built into JFace that allows us to create a
 * page that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */
public class WikedEditorPreferencePage extends FieldEditorPreferencePage 
    implements IWorkbenchPreferencePage {

    public static final String USE_WORD_WRAP = "useWordWrap";

    public WikedEditorPreferencePage() {
		super(GRID);
		setPreferenceStore(WikedPlugin.getInstance().getPreferenceStore());
	}

    public void init(IWorkbench workbench) {
    }

    /**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		Composite editorParent = getFieldEditorParent();
		addField(new BooleanFieldEditor(WikedEditorPreferencePage.USE_WORD_WRAP, 
            "Use Word Wrap", editorParent));
	}
	
}