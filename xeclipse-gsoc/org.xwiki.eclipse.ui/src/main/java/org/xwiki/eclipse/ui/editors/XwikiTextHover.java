package org.xwiki.eclipse.ui.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Point;

/**
 * @author venkatesh
 * 
 */

public class XwikiTextHover implements ITextHover {

	/**
	 * @deprecated
	 */
	@Deprecated
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		if (hoverRegion != null)
			try {
				if (hoverRegion.getLength() > -1) {
					IDocument document = textViewer.getDocument();
					int line = document
							.getLineOfOffset(hoverRegion.getOffset());
					int offset = document.getLineInformation(line).getOffset();
					// Hover text if for a list item.
					if (("aAiIghk".indexOf(document.getChar(offset)) > -1)
							&& (document.getChar(offset + 1) == '.')
							&& (document.getChar(offset + 2) == ' '))
						return "List Item: "
								+ textViewer.getDocument().get(
										hoverRegion.getOffset(),
										hoverRegion.getLength());
					offset = hoverRegion.getOffset();
					int length = hoverRegion.getLength();
					// Hover text if macro has been selected
					if (document.getChar(offset) == '#') {
						String text = textViewer.getDocument().get(offset,
								length);
						int start = textViewer.getDocument()
								.get(offset, length).indexOf("(\"");
						int stop = textViewer.getDocument().get(offset, length)
								.indexOf("\")");
						return textViewer.getDocument().get(offset + 1,
								start - 1).toUpperCase()
								+ ": " + text.substring(start + 2, stop);
					}
					return textViewer.getDocument().get(offset, length);
				}
			} catch (BadLocationException x) {
			}
		// resource bundle.. how to associate the bundle with the properties
		// file??
		return XwikiEditorMessages.getString("XwikiTextHover.emptySelection"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc) Method declared on ITextHover
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		Point selection = textViewer.getSelectedRange();
		if ((selection.x <= offset) && (offset < selection.x + selection.y))
			return new Region(selection.x, selection.y);
		return new Region(offset, 0);
	}
}