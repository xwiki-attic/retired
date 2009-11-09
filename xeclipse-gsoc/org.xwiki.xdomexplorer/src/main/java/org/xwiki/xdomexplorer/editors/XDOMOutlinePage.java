package org.xwiki.xdomexplorer.editors;

import java.io.StringReader;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.XDOM;
import org.xwiki.rendering.parser.Parser;
import org.xwiki.rendering.wikimodel.parser.WikiModelXWikiParser;
import org.xwiki.xdomexplorer.utils.SafeRunnableWithResult;

public class XDOMOutlinePage extends ContentOutlinePage {
	private IDocument document;

	private TreeViewer xdomTreeViewer;

	public XDOMOutlinePage(IDocument document) {
		this.document = document;
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		xdomTreeViewer = getTreeViewer();
		xdomTreeViewer.setContentProvider(new XDOMContentProvider());
		xdomTreeViewer.setLabelProvider(new XDOMLabelProvider());
		update();
	}

	public void update() {
		XDOM xdom = parse(document);
		xdomTreeViewer.setInput(xdom);
		xdomTreeViewer.expandAll();
	}

	private XDOM parse(final IDocument document) {
		SafeRunnableWithResult<XDOM> runnable = new SafeRunnableWithResult<XDOM>() {

			public void run() throws Exception {
				Parser parser = new WikiModelXWikiParser();
				XDOM result = parser.parse(new StringReader(document.get()));
				Block b= result.getRoot();
				
				setResult(result);
			}

		};
		SafeRunner.run(runnable);

		return runnable.getResult();
	}
}
