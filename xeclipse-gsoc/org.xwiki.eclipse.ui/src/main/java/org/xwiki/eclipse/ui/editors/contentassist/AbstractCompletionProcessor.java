package org.xwiki.eclipse.ui.editors.contentassist;

import java.util.Comparator;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/**
 * Super class for all the CompletionProcessors.Each subclass will provide it's own computeCompletionProposals implementation 
 * @author malaka
 *
 */
public abstract class AbstractCompletionProcessor implements IContentAssistProcessor {
	
	
	/**
	 * Used to sort the completion proposals   
	 */
	protected static Comparator<CompletionProposal> PROPOSAL_COMPARATOR = new Comparator<CompletionProposal>() {
		public int compare(CompletionProposal proposal1, CompletionProposal proposal2) {
			String text1 = ((CompletionProposal) proposal1).getDisplayString();
			String text2 = ((CompletionProposal) proposal2).getDisplayString();
			return text1.compareTo(text2);
		}

		public boolean equals(Object proposal) {
			return false;
		}
	};

	/**
	 * Generate proposal for the given offset of the document
	 */
	public abstract ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset);

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		// A better method would be to use the validate() function in one of the
		// ICompleteProposalExtensions, not sure how to implement as of now.
		return "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%~^&*()1234567890/?.>,<;:'\"]}[{-_=+]".toCharArray(); //$NON-NLS-1$
	}

	public char[] getContextInformationAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
