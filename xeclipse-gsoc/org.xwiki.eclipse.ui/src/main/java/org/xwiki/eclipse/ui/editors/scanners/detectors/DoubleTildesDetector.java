package org.xwiki.eclipse.ui.editors.scanners.detectors;

import org.eclipse.jface.text.rules.IWordDetector;

public class DoubleTildesDetector implements IWordDetector{
	
	private boolean flag = false;

	public boolean isWordPart(char arg0) {
		if(arg0 == '~'){
			return true;
		}
		return false;
	}

	public boolean isWordStart(char arg0) {
		if(arg0 == '~'){
			return true;
		}
		return false;
	}

}
