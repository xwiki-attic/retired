package org.xwiki.eclipse.ui.editors.scanners.detectors;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * @author venkatesh
 * 
 */

public class TableWordDetector implements IWordDetector {
	public boolean isWordPart(char character) {
		return Character.isDefined(character);
	}

	public boolean isWordStart(char character) {
		return Character.isDefined(character);
	}
}