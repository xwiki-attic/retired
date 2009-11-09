package xwiki.scanners;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

import xwiki.editors.ColorManager;
import xwiki.editors.IXWikiColorConstants;
import xwiki.editors.XWikiWhitespaceDetector;

public class XWikiTitleScanner extends RuleBasedScanner {

	public XWikiTitleScanner(ColorManager manager) {
		IToken title = new Token(new TextAttribute(manager.getColor(IXWikiColorConstants.XWIKI_TEXT)));

		IRule[] rules = new IRule[2];
	
		rules[0] = new EndOfLineRule(" ", title);

		rules[1] = new WhitespaceRule(new XWikiWhitespaceDetector());

		setRules(rules);
	}
}
