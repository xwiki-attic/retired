
package com.xpn.xwiki.wiked.internal.ui.editor;

import java.net.MalformedURLException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.repository.IPage;
import com.xpn.xwiki.wiked.repository.RepositoryException;

/**
 * Multi-page editor consists of the source editor (WikiMarkupEditor)
 * and browser-based preview (BrowserControl) pages.
 * @see com.xpn.xwiki.wiked.internal.ui.editor.WikiMarkupEditor
 * @see com.xpn.xwiki.wiked.internal.ui.editor.BrowserControl
 */
public class PageEditor extends MultiPageEditorPart 
    implements IResourceChangeListener, INavigableEditor {

    /** The page to be edited */
    private IPage page;
	/** The text editor */
	protected WikiMarkupEditor editor;
    /** The preview pane */
	private BrowserControl browser;
	/** An index of the preview page */
	private int previewPageIndex = -1;
    /** The outline */
    private WikiMarkupOutline contentOutline;

    public static final String ID = "com.xpn.xwiki.wiked.WikedPageEditor";
    
    /**
     * Provides the IEditorInput adapter
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class adapter) {
        if (adapter.equals(IContentOutlinePage.class)) {
            return getContentOutline(getEditorInput());
        }
        return (adapter == IEditorInput.class) ? 
            getEditorInput() : super.getAdapter(adapter);
    }

    /**
     * The <code>MultiPageEditorExample</code> implementation of this method
     * checks that the input is an instance of <code>IFileEditorInput</code>. It retrieves 
     * the IPage instance as well and sets the editor title according to it.
     */
    public void init(IEditorSite site, IEditorInput editorInput) 
        throws PartInitException {
        super.init(site, editorInput);
        page = (IPage) editorInput.getAdapter(IPage.class);
        setPartName((page != null) ? page.getTitle() : editorInput.getName());
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    /**
	 * The <code>MultiPageEditorPart</code> implementation of this <code>IWorkbenchPart</code>
	 * method disposes all nested editors. Subclasses may extend.
	 */
	public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		IDocumentProvider provider = editor.getDocumentProvider();
		IDocument document = provider.getDocument(editor.getEditorInput());
		try {
            document.set(page.getContent());
        } catch (RepositoryException ex) {
            WikedPlugin.logError("page getcontent failed in dispose", ex);
        }
		super.dispose();
	}
	
    /**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
        if (this.contentOutline != null)
            this.contentOutline.setEditorInput(getEditorInput());
	}
	
    /**
	 * No save-as is allowed and implemented.
	 */
	public void doSaveAs() {
	}
	
    /**
	 * The market support
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}
	
    /**
     * No save-as
     */
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	/**
	 * Closes all project files on project close.
     * @see IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i < pages.length; i++) {
                        FileEditorInput input = (FileEditorInput) editor.getEditorInput();
                        IProject project = input.getFile().getProject();
                        if (project != null && project.equals(event.getResource())) {
                            IEditorPart editorPart = pages[i].findEditor(input);
                            if (editorPart != null) {
                            	pages[i].closeEditor(editorPart, true);
                            }
                        }
					}
				}
			});
		}
	}

    /**
	 * @throws BadLocationException
     * @see com.xpn.xwiki.wiked.internal.ui.editor.INavigableEditor#navigateTo(int)
	 */
	public void navigateTo(int lineNumber) throws BadLocationException {
        IWorkbenchPage page = this.editor.getSite().getPage();
        IEditorPart part = page.getActiveEditor();
        IDocument document = this.editor.getDocumentProvider().getDocument(part.getEditorInput());
        this.editor.selectAndReveal(document.getLineOffset(lineNumber), 
            document.getLineLength(lineNumber));
	}
    
    /**
     * Creates the pages of the multi-page editor.
     * Currently, two pages are added: the source and preview page.
     */
    protected void createPages() {
        createEditorPage();
        createPreviewPage();
    }

    /**
     * Synchronize the source and preview editors
     * @param newPageIndex changed page index
	 * @see org.eclipse.ui.part.MultiPageEditorPart#pageChange(int)
	 */
	protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);
        if (newPageIndex == previewPageIndex) {
            IDocumentProvider provider = editor.getDocumentProvider();
            IDocument document = provider.getDocument(editor.getEditorInput());
            try {
                String text = renderContent(document.get());
                this.browser.setText(text);
            } catch (Exception ex) {
                WikedPlugin.logError("render page failed", ex);
            }
        }
	}
	
    /**
	 * @param string
	 * @return
     * @throws MalformedURLException
	 */
	private String renderContent(String content) throws RepositoryException, MalformedURLException {
		return this.page.getSpace().getRepository().renderContent(content);
	}

	private void createEditorPage() {
        try {
            IEditorInput input = getEditorInput();
            if (input instanceof PageEditorInput) {
                boolean remote = ((PageEditorInput)input).isRemotePage();
            	editor = new WikiMarkupEditor(remote);
            } else {
            	editor = new WikiMarkupEditor(false);
            }
            setPageText(addPage(editor, input), "&Source");
        } catch (PartInitException e) {
            ErrorDialog.openError(getSite().getShell(), 
                "Error creating nested text editor", null, e.getStatus());
        }
    }

    private void createPreviewPage() {
        Composite composite = new Composite(getContainer(), SWT.NONE);
        composite.setLayout(new FillLayout());
        browser = new BrowserControl(composite, SWT.NONE, 
            getEditorSite().getActionBars());
        previewPageIndex = addPage(composite);
        setPageText(previewPageIndex, "&Preview");
    }

    private Object getContentOutline(IEditorInput input) {
        if (this.contentOutline == null) {
            this.contentOutline = new WikiMarkupOutline();
        }
        this.contentOutline.setEditorInput(input);
        return this.contentOutline;
    }
}
