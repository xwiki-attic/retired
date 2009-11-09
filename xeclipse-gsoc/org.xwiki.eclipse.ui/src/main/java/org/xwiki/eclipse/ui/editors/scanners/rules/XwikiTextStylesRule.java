package org.xwiki.eclipse.ui.editors.scanners.rules;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class XwikiTextStylesRule extends SingleLineRule {
	
	
	public XwikiTextStylesRule(String startSequence, String endSequence,
			IToken token, char escapeCharacter, boolean breaksOnEOF,
			boolean escapeContinuesLine) {
		super(startSequence, endSequence, token, escapeCharacter, breaksOnEOF,
				escapeContinuesLine);
		// TODO Auto-generated constructor stub
	}

	private static class DecreasingCharArrayLengthComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			return ((char[]) o2).length - ((char[]) o1).length;
		}
	}
	private char[][] fLineDelimiters;
	private char[][] fSortedLineDelimiters;
	private Comparator fLineDelimiterComparator= new DecreasingCharArrayLengthComparator();
	    	
	protected boolean endSequenceDetected(ICharacterScanner scanner) {

		char[][] originalDelimiters= scanner.getLegalLineDelimiters();
		int count= originalDelimiters.length;
		if (fLineDelimiters == null || originalDelimiters.length != count) {
			fSortedLineDelimiters= new char[count][];
		} else {
			while (count > 0 && fLineDelimiters[count-1] == originalDelimiters[count-1])
				count--;
		}
		if (count != 0) {
			fLineDelimiters= originalDelimiters;
			System.arraycopy(fLineDelimiters, 0, fSortedLineDelimiters, 0, fLineDelimiters.length);
			Arrays.sort(fSortedLineDelimiters, fLineDelimiterComparator);
		}

		int readCount= 1;
		char c;
		char test;
		while ((c= (char)scanner.read()) != ICharacterScanner.EOF) {
			if (c == fEscapeCharacter) {
				// Skip escaped character(s)
				if (fEscapeContinuesLine) {
					c= (char)scanner.read();
					for (int i= 0; i < fSortedLineDelimiters.length; i++) {
						if (c == fSortedLineDelimiters[i][0] && sequenceDetected(scanner, fSortedLineDelimiters[i], true))
							break;
					}
				} else
					scanner.read();

			} else if (fEndSequence.length > 0 && c == fEndSequence[0]) {
				// Check if the specified end sequence has been found.
				scanner.unread();
				scanner.unread();
				test = (char)scanner.read();
				scanner.read();
				if (sequenceDetected(scanner, fEndSequence, true)){
					if(test != ' ') return true;
				}
			} else if (fBreaksOnEOL) {
				// Check for end of line since it can be used to terminate the pattern.
				for (int i= 0; i < fSortedLineDelimiters.length; i++) {
					if (c == fSortedLineDelimiters[i][0] && sequenceDetected(scanner, fSortedLineDelimiters[i], true))
						return false;
				}
			}
			readCount++;
		}
		
		if (fBreaksOnEOF)
			return true;

		for (; readCount > 0; readCount--)
			scanner.unread();

		return false;
	}
	
	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed) {
		for (int i= 1; i < sequence.length; i++) {
			int c= scanner.read();
			if (c == ICharacterScanner.EOF && eofAllowed) {
				return true;
			} else if (c != sequence[i]) {
				// Non-matching character detected, rewind the scanner back to the start.
				// Do not unread the first character.
				scanner.unread();
				for (int j= i-1; j > 0; j--)
					scanner.unread();
				return false;
			}
		}

		return true;
	}
	
	protected IToken doEvaluate(ICharacterScanner scanner, boolean resume) {

		if (resume) {

			if (endSequenceDetected(scanner))
				return fToken;

		} else {
			int c= scanner.read();
			if (c == fStartSequence[0]) {
				if (sequenceDetected(scanner, fStartSequence, false)) {
					char c1 = (char)scanner.read();
					if( c1 != ' ' && c1 != '\n'){
						if( c1 == fEndSequence[0]){
							if (sequenceDetected(scanner, fStartSequence, false)) {
								scanner.unread();
					    		return Token.UNDEFINED;
							}
						}
						
						if (endSequenceDetected(scanner))
    						return fToken;   						
					} 
					scanner.unread();
				}
			}
		}

		scanner.unread();
		return Token.UNDEFINED;
	}
}