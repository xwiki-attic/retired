package org.xwiki.eclipse.plugin.code;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * @author venkatesh
 *
 */

public class CodeWordDetector implements IWordDetector {
	public boolean isWordPart(char character) {
		return Character.isDefined(character);
	}
	public boolean isWordStart(char character) {
		return Character.isDefined(character);
	}
}
