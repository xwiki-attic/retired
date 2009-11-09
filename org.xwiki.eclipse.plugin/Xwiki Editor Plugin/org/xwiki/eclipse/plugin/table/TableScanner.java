package org.xwiki.eclipse.plugin.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.xwiki.eclipse.plugin.util.ColorProvider;
import org.xwiki.eclipse.plugin.util.XwikiWhitespaceDetector;

/**
 * @author venkatesh
 *
 */

public class TableScanner extends RuleBasedScanner {

	public TableScanner(ColorProvider provider) {

		IToken xwiki_code = new Token(new TextAttribute(provider.getColor(ColorProvider.DEFAULT)));
		List rules= new ArrayList();		
		rules.add(new WhitespaceRule(new XwikiWhitespaceDetector()));
		// Add word rule for keywords, types, and constants.
		WordRule wordRule= new WordRule(new TableWordDetector(), xwiki_code);
		rules.add(wordRule);
		IRule[] result= new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
}