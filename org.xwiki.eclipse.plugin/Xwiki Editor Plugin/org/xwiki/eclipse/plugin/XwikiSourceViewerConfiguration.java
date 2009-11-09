package org.xwiki.eclipse.plugin;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.DefaultHyperlinkPresenter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.hyperlink.URLHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.xwiki.eclipse.plugin.table.TableAutoEditStrategy;
import org.xwiki.eclipse.plugin.util.HyperlinkDetector;
import org.xwiki.eclipse.plugin.xwiki.DoubleClickStrategy;
import org.xwiki.eclipse.plugin.xwiki.XwikiAutoEditStrategy;
import org.xwiki.eclipse.plugin.xwiki.XwikiCompletionProcessor;

/**
 * @author venkatesh
 *
 */

public class XwikiSourceViewerConfiguration extends SourceViewerConfiguration {

	public XwikiSourceViewerConfiguration() {
	}

	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		//return new XwikiAnnotationHover();
		return null;
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 * 
	 */
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		// the following results in indentation from where it began.
		String ContentTypes[]=this.getConfiguredContentTypes(sourceViewer);
		int i = ContentTypes.length;
		while(i>=0)
		{
			i--;
			if(contentType.equals(ContentTypes[i]))
			{
				switch(i)
				{
				case 0: return new IAutoEditStrategy[] {new XwikiAutoEditStrategy()};
				case 1: return new IAutoEditStrategy[] {new XwikiAutoEditStrategy()};
				case 2: return new IAutoEditStrategy[] {new XwikiAutoEditStrategy()};
				case 3: return new IAutoEditStrategy[] {new TableAutoEditStrategy()}; // change here
				case 4: return new IAutoEditStrategy[] {new XwikiAutoEditStrategy()};
				default: return new IAutoEditStrategy[] {new XwikiAutoEditStrategy()};
				}
			}
		}
		return null;
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredDocumentPartitioning(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		return XwikiEditorExamplePlugin.XWIKI_PARTITIONING;
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, XwikiPartitionScanner.XWIKI_CODE, XwikiPartitionScanner.XWIKI_PRE, XwikiPartitionScanner.XWIKI_TABLE, XwikiPartitionScanner.XWIKI_DL};
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

		ContentAssistant assistant= new ContentAssistant();
		assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		assistant.setContentAssistProcessor(new XwikiCompletionProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
		//Shouldn't I have one for internal code, since its actually code that is going to come inside CODE?
		//And also for other syntaxes, defined in detail in code macro section
		//assistant.setContentAssistProcessor(new CodeCompletionProcessor(), XwikiPartitionScanner.XWIKI_CODE);
		//pre does allow html and scripting.
		//assistant.setContentAssistProcessor(new PreCompletionProcessor(), XwikiPartitionScanner.XWIKI_PRE);
		//assistant.setContentAssistProcessor(new TableCompletionProcessor(), XwikiPartitionScanner.XWIKI_TABLE);
		//assistant.setContentAssistProcessor(new DlCompletionProcessor(), XwikiPartitionScanner.XWIKI_DL);

		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(250);
		assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_REMOVE);
		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
		assistant.setContextInformationPopupBackground(XwikiEditorExamplePlugin.getDefault().getXwikiColorProvider().getColor(new RGB(150, 150, 0)));

		//The below assists in completion proposal, for single proposal
		assistant.enableAutoInsert(true);
		assistant.setStatusLineVisible(true);
		assistant.setStatusMessage("Press Enter to select, or Esc to Cancel");
		assistant.setShowEmptyList(true);
		assistant.setEmptyMessage("No Matches");

		return assistant;
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 * What is this for??
	 */
	public String getDefaultPrefix(ISourceViewer sourceViewer, String contentType) {
		return "//"; 
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		return new DoubleClickStrategy();
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
		return new String[] { "\t", "    " }; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

		PresentationReconciler reconciler= new PresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr= new DefaultDamagerRepairer(XwikiEditorExamplePlugin.getDefault().getXwikiCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		dr= new DefaultDamagerRepairer(XwikiEditorExamplePlugin.getDefault().getCodeScanner());
		reconciler.setDamager(dr, XwikiPartitionScanner.XWIKI_CODE);
		reconciler.setRepairer(dr, XwikiPartitionScanner.XWIKI_CODE);
		dr= new DefaultDamagerRepairer(XwikiEditorExamplePlugin.getDefault().getPreScanner());
		reconciler.setDamager(dr, XwikiPartitionScanner.XWIKI_PRE);
		reconciler.setRepairer(dr, XwikiPartitionScanner.XWIKI_PRE);
		dr= new DefaultDamagerRepairer(XwikiEditorExamplePlugin.getDefault().getTableScanner());
		reconciler.setDamager(dr, XwikiPartitionScanner.XWIKI_TABLE);
		reconciler.setRepairer(dr, XwikiPartitionScanner.XWIKI_TABLE);
		dr= new DefaultDamagerRepairer(XwikiEditorExamplePlugin.getDefault().getDlScanner());
		reconciler.setDamager(dr, XwikiPartitionScanner.XWIKI_DL);
		reconciler.setRepairer(dr, XwikiPartitionScanner.XWIKI_DL);
		return reconciler;
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public int getTabWidth(ISourceViewer sourceViewer) {
		return 4;
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		return new XwikiTextHover(); // only a basic implementation till now..
		//return null;
	}

	public IUndoManager getUndoManager(ISourceViewer sourceViewer)
	{
		//TODO Should have a preferences dialog!
		return new TextViewerUndoManager(50);
	}

	/*
	public IQuickAssistAssistant getQuickAssistAssistant(ISourceViewer sourceViewer)
	{
		return null;
	}*/

	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (sourceViewer == null)
			return null;
		return new IHyperlinkDetector[] {new HyperlinkDetector(), new URLHyperlinkDetector() };
	}

	public IHyperlinkPresenter getHyperlinkPresenter(ISourceViewer sourceViewer) {
		return new DefaultHyperlinkPresenter(new RGB(0, 0, 255));
	}

	public int getHyperlinkStateMask(ISourceViewer sourceViewer) {
		return SWT.MOD1;
	}	
}