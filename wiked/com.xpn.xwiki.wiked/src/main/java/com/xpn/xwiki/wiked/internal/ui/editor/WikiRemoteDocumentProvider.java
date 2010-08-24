package com.xpn.xwiki.wiked.internal.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.repository.IPage;
import com.xpn.xwiki.wiked.repository.RepositoryException;

public class WikiRemoteDocumentProvider extends AbstractDocumentProvider {

	public boolean isModifiable(Object element) {
		return true;
	}

	public boolean isReadOnly(Object element) {
		return false;
	}

	protected IAnnotationModel createAnnotationModel(Object element) {
		return new AnnotationModel();
	}

	protected IDocument createDocument(Object element) {
		ElementInfo info = getElementInfo(element);
		if (info != null) {
			return info.fDocument;
		}
		Document doc = new Document();
		try {
			doc.set(getPage(element).getContent());
		} catch (RepositoryException ex) {
			WikedPlugin.logError("Failed to load page ", ex);
		}
		return doc;
	}

	protected void doSaveDocument(IProgressMonitor monitor, Object element,
		IDocument document, boolean overwrite) {
		IPage page = getPage(element);
		try {
			page.setContent(document.get());
		} catch (Exception ex) {
			WikedPlugin.handleError("Error saving page "+page.getTitle(), ex);
		}
	}

	protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
		return null;
	}

	private IPage getPage(Object element) {
		IEditorInput input = (IEditorInput) element;
		return (IPage)input.getAdapter(IPage.class);
	}

}
