
package com.xpn.xwiki.wiked.internal.ui;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.xpn.xwiki.wiked.internal.WikedPlugin;

public class MessageDialogWithPreference extends MessageDialog {

	private Button toggleButton;
	private Preferences preferences;
	private String preferenceKey;

    public static boolean openConfirm(Shell parent, String title, 
        String message, String preferenceKey) {
        if (checkPreference(preferenceKey)) {
            return true;
        }
        MessageDialogWithPreference dialog = new MessageDialogWithPreference(
            parent, title, null, message, QUESTION, new String[] {
                IDialogConstants.OK_LABEL,
                IDialogConstants.CANCEL_LABEL 
            }, 0, preferenceKey);
        return (dialog.open() == Window.OK);
    }

    public static void openInformation(Shell parent, String title, 
        String message, String preferenceKey) {
        if (checkPreference(preferenceKey)) {
            return;
        }
        MessageDialogWithPreference dialog = new MessageDialogWithPreference(
            parent, title, null, message, INFORMATION, new String[] {
                IDialogConstants.OK_LABEL
            }, 0, preferenceKey);
        dialog.open();
    }

    public static boolean openQuestion(Shell parent, String title, 
        String message, String preferenceKey) {
        if (checkPreference(preferenceKey)) {
            return true;
        }
        MessageDialogWithPreference dialog = new MessageDialogWithPreference(
            parent, title, null, message, QUESTION, new String[] {
                IDialogConstants.YES_LABEL,
                IDialogConstants.NO_LABEL
            }, 0, preferenceKey);
        return (dialog.open() == Window.OK);
    }

    public static void openWarning(Shell parent, String title, 
        String message, String preferenceKey) {
        if (checkPreference(preferenceKey)) {
            return;
        }
        MessageDialogWithPreference dialog = new MessageDialogWithPreference(
            parent, title, null, message, WARNING, new String[] {
                IDialogConstants.OK_LABEL
            }, 0, preferenceKey);
        dialog.open();
    }
        
    public static void openError(Shell parent, String title, 
        String message, String preferenceKey) {
        if (checkPreference(preferenceKey)) {
            return;
        }
        MessageDialogWithPreference dialog = new MessageDialogWithPreference(
            parent, title, null, message, ERROR, new String[] {
                IDialogConstants.OK_LABEL
            }, 0, preferenceKey);
        dialog.open();
    }

    public MessageDialogWithPreference(Shell parentShell, String dialogTitle,
        Image image, String message, int dialogImageType, String[] buttonLabels,
        int defaultIndex, String preferenceKey) {
        this(parentShell, dialogTitle, image, message, dialogImageType, 
            buttonLabels, defaultIndex, null, preferenceKey);
    }

	public MessageDialogWithPreference(Shell parentShell, String dialogTitle,
		Image image, String message, int dialogImageType, String[] buttonLabels,
		int defaultIndex, Preferences prefs, String preferenceKey) {
		super(parentShell, dialogTitle, image, message, dialogImageType, 
            buttonLabels, defaultIndex);
		Assert.isNotNull(preferenceKey);
		this.preferenceKey = preferenceKey;
		preferences = (prefs != null) ? prefs : 
            WikedPlugin.getInstance().getPluginPreferences();
	}

	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		toggleButton = createToggleButton(dialogArea);
		return dialogArea;
	}

	private Button createToggleButton(Composite parent) {
		final Button button = new Button(parent, SWT.CHECK | SWT.LEFT);
        String t = WikedPlugin.getInstance().getResourceString("MDWP.DontAsk");
		button.setText(t);
		button.setSelection(preferences.getBoolean(preferenceKey));
		GridData data = new GridData(SWT.NONE);
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.CENTER;
		button.setLayoutData(data);
		button.setFont(parent.getFont());
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				preferences.setValue(preferenceKey, button.getSelection());
			}
		});
		return button;
	}

	private Button getToggleButton() {
		return toggleButton;
	}
	
	private static boolean checkPreference(String key) {
		return WikedPlugin.getInstance().getPluginPreferences().getBoolean(key);
	}

}