package org.xwiki.eclipse.plugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IEditorStatusLine;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author venkatesh
 *
 */

public class XwikiEditor extends TextEditor implements IEditorStatusLine {
	
	private XwikiContentOutlinePage outline_page;

	public XwikiEditor() {
		super();
	}
	protected void createActions() {
		super.createActions();

		IAction a= new TextOperationAction(XwikiEditorMessages.getResourceBundle(), "ContentAssistProposal.", this, ISourceViewer.CONTENTASSIST_PROPOSALS); //$NON-NLS-1$
		a.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction("ContentAssistProposal", a); //$NON-NLS-1$

		a= new TextOperationAction(XwikiEditorMessages.getResourceBundle(), "ContentAssistTip.", this, ISourceViewer.CONTENTASSIST_CONTEXT_INFORMATION);  //$NON-NLS-1$
		a.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_CONTEXT_INFORMATION);
		setAction("ContentAssistTip", a); //$NON-NLS-1$
	}

	public void dispose() {
		if (outline_page != null)
			outline_page.setInput(null);
		super.dispose();
	}

	public void doRevertToSaved() {
		super.doRevertToSaved();
		if (outline_page != null)
			outline_page.update();
	}

	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		if (outline_page != null)
			outline_page.update();
	}

	public void doSaveAs() {
		super.doSaveAs();
		if (outline_page != null)
			outline_page.update();
	}

	public void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		if (outline_page != null)
			outline_page.setInput(input);
	}
	
	public Object getAdapter(Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (outline_page == null) {
				outline_page= new XwikiContentOutlinePage(getDocumentProvider(), this);
				if (getEditorInput() != null)
					outline_page.setInput(getEditorInput());
			}
			return outline_page;
		}
		return super.getAdapter(required);
	}

	/*
	 * @see org.eclipse.ui.texteditor.ExtendedTextEditor#editorContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		addAction(menu, "ContentAssistProposal"); //$NON-NLS-1$
		addAction(menu, "ContentAssistTip"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * Method declared on AbstractTextEditor
	 */
	protected void initializeEditor() {
		super.initializeEditor();
		setSourceViewerConfiguration(new XwikiSourceViewerConfiguration());
	}

	/*
	 * @see org.eclipse.ui.texteditor.ExtendedTextEditor#createSourceViewer(org.eclipse.swt.widgets.Composite, org.eclipse.jface.text.source.IVerticalRuler, int)
	 */
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {

		fAnnotationAccess= createAnnotationAccess();
		fOverviewRuler= createOverviewRuler(getSharedColors());

		ISourceViewer viewer= new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);
		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);

		//Word Wrap functionality, added for the betterment of "double enter" functionality.
		viewer.getTextWidget().setWordWrap(true);

		return viewer;
	}

	/*
	 * @see org.eclipse.ui.texteditor.ExtendedTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
	}

	/*
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#adjustHighlightRange(int, int)
	 */
	protected void adjustHighlightRange(int offset, int length) {
		ISourceViewer viewer= getSourceViewer();
		if (viewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension= (ITextViewerExtension5) viewer;
			extension.exposeModelRange(new Region(offset, length));
		}
	}
	//TODO How to implement this in practice?
	public void setMessage(boolean error, String message, Image image) {
		IStatusLineManager statusline = getStatusLineManager();
		if(!error)
			statusline.setMessage(image,message);
		if(error)
			statusline.setErrorMessage(image,message);
	}
}