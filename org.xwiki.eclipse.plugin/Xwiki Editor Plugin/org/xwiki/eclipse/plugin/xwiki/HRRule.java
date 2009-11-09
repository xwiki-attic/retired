package org.xwiki.eclipse.plugin.xwiki;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

/**
 * @author venkatesh
 *
 */
public class HRRule extends EndOfLineRule {

	private static Pattern p = Pattern.compile("^----$");

	public HRRule(IToken token)
	{
		super("----",token);
	}

	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed)
	{
		try
		{
			/*
			System.out.print(" Sequence: ");
			System.out.print(sequence);
			System.out.print(" ");
			System.out.print(sequence[0]);*/
			int c = scanner.read();
			//System.out.print(" Here ");
			Matcher m = p.matcher(String.valueOf(c));
			if(!m.find())
			{
				//System.out.println("Here too!");
				scanner.unread();
				return true;
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception in sequenceDetected method of HRRule");
		}
		return super.sequenceDetected(scanner, sequence, eofAllowed);
	}

}