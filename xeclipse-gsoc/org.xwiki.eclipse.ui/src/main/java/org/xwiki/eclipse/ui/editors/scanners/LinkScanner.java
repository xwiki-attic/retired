package org.xwiki.eclipse.ui.editors.scanners;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.xwiki.eclipse.ui.editors.scanners.detectors.LinkWordDetector;
import org.xwiki.eclipse.ui.editors.util.ColorProvider;

public class LinkScanner extends RuleBasedScanner {

    public LinkScanner(ColorProvider provider)
    {
//        IToken bold = new Token(new TextAttribute(provider.getColor(ColorProvider.XWIKI_HEADING), null, SWT.None));
        IToken defaultToken = new Token(new TextAttribute(provider.getColor(ColorProvider.XWIKI_LINK), null, SWT.None));

//        
        IRule[] rules = new IRule[1];
        rules[0] = new WordRule(new LinkWordDetector(), defaultToken);
//        rules[1] = new WordPatternRule(new DoubleTildesDetector(), "~", "~", defaultToken);
//        rules[2] = new WordPatternRule(new DoubleUnderScoresDetector(), "_", "_", defaultToken);
//        rules[3] = new WordPatternRule(new DoubleDashDetector(), "-", "-", defaultToken);

        setRules(rules);
    }
}
