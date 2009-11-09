package org.xwiki.eclipse.ui.editors.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/**
 * This class does nothing on it's own.It delegates to xwiki / velocity completion processor depending on the context  
 * @author malaka
 *
 */
public class DefaultCompletionProcessor extends AbstractCompletionProcessor {

	// hold the reference for current proposal
	private IContentAssistProcessor assistProcessor;

	//this processors are inject to this by XwikiSourceViewerConfiguration at the runtime
	private IContentAssistProcessor xwikiProsessor;

	private IContentAssistProcessor veloProcessor;

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		if (!isVeloCode(viewer, offset)) {
			assistProcessor = xwikiProsessor;
		} else {
			assistProcessor = veloProcessor;
		}

		return assistProcessor.computeCompletionProposals(viewer, offset);
	}

	private static final boolean isVeloChar(char aChar) {

		return aChar == '#' || aChar == '$';
	}

	public void setXwikiProsessor(IContentAssistProcessor xwikiProsessor) {
		this.xwikiProsessor = xwikiProsessor;
	}

	public void setVeloProcessor(IContentAssistProcessor veloProcessor) {
		this.veloProcessor = veloProcessor;
	}

	private boolean isVeloCode(ITextViewer viewer, int start) {

		IDocument doc = viewer.getDocument();
		try {
			char chr = doc.getChar(start - 1);
			if (isVeloChar(chr)) {
				return true;
			}
			while (start > 0 && doc.getChar(start - 1) != '\n') {
				// # or $ found
				if (isVeloChar(doc.getChar(start - 1))) {
					return true;
				}
				start--;
			}
			return false;
		} catch (BadLocationException e1) {
			//DOTO: log the error 
		}
		return false;

	}

}
