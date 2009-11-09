package org.xwiki.eclipse.plugin.pre;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * @author venkatesh
 *
 */

public class PreWordDetector implements IWordDetector {
	public boolean isWordPart(char character) {
		return Character.isDefined(character);
	}
	public boolean isWordStart(char character) {
		return Character.isDefined(character);
	}
}
