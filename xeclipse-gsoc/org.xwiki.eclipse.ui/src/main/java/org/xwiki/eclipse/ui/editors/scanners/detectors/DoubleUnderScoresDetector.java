package org.xwiki.eclipse.ui.editors.scanners.detectors;

import org.eclipse.jface.text.rules.IWordDetector;

public class DoubleUnderScoresDetector implements IWordDetector{

	public boolean isWordPart(char arg0) {
		if(arg0 == '_'){
			return true;
		}
		return false;
	}

	public boolean isWordStart(char arg0) {
		if(arg0 == '_'){
			return true;
		}
		return false;
	}

}
