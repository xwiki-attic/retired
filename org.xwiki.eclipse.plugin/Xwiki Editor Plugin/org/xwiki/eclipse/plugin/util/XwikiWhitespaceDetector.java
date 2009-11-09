package org.xwiki.eclipse.plugin.util;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

/**
 * @author venkatesh
 *
 */

public class XwikiWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char character) {
		return Character.isWhitespace(character);
	}
}