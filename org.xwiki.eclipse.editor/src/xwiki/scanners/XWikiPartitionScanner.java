package xwiki.scanners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;



public class XWikiPartitionScanner extends RuleBasedPartitionScanner {
	
	public final static String XWIKI_TEXT_ENHANCE = "xwiki.text.enchance";
	public final static String XWIKI_TITLE = "xwiki_title";
	public final static String XWIKI_BOXES = "xwiki_boxes";

	public XWikiPartitionScanner() {

		IToken xwikiTextEnhance = new Token(XWIKI_TEXT_ENHANCE);
		IToken xwikiTitle = new Token(XWIKI_TITLE);
		IToken xwikiBoxes = new Token(XWIKI_BOXES);
		
		List<IPredicateRule>  list= new ArrayList<IPredicateRule>();
 

		list.add(new EndOfLineRule("1.1.1.1.1.1 ", xwikiTitle));
		list.add( new EndOfLineRule("1.1.1.1.1 ", xwikiTitle));
		list.add( new EndOfLineRule("1.1.1.1 ", xwikiTitle));
		list.add( new EndOfLineRule("1.1.1 ", xwikiTitle));
		list.add( new EndOfLineRule("1.1 ", xwikiTitle));
		list.add( new EndOfLineRule("1 ", xwikiTitle));
		list.add( new EndOfLineRule("*** ", xwikiTitle));
		list.add( new EndOfLineRule("** ", xwikiTitle));
		list.add( new EndOfLineRule("* ", xwikiTitle));
		
		list.add( new MultiLineRule("*", "*", xwikiTextEnhance));
		list.add( new MultiLineRule("~~", "~~", xwikiTextEnhance));
		list.add( new MultiLineRule("__", "__", xwikiTextEnhance));
		list.add( new MultiLineRule("<sub>", "</sub>",xwikiTextEnhance));
		list.add( new MultiLineRule("--", "--", xwikiTextEnhance));
		list.add( new MultiLineRule("<tt>", "</tt>",xwikiTextEnhance));
		list.add( new MultiLineRule("<sup>", "</sup>",xwikiTextEnhance));
		
		list.add(new SingleLineRule("#info(",")",xwikiBoxes));
		
		IPredicateRule[] rules = new IPredicateRule[list.size()];
		rules=list.toArray(rules);

		setPredicateRules(rules);
	}
	
	
}
