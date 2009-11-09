package org.xwiki.eclipse.ui.editors.scanners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordPatternRule;
import org.xwiki.eclipse.ui.editors.scanners.detectors.IdentifierDetector;
import org.xwiki.eclipse.ui.editors.util.ColorProvider;

public class VelocityStringScanner extends RuleBasedScanner
{

    public VelocityStringScanner(ColorProvider aProvider)
    {
        List<IRule> rules = new ArrayList<IRule>();

        rules.add(new WhitespaceRule(new WhitespaceDetector()));
        
        // Add pattern rule for references
        Token token = new Token(new TextAttribute(aProvider.getColor(ColorProvider.VELO_INTERPOLATED_REFERENCE)));
        rules.add(new WordPatternRule(new IdentifierDetector(), "$", null, token));

        IRule[] result = new IRule[rules.size()];
        rules.toArray(result);
        setRules(result);

        setDefaultReturnToken(new Token(new TextAttribute(aProvider.getColor(ColorProvider.VELO_STRING))));
    }

    private class WhitespaceDetector implements IWhitespaceDetector
    {
        public boolean isWhitespace(char aChar)
        {
            return (aChar == ' ' || aChar == '\t');
        }
    }
}
