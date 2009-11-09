package org.xwiki.eclipse.plugin;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * @author venkatesh
 *
 */

public class XwikiPartitionScanner extends RuleBasedPartitionScanner {

	public static final String XWIKI_CODE = "__xwiki_code";
	public static final String XWIKI_PRE = "__xwiki_pre";
	public static final String XWIKI_TABLE = "__xwiki_table";
	public static final String XWIKI_DL = "__xwiki_dl";
	public final static String[] XWIKI_PARTITION_TYPES= new String[] { XWIKI_CODE, XWIKI_PRE,XWIKI_TABLE,XWIKI_DL };

	public XwikiPartitionScanner() {
		super();
		//IToken xwiki_code= new Token(new TextAttribute(provider.getColor(ColorProvider.XWIKI_PRECODE)));
		IToken xwiki_code = new Token(XWIKI_CODE);
		IToken xwiki_pre = new Token(XWIKI_PRE);
		IToken xwiki_table = new Token(XWIKI_TABLE);
		IToken xwiki_dl = new Token(XWIKI_DL);
		List rules= new ArrayList();
		rules.add(new MultiLineRule("{code}", "{code}", xwiki_code, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("{pre}", "{/pre}", xwiki_pre, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("{table}", "{table}", xwiki_table, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("<dl>", "</dl>", xwiki_dl, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
		//rules.add(new MultiLineRule("/*", "*/", comment, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
		IPredicateRule[] result= new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
	}
}
