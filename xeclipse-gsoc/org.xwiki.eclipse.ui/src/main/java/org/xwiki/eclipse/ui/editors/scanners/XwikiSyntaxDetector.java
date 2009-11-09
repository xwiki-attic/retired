package org.xwiki.eclipse.ui.editors.scanners;

import org.eclipse.jface.text.rules.IWordDetector;

public class XwikiSyntaxDetector implements IWordDetector {
	public boolean isWordPart(char c) {
		if(c == '1' || c == '.' || c == ' ' || c == 't' || c == 'a' || c == 'b' || c == 'l'  || c == 'e' || c == '}'){
			return true;
		}
		return false;
	}

	public boolean isWordStart(char c) {
		if(c == '1' || c == 'a' || c == 'A' || c == 'i' || c == 'g' || c == 'I' || c == 'h' || c == 'k' || c == '{'){
			return true;
		}
		return false;
	}
}