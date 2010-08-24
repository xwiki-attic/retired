
package com.xpn.xwiki.wiked.internal.ui.action;

import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.xpn.xwiki.wiked.internal.WikedPlugin;

/**
 * Text formatting action
 * @author psenicka_ja
 */
public class FormatTextContentAction extends TextEditorAction {

    public FormatTextContentAction() {
        super(WikedPlugin.getInstance().getResourceBundle(), "FormatContent.", null);
    }

    public void run() {
        ITextOperationTarget target = (ITextOperationTarget) 
            getTextEditor().getAdapter(ITextOperationTarget.class);
        if (target != null) {
            target.doOperation(ISourceViewer.FORMAT);
        }
    }
}

