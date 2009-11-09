package xwiki.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import xwiki.scanners.XWikiBoxScanner;
import xwiki.scanners.XWikiEnhancedTextScanner;
import xwiki.scanners.XWikiPartitionScanner;
import xwiki.scanners.XWikiScanner;
import xwiki.scanners.XWikiTitleScanner;

public class XWikiConfiguration extends SourceViewerConfiguration {
	private XWikiDoubleClickStrategy doubleClickStrategy;
	private XWikiTitleScanner titleScanner;
	private XWikiEnhancedTextScanner enhancedTextScanner;
	private XWikiBoxScanner boxScanner;
	private XWikiScanner scanner;
	private ColorManager colorManager;

	public XWikiConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE,
				XWikiPartitionScanner.XWIKI_TEXT_ENHANCE,
				XWikiPartitionScanner.XWIKI_TITLE,
				XWikiPartitionScanner.XWIKI_BOXES };
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(
			ISourceViewer sourceViewer, String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new XWikiDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected XWikiScanner getXMLScanner() {
		if (scanner == null) {
			scanner = new XWikiScanner(colorManager);
			scanner.setDefaultReturnToken(new Token(new TextAttribute(
					colorManager.getColor(IXWikiColorConstants.DEFAULT))));
		}
		return scanner;
	}

	protected XWikiTitleScanner getXWikiTitleScanner() {
		if (titleScanner == null) {
			titleScanner = new XWikiTitleScanner(colorManager);
			titleScanner
					.setDefaultReturnToken(new Token(
							new TextAttribute(
									colorManager
											.getColor(IXWikiColorConstants.KEYWORD_DEFAULT))));
		}
		return titleScanner;
	}

	protected XWikiEnhancedTextScanner getEnhancedTextScanner() {
		if (enhancedTextScanner == null) {
			enhancedTextScanner = new XWikiEnhancedTextScanner(colorManager);
			enhancedTextScanner.setDefaultReturnToken(new Token(
					new TextAttribute(colorManager
							.getColor(IXWikiColorConstants.ENHANCED_TEXT))));
		}
		return enhancedTextScanner;
	}

	protected XWikiBoxScanner getBoxScanner() {
		if (boxScanner == null) {
			boxScanner = new XWikiBoxScanner(colorManager);
			boxScanner.setDefaultReturnToken(new Token(	new TextAttribute(colorManager.getColor(IXWikiColorConstants.KEYWORD_DEFAULT))));
		}
		return boxScanner;
	}

	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(
				getXWikiTitleScanner());
		reconciler.setDamager(dr, XWikiPartitionScanner.XWIKI_TITLE);
		reconciler.setRepairer(dr, XWikiPartitionScanner.XWIKI_TITLE);

		dr = new DefaultDamagerRepairer(getEnhancedTextScanner());
		reconciler.setDamager(dr, XWikiPartitionScanner.XWIKI_TEXT_ENHANCE);
		reconciler.setRepairer(dr, XWikiPartitionScanner.XWIKI_TEXT_ENHANCE);

		dr = new DefaultDamagerRepairer(getBoxScanner());
		reconciler.setDamager(dr, XWikiPartitionScanner.XWIKI_BOXES);
		reconciler.setRepairer(dr, XWikiPartitionScanner.XWIKI_BOXES);

		dr = new DefaultDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		return reconciler;
	}

}