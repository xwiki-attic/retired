package org.xwiki.eclipse.ui.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;

/**
 * @author venkatesh
 * 
 */
public class DoubleClickStrategy implements ITextDoubleClickStrategy {

	public DoubleClickStrategy() {
		super();
	}

	public void doubleClicked(ITextViewer viewer) {
		int startOffset = viewer.getSelectedRange().x;
		IDocument document = viewer.getDocument();
		IRegion line;
		boolean found = false;
		try {
			line = document.getLineInformationOfOffset(startOffset);
			int line_no = document.getLineOfOffset(startOffset);
			int startPos = startOffset;
			int endPos = startOffset;
			char char_found = '*'; // dummy initialization
			while (startPos >= line.getOffset()) {
				startPos--;
				// TODO implementing only for bold, underline, italics and
				// strikethrough.. see possiblity of other stuff
				// and maybe easier,better implementation of the below code
				char c = document.getChar(startPos);
				if ((c == '*')
						|| (c == '<')
						|| (c == '#')
						|| ((c == '~') && (document.getChar(startPos - 1) == '~'))
						|| ((c == '-') && (document.getChar(startPos - 1) == '-'))
						|| ((c == '_') && (document.getChar(startPos - 1) == '_'))
						|| ((c == ' ')
								&& (document.getChar(startPos - 1) == '.') && ("aAiIghk"
								.indexOf(new Character(document
										.getChar(startPos - 2)).toString()) > -1))) {

					char_found = document.getChar(startPos);
					// for dl tags, click the first, top most to select the
					// corresponding lower tag, for other tags, select anywhere
					// in between the tags.
					// TODO bug, when there is nested dl.
					if (char_found == '<') {
						String starts[] = { "<dl>", "<sup>", "<sub>", "<dt>",
								"<dd>" };
						String ends[] = { "</dl>", "</sup>", "</sub>", "</dt>",
								"</dd>" };
						int found_tag = 0;
						for (; found_tag < starts.length; found_tag++)
							if (document.get(startPos, 6).toLowerCase()
									.indexOf(starts[found_tag]) > -1) // 6
								// because,
								// max
								// length
								// is 6
								// in
								// the
								// array,
								// as
								// of
								// yet
								break;
						int k = 0;
						while (true) {
							if (document
									.get(endPos, starts[found_tag].length())
									.equalsIgnoreCase(starts[found_tag]))
								// System.out.println("updating k.. "+k);
								// Nested tags, keeping count as variable k
								k++;
							if (document.get(endPos, ends[found_tag].length())
									.equalsIgnoreCase(ends[found_tag])) {
								if (k > 0) {
									k--;
									// System.out.println("here "+k);
									endPos++;
									continue;
								}
								viewer.setSelectedRange(startPos, endPos
										- startPos + ends[found_tag].length());
								return;
							}
							endPos++;
						}
					}
					if (char_found == '#') {
						// System.out.println("startPos: "+startPos);
						// System.out.println("endPos: "+endPos);
						while (!((document.getChar(endPos) == '\"') && (document
								.getChar(endPos + 1) == ')')))
							// System.out.println("endPos: "+endPos);
							// System.out.println(document.getChar(endPos) +" "+
							// document.getChar(endPos));
							endPos++;
						endPos++;
						// System.out.println(endPos);
						viewer
								.setSelectedRange(startPos, endPos - startPos
										+ 1);
					}
					if (char_found == ' ') {
						// valid for "a. ", space required.
						char_found = document.getChar(startPos - 2);
						while (true) {
							line_no--;
							if (line_no < 0) {
								startPos = line_no + 1;
								break;
							}
							String text = document.get(document
									.getLineInformation(line_no).getOffset(),
									document.getLineInformation(line_no)
											.getLength());
							// System.out.println("start Line_no: "+line_no+"
							// Text: "+text);
							if (!text.startsWith(char_found + ". ")) {
								startPos = line_no + 1; // now its the line
														// number,
								// not offset
								break;
							}
						}
						// System.out.println("StartPos: "+startPos);
						line_no = document.getLineOfOffset(startOffset);
						boolean end = false;
						while (true) {
							line_no++;
							if (line_no == document.getNumberOfLines()) {
								endPos = line_no - 1;
								end = true;
								break;
							}
							String text = document.get(document
									.getLineInformation(line_no).getOffset(),
									document.getLineInformation(line_no)
											.getLength());
							// System.out.println("Line_no: "+line_no+" Text:
							// "+text);
							if (!text.startsWith(char_found + ". ")) {
								endPos = line_no;
								break;
							}
						}
						// System.out.println("endPos: "+endPos);
						if (!end)
							viewer.setSelectedRange(document
									.getLineOffset(startPos), document
									.getLineOffset(endPos)
									- document.getLineOffset(startPos) - 1);
						if (end)
							// System.out.println(endPos+" "+startPos+" -1 "
							// +document.getLineInformation(document.getNumberOfLines()-1).getLength());
							viewer.setSelectedRange(document
									.getLineOffset(startPos), document
									.getLineOffset(endPos)
									- document.getLineOffset(startPos)
									+ document.getLineInformation(
											document.getNumberOfLines() - 1)
											.getLength());
						return;
					}
					found = true;
					break;
				}
			}
			if (found) {
				found = false;
				while (endPos <= line.getOffset() + line.getLength()) {
					if (char_found == '*')
						if (document.getChar(endPos) == char_found) {
							found = true;
							break;
						}
					if (char_found != '*')
						if ((document.getChar(endPos) == char_found)
								&& (document.getChar(endPos + 1) == char_found)) {
							found = true;
							break;
						}
					endPos++;
				}
			}
			if (found)
				if (startPos == endPos)
					viewer.setSelectedRange(startPos, 0);
				else if (char_found == '*')
					viewer.setSelectedRange(startPos, endPos - startPos + 1); // select
				// including
				// the
				// asterix
				// marks
				else
					viewer
							.setSelectedRange(startPos - 1, endPos - startPos
									+ 3);
		} catch (BadLocationException e) {
			System.out
					.println("Double Click Strategy, beginning/end of document");
		}
	}
}