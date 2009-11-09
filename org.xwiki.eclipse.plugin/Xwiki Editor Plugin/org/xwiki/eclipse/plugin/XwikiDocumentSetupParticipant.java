package org.xwiki.eclipse.plugin;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;

/**
 * @author venkatesh
 *
 */

public class XwikiDocumentSetupParticipant implements IDocumentSetupParticipant {

	public XwikiDocumentSetupParticipant() {
	}

	/*
	 * @see org.eclipse.core.filebuffers.IDocumentSetupParticipant#setup(org.eclipse.jface.text.IDocument)
	 */
	public void setup(IDocument document) {
		if (document instanceof IDocumentExtension3) {
			IDocumentExtension3 extension3= (IDocumentExtension3) document;
			IDocumentPartitioner partitioner= new FastPartitioner(XwikiEditorExamplePlugin.getDefault().getXwikiPartitionScanner(), XwikiPartitionScanner.XWIKI_PARTITION_TYPES);
			extension3.setDocumentPartitioner(XwikiEditorExamplePlugin.XWIKI_PARTITIONING, partitioner);
			partitioner.connect(document);
		}
	}
}
