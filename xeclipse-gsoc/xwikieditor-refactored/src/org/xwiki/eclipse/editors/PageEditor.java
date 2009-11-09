package org.xwiki.eclipse.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.TextOperationAction;

import xwikieditor.Activator;

public class PageEditor extends TextEditor
{

    public PageEditor()
    {
        super();
        setDocumentProvider(new XWikiDocumentProvider());
        setSourceViewerConfiguration(new XWikiSourceViewerConfiguration(this));
    }

    @Override
    protected void createActions()
    {
        super.createActions();

        setAction("ContentAssistProposal", new TextOperationAction(Activator.getDefault().getResourceBundle(),
            "ContentAssistProposal.", this, ISourceViewer.CONTENTASSIST_PROPOSALS));
    }

    public int getCaretOffset()
    {
        return getSourceViewer().getTextWidget().getCaretOffset();
    }

    public IDocument getDocument()
    {
        ISourceViewer viewer = getSourceViewer();
        if (viewer != null) {
            return viewer.getDocument();
        }
        return null;
    }

    public void setSelectionRange(int start, int length)
    {
        getSourceViewer().getTextWidget().setSelectionRange(start, length);
    }
}
