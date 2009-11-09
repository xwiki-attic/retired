package xwiki.scanners;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;

import xwiki.editors.ColorManager;
import xwiki.editors.XWikiWhitespaceDetector;

public class XWikiScanner extends RuleBasedScanner {

	public XWikiScanner(ColorManager manager) {

		IRule[] rules = new IRule[1];
		// Add generic whitespace rule.
		rules[0] = new WhitespaceRule(new XWikiWhitespaceDetector());

		setRules(rules);
	}
}
