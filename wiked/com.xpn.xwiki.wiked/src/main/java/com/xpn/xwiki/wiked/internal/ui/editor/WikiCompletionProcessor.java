package com.xpn.xwiki.wiked.internal.ui.editor;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.WikiContextType;
import com.xpn.xwiki.wiked.internal.ui.WikedImageRegistry;

public class WikiCompletionProcessor extends TemplateCompletionProcessor {

	private char[] completionChars = new char[] { '{' };

	protected Template[] getTemplates(String contextTypeId) {
		return WikedPlugin.getInstance().getTemplateStore().getTemplates();
	}

	protected Image getImage(Template template) {
		return WikedPlugin.getInstance().getImageRegistry().get(
            WikedImageRegistry.COMPLETION);
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return completionChars;
	}

	protected TemplateContextType getContextType(ITextViewer viewer, 
        IRegion region) {
		return WikedPlugin.getInstance().getContextTypeRegistry()
		    .getContextType(WikiContextType.CONTEXT_TYPE);
	}
}