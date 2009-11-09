package org.xwiki.eclipse.ui.editors.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.xwiki.eclipse.ui.editors.PageEditor;

import org.xwiki.eclipse.ui.editors.parser.model.DirectiveNode;

/**
 * This class abstract out the common proposals used by the VeloDefaultCompletionProcessor & VeloStringCompletionProcessor    
 * @author malaka
 *
 */
public abstract class AbstractVeloCompletionProcessor extends
		AbstractCompletionProcessor {

	protected PageEditor pageEditor;
	
	public AbstractVeloCompletionProcessor(PageEditor editor){
		pageEditor = editor;
	}
	
	/**
	 * Returns proposals from all directives with given prefix.
	 * @param aPrefix prefix to propose
 	 * @param anOffset offset of the proposal
	 * @return
	 */
	protected ICompletionProposal[] getDirectiveProposals(String aPrefix,
			int anOffset) {
		List<CompletionProposal> proposals = new ArrayList<CompletionProposal>();

		// Add system directives
		String[] directives = DirectiveNode.DIRECTIVES;
		for (int i = directives.length - 1; i >= 0; i--) {
			String directive = directives[i];
			if (directive.substring(1).startsWith(aPrefix)) {
				int cursorPos;
				if (i == DirectiveNode.TYPE_ELSE || i == DirectiveNode.TYPE_END
						|| i == DirectiveNode.TYPE_STOP) {
					cursorPos = directive.length() - 1;
				} else {
					directive += "()";
					cursorPos = directive.length() - 2;
				}
				proposals.add(new CompletionProposal(directive.substring(1),
						anOffset, aPrefix.length(), cursorPos, null, directive,
						null, null));
			}
		}

		Collections.sort(proposals, PROPOSAL_COMPARATOR);
		return proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

	/**
	 * Returns proposals from all variables with given prefix.
	 * @param prefix
	 * @param offset
	 * @return
	 */
	protected ICompletionProposal[] getVariableProposals(String prefix,
			int offset) {
		ICompletionProposal[] result = null;
		
		List<String> variables = pageEditor.getVariables(pageEditor.getLine(offset));
		addXwikiContextVariables(variables);
		
		TreeSet variableSet = new TreeSet();
		variableSet.addAll(variables);
		
		if (!variableSet.isEmpty()) {
			List<CompletionProposal> proposals = new ArrayList<CompletionProposal>();
			Iterator<String> iter = variableSet.iterator();
			while (iter.hasNext()) {
				String variable = iter.next();
				if (variable.substring(1).startsWith(prefix)) {
					proposals.add(new CompletionProposal(variable.substring(1),
							offset, prefix.length(), variable.length() - 1,
							null, variable, null, null));
				}
			}
			Collections.sort(proposals, PROPOSAL_COMPARATOR);
			result = proposals
					.toArray(new ICompletionProposal[proposals.size()]);
		}
		return result;
	}

	/**
	 * propose the xwiki api depending on the context variable
	 * @param contextType type of the context variable 
	 * @param viewer
	 * @param anOffset
	 * @return
	 */
	protected ICompletionProposal[] getAPIProposal(XwkiAPIObject contextType,
			ITextViewer viewer, int anOffset) {

		XwikiAPIProcessor xwikiAPIProcessor = new XwikiAPIProcessor(contextType);
		return xwikiAPIProcessor.computeCompletionProposals(viewer, anOffset);
	}

	// TODO : This is a temp implementation 
	//This is where the velocity parser should be integrated to get the list defined variables.
	private void addXwikiContextVariables(List<String> variableList) {
		variableList.add("$xwiki");
		variableList.add("$doc");
		variableList.add("$context");
		variableList.add("$request");
	}
}
