package org.xwiki.eclipse.ui.editors.scanners.rules;

import java.util.Iterator;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.xwiki.eclipse.ui.editors.scanners.XwikiDefaultCodeScanner;

public class XwikiSyntaxRule extends WordRule{
	
	private StringBuffer fBuffer= new StringBuffer();

	private boolean fIgnoreCase= false;

	public XwikiSyntaxRule(IWordDetector detector, IToken defaultToken) {
		super(detector, defaultToken);
	}

	public IToken evaluate(ICharacterScanner scanner) {
		XwikiDefaultCodeScanner xwikiScanner = (XwikiDefaultCodeScanner)scanner;
		int c= scanner.read();
		if (c != ICharacterScanner.EOF && fDetector.isWordStart((char) c)) {
			if (xwikiScanner.isFistCharOfLine()) {


				fBuffer.setLength(0);
				do {
					fBuffer.append((char) c);
					c= scanner.read();
				} while (c != ICharacterScanner.EOF && fDetector.isWordPart((char) c));
				scanner.unread();

				String buffer= fBuffer.toString();
				IToken token= (IToken)fWords.get(buffer);
				
				if(fIgnoreCase) {
					Iterator iter= fWords.keySet().iterator();
					while (iter.hasNext()) {
						String key= (String)iter.next();
						if(buffer.equalsIgnoreCase(key)) {
							token= (IToken)fWords.get(key);
							break;
						}
					}
				} else
					token= (IToken)fWords.get(buffer);
				
				if (token != null){
					return token;
				}
					unreadBuffer(scanner);
					return Token.UNDEFINED;
			}
		}

		scanner.unread();
		return Token.UNDEFINED;
	}

	protected void unreadBuffer(ICharacterScanner scanner) {
		for (int i= fBuffer.length() - 1; i >= 0; i--)
			scanner.unread();
	}		
}