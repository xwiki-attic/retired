package org.xwiki.eclipse.plugin.xwiki;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.xwiki.eclipse.plugin.util.ColorProvider;
import org.xwiki.eclipse.plugin.util.XwikiWhitespaceDetector;

/**
 * @author venkatesh
 *
 */

public class XwikiCodeScanner extends RuleBasedScanner {

	public XwikiCodeScanner(ColorProvider provider, Display display) {


		IToken string= new Token(new TextAttribute(provider.getColor(ColorProvider.STRING)));
		//IToken comment= new Token(new TextAttribute(provider.getColor(ColorProvider.SINGLE_LINE_COMMENT)));
		IToken other= new Token(new TextAttribute(provider.getColor(ColorProvider.DEFAULT)));

		IToken xwiki_bold = new Token(new TextAttribute(provider.getColor(ColorProvider.DEFAULT),null,SWT.BOLD));
		IToken xwiki_italicized = new Token(new TextAttribute(provider.getColor(ColorProvider.DEFAULT),null,SWT.ITALIC));
		IToken xwiki_underlined = new Token(new TextAttribute(provider.getColor(ColorProvider.DEFAULT),null,TextAttribute.UNDERLINE));
		IToken xwiki_list= new Token(new TextAttribute(provider.getColor(ColorProvider.XWIKI_LIST)));
		IToken xwiki_strike = new Token(new TextAttribute(provider.getColor(ColorProvider.DEFAULT),null,TextAttribute.STRIKETHROUGH));
		IToken xwiki_info= new Token(new TextAttribute(provider.getColor(ColorProvider.XWIKI_INFO)));
		IToken xwiki_warning= new Token(new TextAttribute(provider.getColor(ColorProvider.XWIKI_WARNING)));
		IToken xwiki_error= new Token(new TextAttribute(provider.getColor(ColorProvider.XWIKI_ERROR)));
		//Separate word detectors in each of their own packages, following does not, actually have any effect.
		IToken xwiki_pre = new Token(new TextAttribute(provider.getColor(ColorProvider.XWIKI_PRECODE),null,SWT.NORMAL,new Font(display,"Monospace",8,SWT.NORMAL)));
		IToken xwiki_code = new Token(new TextAttribute(provider.getColor(ColorProvider.XWIKI_PRECODE),null,SWT.NORMAL,new Font(display,"Monospace",8,SWT.NORMAL)));
		IToken xwiki_link = new Token(new TextAttribute(provider.getColor(ColorProvider.XWIKI_LINK), provider.getColor(ColorProvider.XWIKI_LINK_BACK),TextAttribute.UNDERLINE));
		IToken xwiki_image = new Token(new TextAttribute(provider.getColor(ColorProvider.XWIKI_IMAGE),null,SWT.NORMAL));

		//IToken xwiki_hr = new Token(new TextAttribute(provider.getColor(ColorProvider.XWIKI_HR)));

		List rules= new ArrayList();

		// Add rule for single line comments.
		//rules.add(new EndOfLineRule("//", comment)); //$NON-NLS-1$

		// Add rule for strings and character constants.
		rules.add(new SingleLineRule("\"", "\"", string, '\\')); //$NON-NLS-2$ //$NON-NLS-1$
		rules.add(new SingleLineRule("'", "'", string, '\\')); //$NON-NLS-2$ //$NON-NLS-1$

		// Rule for HR .. regex.
		//rules.add(new HRRule(xwiki_hr)); //$NON-NLS-1$ //$NON-NLS-2$

		// Rule for List.
		rules.add(new EndOfLineRule("* ",xwiki_list)); //$NON-NLS-1$
		// Rule for xwiki_bold,xwiki_italicized,xwiki_underlined
		rules.add(new PatternRule("*"," *",other,'\\',false)); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new PatternRule("*","*",xwiki_bold,'\\',true));  //$NON-NLS-1$//$NON-NLS-2$
		//its coming for single end itself.. why??
		rules.add(new SingleLineRule("~~","~~",xwiki_italicized,'\\'));  //$NON-NLS-1$//$NON-NLS-2$
		//here again, same problem.
		rules.add(new SingleLineRule("__","__",xwiki_underlined,'\\'));  //$NON-NLS-1$//$NON-NLS-2$
		//here again, same problem.
		rules.add(new SingleLineRule("--","--",xwiki_strike,'\\'));  //$NON-NLS-1$//$NON-NLS-2$
		rules.add(new SingleLineRule("#info(\"","\")",xwiki_info,'\\'));  //$NON-NLS-1$//$NON-NLS-2$
		rules.add(new SingleLineRule("#warning(\"","\")",xwiki_warning,'\\'));  //$NON-NLS-1$//$NON-NLS-2$
		rules.add(new SingleLineRule("#error(\"","\")",xwiki_error,'\\'));  //$NON-NLS-1$//$NON-NLS-2$
		rules.add(new SingleLineRule("{pre}","{/pre}",xwiki_pre,'\\'));  //$NON-NLS-1$//$NON-NLS-2$
		rules.add(new SingleLineRule("{code}","{code}",xwiki_code,'\\'));  //$NON-NLS-1$//$NON-NLS-2$
		rules.add(new PatternRule("[","]",xwiki_link,'\\',false)); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new PatternRule("{image:","}",xwiki_image,'\\',false)); //$NON-NLS-1$ //$NON-NLS-2$

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new XwikiWhitespaceDetector()));

		IRule[] result= new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
}