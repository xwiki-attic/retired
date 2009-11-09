package org.xwiki.eclipse.ui.editors.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

/**
 * @author venkatesh
 * 
 */

public class XwikiAutoEditStrategy extends DefaultIndentLineAutoEditStrategy {

	public static String getIndent(IDocument document, int line)
			throws BadLocationException {
		if (line > -1) {
			int start = document.getLineOffset(line);
			int end = start + document.getLineLength(line) - 1;
			int whiteend = EndOfWhiteSpace(document, start, end);
			return document.get(start, whiteend - start);
		}
		return "";
	}

	public static int EndOfWhiteSpace(IDocument document, int offset, int end)
			throws BadLocationException {
		while (offset < end) {
			char c = document.getChar(offset);
			if ((c != ' ') & (c != '\t'))
				return offset;
			offset++;
		}
		return end;
	}

	@Override
	public void customizeDocumentCommand(IDocument document,
			DocumentCommand command) {

		if (command.text.equals(">"))
			try {
				if ((document.getChar(command.offset - 1) == 'l')
						&& (document.getChar(command.offset - 2) == 'd')
						&& (document.getChar(command.offset - 3) == '<')) {
					int line = document.getLineOfOffset(command.offset);
					String indent = getIndent(document, line);
					command.text = ">" + "\n\t" + indent + "\n" + indent
							+ "</dl>";
					configureCommand(command, 3 + indent.length());
				}
				if ((document.getChar(command.offset - 1) == 't')
						&& (document.getChar(command.offset - 2) == 't')
						&& (document.getChar(command.offset - 3) == '<')) {
					command.text = "></tt>";
					configureCommand(command, 1);
				}
				if ((document.getChar(command.offset - 1) == 'p')
						&& (document.getChar(command.offset - 2) == 'u')
						&& (document.getChar(command.offset - 3) == 's')
						&& (document.getChar(command.offset - 4) == '<')) {
					command.text = "></sup>";
					configureCommand(command, 1);
				}
				if ((document.getChar(command.offset - 1) == 'b')
						&& (document.getChar(command.offset - 2) == 'u')
						&& (document.getChar(command.offset - 3) == 's')
						&& (document.getChar(command.offset - 4) == '<')) {
					command.text = "></sub>";
					configureCommand(command, 1);
				}
			} catch (BadLocationException e) {
				// beginning of document.
			}
		// TODO Remove content proposal for bold,underline,italics,strike
		if (command.text.equals("\"")) {
			command.text = "\"\"";
			configureCommand(command, 1);
		}
		// TODO make this better.
		if (command.text.equals("~") || command.text.equals("-")
				|| command.text.equals("_")) {
			char c = command.text.toCharArray()[0];
			try {
				if (document.getChar(command.offset - 1) == c) {
					command.text = new String(new char[] { c, c, c });
					configureCommand(command, 1);
				}
			} catch (BadLocationException e) {
				// beginning of the document
			}
		}
		if (command.text.equals("'")) {
			command.text = "''";
			configureCommand(command, 1);
		}
		if (command.text.equals("*")) {
			command.text = "**";
			configureCommand(command, 1);
		}
		// For enumerated lists, automatic code completion.
		if (command.text.equals("\n"))
			try {
				int line = document.getLineOfOffset(command.offset);
				IRegion region = document.getLineInformation(line);
				int offset = region.getOffset();
				if (region.getLength() > 2) {
					// System.out.println("Region: "+offset+"
					// "+region.getLength());
					Character c = new Character(document.getChar(offset));
					String arr = "aAiIghk*";
					if (arr.indexOf(c.toString()) > -1) {
						// for unordered list auto edit
						if (c.toString().toCharArray()[0] == '*') // TODO
						// better
						// way to do
						// this?
						{
							// starts with '* '
							int i = 1;
							while (document.getChar(offset + i) == '*')
								i++;
							if (document.getChar(offset + i) == ' ') {
								command.text = "\n";
								while (i-- > 0)
									command.text += '*';
								command.text += ' ';
								configureCommand(command, command.text.length());
								return;
							}
						}
						// character starts with one of them.
						if ((document.getChar(offset + 1) == '.')
								&& (document.getChar(offset + 2) == ' ')) {
							command.text = "\n" + c + ". ";
							configureCommand(command, 4);
						}
					}
				}
			} catch (BadLocationException e) {
				System.out.println("Inside customizeDocumentCommand");
			}
		// An enter button pressed is taken as intention to start a new
		// paragraph, otherwise, it shall word wrap.
		// TODO Should this be like this, or should we enter '\\\n' instead of
		// this?
		if (command.text.equals("\n")) {
			command.text = "\n\n";
			configureCommand(command, 2);
		}
	}

	private void configureCommand(DocumentCommand command, int offset_correction) {
		// puts the caret between both the quotes
		command.caretOffset = command.offset + offset_correction;
		command.shiftsCaret = false;
	}
}