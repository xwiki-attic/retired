package org.xwiki.eclipse.plugin.xwiki;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Point;

/**
 * @author venkatesh
 *
 */

public class XwikiCompletionProcessor implements IContentAssistProcessor,ICompletionProposalExtension4{

	protected static class Validator implements IContextInformationValidator, IContextInformationPresenter {

		protected int fInstallOffset;

		/*
		 * @see IContextInformationValidator#isContextInformationValid(int)
		 */
		public boolean isContextInformationValid(int offset) {
			return Math.abs(fInstallOffset - offset) < 5;
		}

		/*
		 * @see IContextInformationValidator#install(IContextInformation, ITextViewer, int)
		 */
		public void install(IContextInformation info, ITextViewer viewer, int offset) {
			fInstallOffset= offset;
		}

		/*
		 * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#updatePresentation(int, TextPresentation)
		 */
		public boolean updatePresentation(int documentPosition, TextPresentation presentation) {
			return false;
		}
	}

	protected IContextInformationValidator fValidator= new Validator();

	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
		// Retrieve current document
		IDocument doc = viewer.getDocument();
		// Retrieve current selection range
		Point selectedRange = viewer.getSelectedRange();
		List propList = new ArrayList();
		if (selectedRange.y > 0) {
			try {

				// Retrieve selected text
				String text = doc.get(selectedRange.x, selectedRange.y);

				// Compute completion proposals
				computeStyleProposals(text, selectedRange, propList);

			} catch (BadLocationException e) {

			}
		} else {
			// Retrieve qualifier
			String qualifier = getQualifier(doc, documentOffset);
			// Compute completion proposals
			try {
				doc.getChar(documentOffset-1);
			} catch (BadLocationException e) {
				// start of document, default proposals.
				qualifier="";
				computeStructureProposals(qualifier, documentOffset, propList);
			}
			if(!qualifier.isEmpty())
			{
				if(qualifier.equals("\n") || qualifier.equals(" ") || qualifier.equals("\t") || qualifier.startsWith("\n")) { qualifier=""; } // check for default content proposals, after empty space/new line
				computeStructureProposals(qualifier, documentOffset, propList);
			}
		}
		ICompletionProposal[] proposals = new ICompletionProposal[propList.size()];
		// and fill with list elements
		propList.toArray(proposals);
		// Return the proposals
		return proposals; 
	}
	// position of cursor adjustment
	private final static int[] STRUCTTAGS4 =  new int[] {
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,0,-1,-1,-1,-1,-1,-1,-1,-1,-1 	// -1 for the end of the replaced text
	};
	// proposal before text
	private final static String[] STRUCTTAGS1 = new String[]{
		"1 ","1.1 ","1.1.1 ","1.1.1.1 ","1.1.1.1.1 ","1.1.1.1.1.1 ","<dl>\n\t","*","~~","__","--","<tt>","<sup>","<sub>","{quote:}","{table}\n","{pre}","{code}","#info(\"","#warning(\"","#error(\"","#floatingbox(\"","----\n","\n\n"
	};
	// Proposal after text
	private final static String[] STRUCTTAGS2 =	new String[]{
		"","","","","","","\n</dl>","*","~~","__","--","</tt>","</sup>","</sub>","{quote}","\n{table}","{/pre}","{code}","\")","\")","\")","\")","",""
	};
	// name of proposal
	private final static String[] STRUCTTAGS3 =	new String[]{
		"1 Title 1","1.1 Title 2","1.1.1 Title 3","1.1.1.1 Title 4","1.1.1.1.1 Title 5","1.1.1.1.1.1 Title 6","<dl> Definition List","*Bold*","~~Italics~~","__Underline__","--Strikethrough--","<tt>Monospace</tt>","<sup>Superscript</sup>","<sub>Subscript</sub>","{Quote:}","{Table}","{pre}","{code}","#info","#warning","#error","#floatingbox","Horizonal Rule","New Paragraph"
	};

	private void computeStyleProposals(String selectedText, Point selectedRange, List propList) {

		// Loop through all styles
		for (int i = 0; i < STRUCTTAGS1.length; i++) {
			String tag = STRUCTTAGS1[i];
			String end = STRUCTTAGS2[i];

			// Compute replacement text
			String replacement = tag + selectedText + end;  

			// Derive cursor position
			int cursor = 0;

			if(STRUCTTAGS4[i]==-1)
			{
				cursor = end.length()+tag.length()+selectedText.length();
			}
			if(STRUCTTAGS4[i]!=-1)
			{
				//System.out.println(STRUCTTAGS4[i]);
				cursor = end.length()+STRUCTTAGS4[i]; // For quote.. i think its an anomaly.
			}


			// Compute a suitable context information
			IContextInformation contextInfo = 
				new ContextInformation(null, STRUCTTAGS3[i]); 

			// Construct proposal
			CompletionProposal proposal = new CompletionProposal(replacement, 
					selectedRange.x, selectedRange.y, cursor, null, STRUCTTAGS3[i], 
					contextInfo, replacement);

			// and add to result list
			propList.add(proposal);
		}
	}

	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		// A better method would be to use the validate() function in one of the ICompleteProposalExtensions, not sure how to implement as of now.
		return "ABCDEFGHIJKLMNOPQRSTUVWXYZbcdefghijklmnopqrstuvwxyz!@#$%~^&*()1234567890/?.>,<;:'\"]}[{-_=+]".toCharArray(); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return fValidator;
	}

	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public String getErrorMessage() {
		return null;
	}

	private String getQualifier(IDocument doc, int documentOffset) {
		StringBuffer buf = new StringBuffer();
		String lastfound = "";
		while (true) {
			try {
				char c = doc.getChar(--documentOffset);
				buf.append(c);
			} catch (BadLocationException e) {
				break;	// Start of document reached
			}
			int i=0;
			String inbuffer = buf.reverse().toString().toLowerCase();
			//System.out.println("inbuffer: "+inbuffer);
			buf.reverse().toString(); // reverse actually reverses the buffer.
			for(int j=0;j<STRUCTTAGS1.length;j++)
			{
				i++;
				// why is #fl causing an error??
				//System.out.println(STRUCTTAGS1[j].toLowerCase()+" "+inbuffer+" "+(STRUCTTAGS1[j].toLowerCase().indexOf(inbuffer) !=-1));
				if(STRUCTTAGS1[j].toLowerCase().indexOf(inbuffer) !=-1)
				{
					lastfound=inbuffer;
					break;
				}
			}
			if(i==STRUCTTAGS1.length)
			{
				break;
			}
		}
		return lastfound;
	}

	private void computeStructureProposals(String qualifier, int documentOffset, List propList) { 
		int qlen = qualifier.length();

		// Loop through all proposals
		for (int i = 0; i < STRUCTTAGS1.length; i++) {
			String startTag = STRUCTTAGS1[i];

			// Check if proposal matches qualifier
			if (startTag.toLowerCase().startsWith(qualifier.toLowerCase())) {

				// Yes -- compute whole proposal text
				String text = startTag.toLowerCase() + STRUCTTAGS2[i].toLowerCase();

				// Derive cursor position
				int cursor = startTag.length();

				// Construct proposal
				CompletionProposal proposal =
					new CompletionProposal(text, documentOffset - qlen, qlen,cursor,null,STRUCTTAGS3[i],null,null);

				// and add to result list
				propList.add(proposal);
			}
		}
	}

	public boolean isAutoInsertable() {
		//For only 1 proposal
		return true;
	}
}