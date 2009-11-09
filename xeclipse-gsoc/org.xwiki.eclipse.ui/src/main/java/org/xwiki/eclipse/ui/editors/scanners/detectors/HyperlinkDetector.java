package org.xwiki.eclipse.ui.editors.scanners.detectors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.URLHyperlink;

/**
 * @author venkatesh
 * 
 */
public class HyperlinkDetector implements IHyperlinkDetector {

	public HyperlinkDetector() {
		super();
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		// TODO Within file hyperlinks
		// TODO Error, when there are multiple quotes in one single line.
		// TODO Error, activates for the whole line.
		IRegion lineInfo;
		String line;
		try {
			lineInfo = textViewer.getDocument().getLineInformationOfOffset(
					region.getOffset());
			line = textViewer.getDocument().get(lineInfo.getOffset(),
					lineInfo.getLength());
		} catch (BadLocationException ex) {
			return null;
		}
		int begin = line.indexOf("{quote:");
		int end = line.indexOf("}", begin);
		if ((end < 0) || (begin < 0) || (end == begin + 1))
			return null;
		String text = line.substring(begin + 7, end);
		region = new Region(lineInfo.getOffset() + begin + 7, text.length());
		// return new IHyperlink[] {new Hyperlink(Region,text)};
		// System.out.print(text+" ");
		// System.out.println(region.getLength()+" "+region.getOffset());
		return new IHyperlink[] { new URLHyperlink(region, text) };
	}
}