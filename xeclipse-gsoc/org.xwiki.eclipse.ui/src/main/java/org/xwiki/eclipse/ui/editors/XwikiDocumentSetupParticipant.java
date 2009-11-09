package org.xwiki.eclipse.ui.editors;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.xwiki.eclipse.ui.editors.scanners.DocumentPartitionScanner;
import org.xwiki.eclipse.ui.editors.scanners.EditorScanners;

/**
 * @author venkatesh
 * 
 */
/**
 * Todo: This class is not used any more.It should be removed.
 */
public class XwikiDocumentSetupParticipant implements IDocumentSetupParticipant
{

    public XwikiDocumentSetupParticipant()
    {
    }

    /*
     * @see org.eclipse.core.filebuffers.IDocumentSetupParticipant#setup(org.eclipse.jface.text.IDocument)
     */
    public void setup(IDocument document)
    {
        if (document instanceof IDocumentExtension3) {
            IDocumentExtension3 extension3 = (IDocumentExtension3) document;
            IDocumentPartitioner partitioner =
                new FastPartitioner(EditorScanners.getDefault().getDocumentPartitionScanner(),
                    DocumentPartitionScanner.XWIKI_PARTITION_TYPES);
            extension3.setDocumentPartitioner(EditorScanners.XWIKI_PARTITIONING, partitioner);
            partitioner.connect(document);
        }
    }
}
