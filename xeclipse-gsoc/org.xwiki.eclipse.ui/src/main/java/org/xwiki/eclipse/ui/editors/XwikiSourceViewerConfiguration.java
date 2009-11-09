package org.xwiki.eclipse.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.DefaultHyperlinkPresenter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.hyperlink.URLHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.xwiki.eclipse.core.model.XWikiEclipsePage;
import org.xwiki.eclipse.ui.editors.contentassist.DefaultCompletionProcessor;
import org.xwiki.eclipse.ui.editors.contentassist.TableAutoEditStrategy;
import org.xwiki.eclipse.ui.editors.contentassist.VeloDefaultCompletionProcessor;
import org.xwiki.eclipse.ui.editors.contentassist.VeloStringCompletionProcessor;
import org.xwiki.eclipse.ui.editors.contentassist.XWikiLinkCompletionProcessor;
import org.xwiki.eclipse.ui.editors.contentassist.XwikiAutoEditStrategy;
import org.xwiki.eclipse.ui.editors.contentassist.XwikiCompletionProcessor;
import org.xwiki.eclipse.ui.editors.scanners.DocumentPartitionScanner;
import org.xwiki.eclipse.ui.editors.scanners.EditorScanners;
import org.xwiki.eclipse.ui.editors.scanners.detectors.HyperlinkDetector;
import org.xwiki.eclipse.ui.editors.util.ColorProvider;
import org.xwiki.eclipse.ui.editors.util.NonRuleBasedDamagerRepairer;

/**
 * @author venkatesh
 */

public class XwikiSourceViewerConfiguration extends SourceViewerConfiguration
{

    private XWikiEclipsePage page = null;

    private PageEditor pageEditor = null;

    public XwikiSourceViewerConfiguration(XWikiEclipsePage page, PageEditor pageEditor)
    {
        this.page = page;
        this.pageEditor = pageEditor;
    }

    @Override
    public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer)
    {
        // return new XwikiAnnotationHover();
        return null;
    }

    /*
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoEditStrategies
     * (org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
     */
    @Override
    public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType)
    {
        if (contentType.equals(DocumentPartitionScanner.XWIKI_TABLE)) {
            return new IAutoEditStrategy[] {new TableAutoEditStrategy()};
        }

        return new IAutoEditStrategy[] {new XwikiAutoEditStrategy()};
    }

    /*
     * @seeorg.eclipse.jface.text.source.SourceViewerConfiguration# getConfiguredDocumentPartitioning
     * (org.eclipse.jface.text.source.ISourceViewer)
     */
    @Override
    public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer)
    {
        return EditorScanners.XWIKI_PARTITIONING;
    }

    public IReconciler getReconciler(ISourceViewer aSourceViewer)
    {
        // pageEditor.getReconcilingStrategy().setISourceViewer(aSourceViewer);
        return new MonoReconciler(pageEditor.getReconcilingStrategy(), false);
    }

    /*
     * (non-Javadoc) Method declared on SourceViewerConfiguration
     */
    @Override
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer)
    {
        String[] pertitionTypes = DocumentPartitionScanner.XWIKI_PARTITION_TYPES;

        List<String> contentTypes = new ArrayList<String>();

        contentTypes.add(IDocument.DEFAULT_CONTENT_TYPE);// add the default contentType
        for (String partition : pertitionTypes) {
            contentTypes.add(partition);
        }

        String[] results = new String[contentTypes.size()];
        contentTypes.toArray(results);
        return results;
    }

    /*
     * (non-Javadoc) Method declared on SourceViewerConfiguration
     */
    @Override
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer)
    {
        ContentAssistant assistant = new ContentAssistant();
        // assistProcessor responsible for both xwiki and velo completion in default content type
        DefaultCompletionProcessor assistProcessor = new DefaultCompletionProcessor();
        assistProcessor.setXwikiProsessor(new XwikiCompletionProcessor(page));
        assistProcessor.setVeloProcessor(new VeloDefaultCompletionProcessor(pageEditor));

        assistant.setContentAssistProcessor(assistProcessor, IDocument.DEFAULT_CONTENT_TYPE);
        assistant.setContentAssistProcessor(new VeloStringCompletionProcessor(pageEditor),
            DocumentPartitionScanner.VELO_INTERPOLATED_STRING);
        
        assistant.setContentAssistProcessor(new XWikiLinkCompletionProcessor(page.getDataManager()), DocumentPartitionScanner.XWIKI_LINK);

        assistant.enableAutoActivation(true);
        assistant.setAutoActivationDelay(250);
        assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_REMOVE);
        assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
        assistant.setContextInformationPopupBackground(EditorScanners.getDefault().getXwikiColorProvider()
            .getColor(new RGB(150, 150, 0)));

        // The below assists in completion proposal, for single proposal
        assistant.enableAutoInsert(true);
        assistant.setStatusLineVisible(true);
        assistant.setStatusMessage("Press Enter to select, or Esc to Cancel");
        assistant.setShowEmptyList(true);
        assistant.setEmptyMessage("No Matches");
        return assistant;
    }

    /*
     * (non-Javadoc) Method declared on SourceViewerConfiguration What is this for??
     */
    public String getDefaultPrefix(ISourceViewer sourceViewer, String contentType)
    {
        return "//";
    }

    /*
     * (non-Javadoc) Method declared on SourceViewerConfiguration
     */
    @Override
    public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType)
    {
        return new DoubleClickStrategy();
    }

    /*
     * (non-Javadoc) Method declared on SourceViewerConfiguration
     */
    @Override
    public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType)
    {
        return new String[] {"\t", "    "}; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /*
     * (non-Javadoc) Method declared on SourceViewerConfiguration
     */
    @Override
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
    {
        PresentationReconciler reconciler = new PresentationReconciler();
        reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

        DefaultDamagerRepairer dr =
            new DefaultDamagerRepairer(EditorScanners.getDefault().getDefaultCodeScanner());
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

        // xwiki related damagerRepaires
        dr = new DefaultDamagerRepairer(EditorScanners.getDefault().getXwikiCodeScanner());
        reconciler.setDamager(dr, DocumentPartitionScanner.XWIKI_CODE);
        reconciler.setRepairer(dr, DocumentPartitionScanner.XWIKI_CODE);

        dr = new DefaultDamagerRepairer(EditorScanners.getDefault().getXwikiPreScanner());
        reconciler.setDamager(dr, DocumentPartitionScanner.XWIKI_PRE);
        reconciler.setRepairer(dr, DocumentPartitionScanner.XWIKI_PRE);

        dr = new DefaultDamagerRepairer(EditorScanners.getDefault().getDefaultCodeScanner());
        reconciler.setDamager(dr, DocumentPartitionScanner.XWIKI_TABLE);
        reconciler.setRepairer(dr, DocumentPartitionScanner.XWIKI_TABLE);

        dr = new DefaultDamagerRepairer(EditorScanners.getDefault().getXwikiDlScanner());
        reconciler.setDamager(dr, DocumentPartitionScanner.XWIKI_DL);
        reconciler.setRepairer(dr, DocumentPartitionScanner.XWIKI_DL);

        dr = new DefaultDamagerRepairer(EditorScanners.getDefault().getXwikiHeadingScanner());
        reconciler.setDamager(dr, DocumentPartitionScanner.XWIKI_HEADING);
        reconciler.setRepairer(dr, DocumentPartitionScanner.XWIKI_HEADING);

        dr = new DefaultDamagerRepairer(EditorScanners.getDefault().getXwikiListScanner());
        reconciler.setDamager(dr, DocumentPartitionScanner.XWIKI_LIST);
        reconciler.setRepairer(dr, DocumentPartitionScanner.XWIKI_LIST);

        dr = new DefaultDamagerRepairer(EditorScanners.getDefault().getMacroScanner());
        reconciler.setDamager(dr, DocumentPartitionScanner.XWIKI_MACRO);
        reconciler.setRepairer(dr, DocumentPartitionScanner.XWIKI_MACRO);

        dr = new DefaultDamagerRepairer(EditorScanners.getDefault().getXwikiTextStylesScanner());
        reconciler.setDamager(dr, DocumentPartitionScanner.XWIKI_TEXT_STYLES);
        reconciler.setRepairer(dr, DocumentPartitionScanner.XWIKI_TEXT_STYLES);

        dr = new DefaultDamagerRepairer(EditorScanners.getDefault().getXwikiLinkScanner());
        reconciler.setDamager(dr, DocumentPartitionScanner.XWIKI_LINK);
        reconciler.setRepairer(dr, DocumentPartitionScanner.XWIKI_LINK);

        dr = new DefaultDamagerRepairer(EditorScanners.getDefault().getMacroScanner());
        reconciler.setDamager(dr, DocumentPartitionScanner.XWIKI_START_FLOTING_MACRO);
        reconciler.setRepairer(dr, DocumentPartitionScanner.XWIKI_START_FLOTING_MACRO);

        // velo related damagerRepaires
        NonRuleBasedDamagerRepairer ndr =
            new NonRuleBasedDamagerRepairer(new TextAttribute(EditorScanners.getDefault().getXwikiColorProvider()
                .getColor(ColorProvider.VELO_COMMENT)));
        reconciler.setDamager(ndr, DocumentPartitionScanner.VELO_SINGLE_LINE_COMMENT);
        reconciler.setRepairer(ndr, DocumentPartitionScanner.VELO_SINGLE_LINE_COMMENT);

        ndr =
            new NonRuleBasedDamagerRepairer(new TextAttribute(EditorScanners.getDefault().getXwikiColorProvider()
                .getColor(ColorProvider.VELO_COMMENT)));
        reconciler.setDamager(ndr, DocumentPartitionScanner.VELO_MULTI_LINE_COMMENT);
        reconciler.setRepairer(ndr, DocumentPartitionScanner.VELO_MULTI_LINE_COMMENT);

        ndr =
            new NonRuleBasedDamagerRepairer(new TextAttribute(EditorScanners.getDefault().getXwikiColorProvider()
                .getColor(ColorProvider.VELO_DOC_COMMENT)));
        reconciler.setDamager(ndr, DocumentPartitionScanner.VELO_DOC_COMMENT);
        reconciler.setRepairer(ndr, DocumentPartitionScanner.VELO_DOC_COMMENT);

        ndr =
            new NonRuleBasedDamagerRepairer(new TextAttribute(EditorScanners.getDefault().getXwikiColorProvider()
                .getColor(ColorProvider.STRING)));
        reconciler.setDamager(ndr, DocumentPartitionScanner.VELO_NON_INTERPOLATED_STRING);
        reconciler.setRepairer(ndr, DocumentPartitionScanner.VELO_NON_INTERPOLATED_STRING);

        dr = new DefaultDamagerRepairer(EditorScanners.getDefault().getVelosityStringScanner());
        reconciler.setDamager(dr, DocumentPartitionScanner.VELO_INTERPOLATED_STRING);
        reconciler.setRepairer(dr, DocumentPartitionScanner.VELO_INTERPOLATED_STRING);

        return reconciler;
    }

    /*
     * (non-Javadoc) Method declared on SourceViewerConfiguration
     */
    @Override
    public int getTabWidth(ISourceViewer sourceViewer)
    {
        return 4;
    }

    /*
     * (non-Javadoc) Method declared on SourceViewerConfiguration
     */
    @Override
    public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType)
    {
        return new XwikiTextHover(); // only a basic implementation till
        // now..
        // return null;
    }

    @Override
    public IUndoManager getUndoManager(ISourceViewer sourceViewer)
    {
        // TODO Should have a preferences dialog!
        return new TextViewerUndoManager(50);
    }

    /*
     * public IQuickAssistAssistant getQuickAssistAssistant(ISourceViewer sourceViewer) { return null; }
     */

    @Override
    public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer)
    {
        if (sourceViewer == null)
            return null;
        return new IHyperlinkDetector[] {new HyperlinkDetector(), new URLHyperlinkDetector()};
    }

    @Override
    public IHyperlinkPresenter getHyperlinkPresenter(ISourceViewer sourceViewer)
    {
        return new DefaultHyperlinkPresenter(new RGB(0, 0, 255));
    }

    @Override
    public int getHyperlinkStateMask(ISourceViewer sourceViewer)
    {
        return SWT.MOD1;
    }
}
