package org.xwiki.eclipse.ui.editors.util;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;

/**
 * @author venkatesh
 * 
 */
public class Hyperlink implements IHyperlink {

	private String text;
	private IRegion region;

	public Hyperlink(IRegion region, String text) {
		this.region = region;
		this.text = text;
	}

	public IRegion getHyperlinkRegion() {
		return region;
	}

	public void open() {
		/*
		 * if(location!=null) { int offset=region.getOffset(); TextEditor
		 * editor=viewer. editor.selectAndReveal(offset,0); editor.setFocus(); }
		 */
		if (text != null) {
			String platform = SWT.getPlatform();
			// System.out.println("Text: "+text);
			if ("motif".equals(platform) || "gtk".equals(platform)) { //$NON-NLS-1$ //$NON-NLS-2$
				Program program = Program.findProgram("html"); //$NON-NLS-1$
				if (program == null)
					program = Program.findProgram("htm"); //$NON-NLS-1$
				if (program != null)
					program.execute(text);
			} else
				Program.launch(text);
			text = null;
			return;
		}

	}

	public String getTypeLabel() {
		return null;
	}

	public String getHyperlinkText() {
		return null;
	}
}