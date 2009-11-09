package org.xwiki.eclipse.ui.editors.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

/**
 * This is used to identify the type of velocity proposal needed to provide depending of the current position of the document 
 * @author malaka
 *
 */
public class VelocityTextGuesser {
	//type of velocity proposals
	public static final int TYPE_INVALID = 0;

	public static final int TYPE_DIRECTIVE = 1;

	public static final int TYPE_VARIABLE = 2;

	public static final int TYPE_API_VARIABLE = 3;

	private int fType;

	private String fText;

	private int fLine;

	private XwkiAPIObject xwkiAPIObject;

	/**
	 * Create an invalid text guesser.
	 */
	public VelocityTextGuesser() {
		fType = TYPE_INVALID;
		fText = "";
		fLine = -1;
	}

	//calculate the the type of proposal 
	public VelocityTextGuesser(IDocument aDocument, int anOffset) {

		boolean dotFound = false;
		int dotOffset = 0;

		try {

			int start = anOffset;
			// Guess start position
			while (start >= 1 && isWordPart(aDocument.getChar(start - 1))) {
				if (aDocument.getChar(start - 1) == '.') {
					dotFound = true;
					dotOffset = start - 1;
				}

				start--;
			}

			int end = anOffset;
			fText = aDocument.get(start, end - start);
			fLine = aDocument.getLineOfOffset(start) + 1;

			// Now guess fType of completion
			if (start >= 1) {

				// Directive or shorthand reference
				char c1 = aDocument.getChar(start - 1);
				if (c1 == '#') {
					fType = TYPE_DIRECTIVE;
				} else if (c1 == '$') {
					fType = TYPE_VARIABLE;
				} else {
					if (start >= 2) {

						// Formal or quiet reference
						char c2 = aDocument.getChar(start - 2);
						if (c2 == '$' && (c1 == '{' || c1 == '!')) {
							fType = TYPE_VARIABLE;
						} else {
							if (start >= 3) {

								// Formal quiet reference
								char c3 = aDocument.getChar(start - 3);
								if (c3 == '$' && c2 == '!' || c1 == '{') {
									fType = TYPE_VARIABLE;
								}
							}
						}
					}
				}
			}

			if (dotFound && fType == TYPE_VARIABLE) {
				String xwikiAPIType = aDocument.get(start, dotOffset - start);
				xwkiAPIObject = XwkiAPIObject.getType(xwikiAPIType);
				fText = aDocument.get(dotOffset + 1, anOffset - dotOffset - 1);
				fType = TYPE_API_VARIABLE;
			}

		} catch (BadLocationException e) {
			fType = TYPE_INVALID;
			fText = "";
			fLine = -1;
		}

	}

	public int getType() {
		return fType;
	}

	public String getText() {
		return fText;
	}

	public int getLine() {
		return fLine;
	}

	public XwkiAPIObject getXwkiAPIObject() {
		return xwkiAPIObject;
	}

	/**
	 * Determines if the specified character may be part of a Velocity
	 * identifier as other than the first character. A character may be part of
	 * a Velocity identifier if and only if it is one of the following: a letter
	 * (a..z, A..Z) a digit (0..9) a hyphen ("-") a connecting punctuation
	 * character ("_")
	 */
	private static final boolean isWordPart(char aChar) {
		return Character.isLetterOrDigit(aChar) || aChar == '-' || aChar == '_'
				|| aChar == '.';
	}

	public String toString() {
		return "type=" + fType + ", text=" + fText + ", line=" + fLine;
	}
}
