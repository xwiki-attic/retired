package xwiki.scanners;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

import xwiki.editors.ColorManager;
import xwiki.editors.IXWikiColorConstants;
import xwiki.editors.XWikiWhitespaceDetector;

public class XWikiBoxScanner extends RuleBasedScanner {

	public XWikiBoxScanner(ColorManager manager) {
		IToken eText = new Token(new TextAttribute(manager
				.getColor(IXWikiColorConstants.XWIKI_TEXT)));

		IRule[] rules = new IRule[2];

		// Add rule for double quotes
		rules[0] = new SingleLineRule("\"", "\"", eText, '\\');

		// Add generic whitespace rule.
		rules[1] = new WhitespaceRule(new XWikiWhitespaceDetector());

		setRules(rules);
	}

}
