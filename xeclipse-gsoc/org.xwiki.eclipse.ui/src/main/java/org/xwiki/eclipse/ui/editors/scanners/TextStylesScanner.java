package org.xwiki.eclipse.ui.editors.scanners;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordPatternRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.xwiki.eclipse.ui.editors.scanners.detectors.DoubleDashDetector;
import org.xwiki.eclipse.ui.editors.scanners.detectors.DoubleTildesDetector;
import org.xwiki.eclipse.ui.editors.scanners.detectors.DoubleUnderScoresDetector;
import org.xwiki.eclipse.ui.editors.scanners.detectors.StarDetector;
import org.xwiki.eclipse.ui.editors.util.ColorProvider;

public class TextStylesScanner extends RuleBasedScanner {

    public TextStylesScanner(ColorProvider provider)
    {
//        IToken bold = new Token(new TextAttribute(provider.getColor(ColorProvider.XWIKI_HEADING), null, SWT.None));
        IToken defaultToken = new Token(new TextAttribute(provider.getColor(ColorProvider.XWIKI_SYNTAX), null, SWT.BOLD));
//        
        IRule[] rules = new IRule[4];
        rules[0] = new WordRule(new StarDetector(), defaultToken);
//        rules[0] = new WordPatternRule(new StarDetector(),"*", "*", defaultToken);
        rules[1] = new WordPatternRule(new DoubleTildesDetector(), "~", "~", defaultToken);
        rules[2] = new WordPatternRule(new DoubleUnderScoresDetector(), "_", "_", defaultToken);
        rules[3] = new WordPatternRule(new DoubleDashDetector(), "-", "-", defaultToken);

        setRules(rules);
    }
}
