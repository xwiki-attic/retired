package org.xwiki.eclipse.ui.editors.scanners;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.swt.SWT;
import org.xwiki.eclipse.ui.editors.scanners.detectors.WhitespaceDetector;
import org.xwiki.eclipse.ui.editors.util.ColorProvider;

public class ListScanner extends RuleBasedScanner

{
    public ListScanner(ColorProvider provider){
        
        IToken title = new Token(new TextAttribute(provider.getColor(ColorProvider.STRING)));
        IRule[] rules = new IRule[2];
        rules[0] = new EndOfLineRule(" ", title);
        rules[1] = new WhitespaceRule(new WhitespaceDetector());
        setRules(rules);        
    }

}
