package org.xwiki.eclipse.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.xwiki.eclipse.editors.scanners.XWikiPartitionScanner;

/**
 * @author fmancinelli, venkatesh, malaka
 */
public class XWikiDocumentProvider extends FileDocumentProvider
{
    @Override
    protected IDocument createDocument(Object element) throws CoreException
    {
        IDocument document = super.createDocument(element);
        if (document != null) {
            IDocumentPartitioner partitioner =
                new FastPartitioner(new XWikiPartitionScanner(), XWikiPartitionScanner.ALL_PARTITIONS);
            partitioner.connect(document);
            document.setDocumentPartitioner(partitioner);
        }

        return document;
    }
}
