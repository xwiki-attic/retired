
package com.xpn.xwiki.wiked.internal.ui.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.ContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class WikiSourceViewerConfiguration extends SourceViewerConfiguration {
    
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			WikiPartitionScanner.MACRO};
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
        ITokenScanner scanner = WikiMacroScanner.getDefaultScanner();
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		return reconciler;
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant= new ContentAssistant();
		assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		IContentAssistProcessor processor = new WikiCompletionProcessor();
		assistant.setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
		assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		assistant.enableAutoActivation(true);
		return assistant;	
	}
	
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		ContentFormatter contentFormatter = new ContentFormatter();
		contentFormatter.setFormattingStrategy(new WikiFormatingStrategy(),
            IDocument.DEFAULT_CONTENT_TYPE);
		return contentFormatter;
	}
}
