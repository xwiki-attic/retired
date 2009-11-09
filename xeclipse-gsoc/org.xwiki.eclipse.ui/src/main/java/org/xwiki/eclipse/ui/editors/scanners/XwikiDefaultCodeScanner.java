package org.xwiki.eclipse.ui.editors.scanners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordPatternRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.xwiki.eclipse.ui.editors.parser.model.DirectiveNode;
import org.xwiki.eclipse.ui.editors.scanners.detectors.DirectiveWordDetector;
import org.xwiki.eclipse.ui.editors.scanners.detectors.IdentifierDetector;
import org.xwiki.eclipse.ui.editors.scanners.detectors.WhitespaceDetector;
import org.xwiki.eclipse.ui.editors.scanners.rules.XwikiSyntaxRule;
import org.xwiki.eclipse.ui.editors.util.ColorProvider;

public class XwikiDefaultCodeScanner extends RuleBasedScanner
{
	public static final String partitionType = null;
	
	private static final class SyntaxDetector implements IWordDetector {
		public boolean isWordPart(char c) {
			if(c == '1' || c == '.' || c == ' ' || c == 't' || c == 'a' || c == 'b' || c == 'l'  || c == 'e' || c == '}'){
				return true;
			}
			return false;
		}

		public boolean isWordStart(char c) {
			if(c == '1' || c == 'a' || c == 'A' || c == 'i' || c == 'g' || c == 'I' || c == 'h' || c == 'k' || c == '{'){
				return true;
			}
			return false;
		}
	}
	
	private static final class ListDetector implements IWordDetector {
		public boolean isWordPart(char c) {
			if(c == '*' || c == ' '){
				return true;
			}
			return false;
		}

		public boolean isWordStart(char c) {
			if(c == '*'){
				return true;
			}
			return false;
		}
	}
	
	public boolean isFistCharOfLine(){
		boolean firstChar = true;
		try {
				IRegion region =  fDocument.getLineInformationOfOffset(fOffset);
				int length = region.getLength();
				int offset = region.getOffset();
				
			    String line = fDocument.get(offset, length);

			    int loop = (fOffset-2)-offset;
			    if(loop == -1) return firstChar;
			    while(loop != -1 ) {
			    	if(!Character.isWhitespace(line.charAt(loop))){
			    		firstChar = false;
			    		break;
			    	}
			    	loop--;
			    }
			    
				return firstChar;
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return firstChar;
	}
			
    public XwikiDefaultCodeScanner(ColorProvider provider)
    {
        List<IRule> rules = new ArrayList<IRule>();

        rules.add(new WhitespaceRule(new WhitespaceDetector()));
        Token defaultToken = new Token(new TextAttribute(provider.getColor(ColorProvider.DEFAULT)));
        Token syntaxToken = new Token(new TextAttribute(provider.getColor(ColorProvider.XWIKI_SYNTAX), null, SWT.BOLD));
        
        //Some XWiki syntax
        WordRule syntaxRule = new XwikiSyntaxRule(new SyntaxDetector(), defaultToken);
        String[] headings =
            {"1 ", "1.1.1.1.1.1 ", "1.1.1.1.1 ", "1.1.1.1 ", "1.1.1 ", "1.1 ", "1. ", "a. ", "A. ", "i. ", "g. ", "I. ","h. ", "k. ", "{table}"};

        for (int i = headings.length - 1; i >= 0; i--) {
        	syntaxRule.addWord(headings[i], syntaxToken);
        }
        rules.add(syntaxRule);
        
        //XWiki Lists       
        WordPatternRule listPatternRule = new WordPatternRule(new ListDetector(), "*", " ", syntaxToken);
        listPatternRule.setColumnConstraint(0);//TODO remove this
        rules.add(listPatternRule); 
  
        // Add word rule for velocity directives
        Token userMacroToken = new Token(new TextAttribute(provider.getColor(ColorProvider.VELO_DIRECTIVE)));
        WordRule wordRule = new WordRule(new DirectiveWordDetector(), userMacroToken);

        Token systemDirectivestoken = new Token(new TextAttribute(provider.getColor(ColorProvider.VELO_DIRECTIVE), null, SWT.BOLD));

        // System directives
        String[] directives =DirectiveNode.DIRECTIVES;
        for (int i = directives.length - 1; i >= 0; i--) {
            wordRule.addWord(directives[i], systemDirectivestoken);
        }
        rules.add(wordRule);
        
        //TODO: add user define macros to the list   

        // Add pattern rule for references
        Token referenceToken = new Token(new TextAttribute(provider.getColor(ColorProvider.VELO_REFERENCE)));
        rules.add(new WordPatternRule(new IdentifierDetector(), "$", null, referenceToken));
        
        IRule[] result = new IRule[rules.size()];
        rules.toArray(result);
        setRules(result);
    }
}
