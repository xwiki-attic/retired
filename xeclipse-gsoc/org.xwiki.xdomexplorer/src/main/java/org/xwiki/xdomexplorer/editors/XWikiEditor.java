package org.xwiki.xdomexplorer.editors;

import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class XWikiEditor extends TextEditor
{
    private XDOMOutlinePage xdomOutlinePage;

    @Override
    protected void editorSaved()
    {
        super.editorSaved();

        if (xdomOutlinePage != null) {
            xdomOutlinePage.update();
        }
    }

    @Override
    public Object getAdapter(Class adapter)
    {
        if (adapter.equals(IContentOutlinePage.class)) {
            if (xdomOutlinePage == null) {
                xdomOutlinePage = new XDOMOutlinePage(getDocumentProvider().getDocument(getEditorInput()));
            }
            return xdomOutlinePage;
        }
        return super.getAdapter(adapter);
    }

}
