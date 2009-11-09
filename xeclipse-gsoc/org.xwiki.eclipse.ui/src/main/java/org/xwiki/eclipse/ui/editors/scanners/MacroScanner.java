package org.xwiki.eclipse.ui.editors.scanners;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.xwiki.eclipse.ui.editors.scanners.detectors.WhitespaceDetector;
import org.xwiki.eclipse.ui.editors.util.ColorProvider;

public class MacroScanner extends RuleBasedScanner
{
    public MacroScanner(ColorProvider provider){
        IToken eText = new Token(new TextAttribute(provider.getColor(ColorProvider.STRING)));

        IRule[] rules = new IRule[2];

        // Add rule for double quotes
        rules[0] = new SingleLineRule("\"", "\"", eText, '\\');

        // Add generic whitespace rule.
        rules[1] = new WhitespaceRule(new WhitespaceDetector());

        setRules(rules);

    }

}
