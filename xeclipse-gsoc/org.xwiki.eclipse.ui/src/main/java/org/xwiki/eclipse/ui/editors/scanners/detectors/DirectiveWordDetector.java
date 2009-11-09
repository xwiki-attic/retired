package org.xwiki.eclipse.ui.editors.scanners.detectors;

import org.eclipse.jface.text.rules.IWordDetector;

public class DirectiveWordDetector implements IWordDetector {

    public boolean isWordStart(char c) {
        return c == '#';
    }
    
    public boolean isWordPart(char c) {
        return Character.isLetterOrDigit(c) || c == '-' ||
                                                    c == '_';
    }
}
