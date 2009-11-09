package org.xwiki.eclipse.ui.editors.scanners.detectors;

import org.eclipse.jface.text.rules.IWordDetector;

public class IdentifierDetector implements IWordDetector {
    
    public boolean isWordStart(char c) {
        return Character.isLetter(c);
    }
    
    public boolean isWordPart(char c) {
        return (Character.isLetterOrDigit(c) || c == '-' ||
                                                 c == '_' || c == '.');
    }
}
