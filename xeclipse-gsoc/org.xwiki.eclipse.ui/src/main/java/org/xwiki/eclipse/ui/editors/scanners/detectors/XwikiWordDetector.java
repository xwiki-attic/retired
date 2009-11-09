package org.xwiki.eclipse.ui.editors.scanners.detectors;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * @author venkatesh
 * 
 */

public class XwikiWordDetector implements IWordDetector {

	/*
	 * (non-Javadoc) Method declared on IWordDetector.
	 */
	public boolean isWordPart(char character) {
		return Character.isDefined(character);
	}

	/*
	 * (non-Javadoc) Method declared on IWordDetector.
	 */
	public boolean isWordStart(char character) {
		return Character.isDefined(character);
	}
}