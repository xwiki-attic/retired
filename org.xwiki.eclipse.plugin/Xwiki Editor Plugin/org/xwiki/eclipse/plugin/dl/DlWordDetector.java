package org.xwiki.eclipse.plugin.dl;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * @author venkatesh
 *
 */

public class DlWordDetector implements IWordDetector {
	public boolean isWordPart(char character) {
		return Character.isDefined(character);
	}
	public boolean isWordStart(char character) {
		return Character.isDefined(character);
	}
}
