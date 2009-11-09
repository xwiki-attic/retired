package org.xwiki.eclipse.ui.editors.contentassist;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.xwiki.eclipse.ui.editors.PageEditor;

/**
 * Generate proposals for velocity scripts in the velo_parsed_string content type
 * @author malaka
 *
 */

public class VeloStringCompletionProcessor extends AbstractVeloCompletionProcessor{

	public VeloStringCompletionProcessor(PageEditor editor) {
		super(editor);
		// TODO Auto-generated constructor stub
	}

	/**
	 * implements the completion proposal for velo scripts in the velo_parsed_string content 
	 */
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		
		ICompletionProposal[] proposals = null;
		IDocument doc = viewer.getDocument();

		VelocityTextGuesser prefix = new VelocityTextGuesser(doc, offset);
		
		if (prefix.getType() == VelocityTextGuesser.TYPE_VARIABLE) {
			proposals = getVariableProposals(prefix.getText(), offset
					- prefix.getText().length());
		}else if (prefix.getType() == VelocityTextGuesser.TYPE_API_VARIABLE) {
			proposals=getAPIProposal(prefix.getXwkiAPIObject(), viewer, offset
					- prefix.getText().length());
			
		}
		return proposals;
	}

}
