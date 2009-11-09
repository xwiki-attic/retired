
package com.xpn.xwiki.wiked.internal.ui.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class WikiMacroScanner extends RuleBasedScanner {

    private static ITokenScanner defaultScanner;

    private static Color RED = new Color(Display.getCurrent(), new RGB(255,0,0));
    private static Color LGREY = new Color(Display.getCurrent(), new RGB(0,128,0));
    private static Color BLUE = new Color(Display.getCurrent(), new RGB(0,0,255));
    
    public static ITokenScanner getDefaultScanner() {
        if (defaultScanner == null) {
        	defaultScanner = new WikiMacroScanner();
        }
        return defaultScanner;
    }
        
    private WikiMacroScanner() {
		IToken macro = new Token(new TextAttribute(BLUE));
		IToken link = new Token(new TextAttribute(LGREY));
		IToken header = new Token(new TextAttribute(RED));
		IRule[] rules = new IRule[3];
		// Add rule for macros
		int i = 0;
		rules[i++] = new SingleLineRule("{", "}", macro, '\\');
		rules[i++] = new SingleLineRule("[", "]", link, '\\');
		WordRule headingRule = new WordRule(new IWordDetector() {
			public boolean isWordStart(char c) {
				return c == 'h';
			}

			public boolean isWordPart(char c) {
				return Character.isDigit(c) || c == '.';
			}
		});
		headingRule.setColumnConstraint(0);
		for(int h = 1; h < 7;++h){
			headingRule.addWord(Integer.toString(h)+".",header);
		}
		rules[i++] = headingRule; 
		setRules(rules);
	}

}
