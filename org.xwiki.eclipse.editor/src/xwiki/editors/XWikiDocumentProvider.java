package xwiki.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import xwiki.scanners.XWikiPartitionScanner;

public class XWikiDocumentProvider extends FileDocumentProvider {

	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new XWikiPartitioner(
					new XWikiPartitionScanner(),
					new String[] {
						XWikiPartitionScanner.XWIKI_TEXT_ENHANCE,
						XWikiPartitionScanner.XWIKI_TITLE,
						XWikiPartitionScanner.XWIKI_BOXES
						 });
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
}