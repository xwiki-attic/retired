package com.xpn.xwiki.wiked.internal.ui.editor;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.ForwardingDocumentProvider;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;

/**
 * The default document provider implementation does nothing but
 * delegates all calls to provided parent provider.
 * 
 * @author psenicka_ja
 */
public class WikiDocumentProvider extends ForwardingDocumentProvider {

    public WikiDocumentProvider() {
        this(new TextFileDocumentProvider());
    }

    public WikiDocumentProvider(IDocumentProvider parent) {
    	super("default", new IDocumentSetupParticipant() {
            public void setup(IDocument document) {
            }
        }, parent);
    }

}
