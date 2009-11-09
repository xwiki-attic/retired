package org.xwiki.eclipse.plugin.table;

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