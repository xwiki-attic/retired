package org.xwiki.eclipse.ui.actions;

import java.io.FileOutputStream;
import java.io.Reader;
import java.io.StringReader;

import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.wikimodel.wem.IWemListener;
import org.wikimodel.wem.IWikiParser;
import org.wikimodel.wem.IWikiPrinter;
import org.wikimodel.wem.PrintTextListener;
import org.wikimodel.wem.jspwiki.JspWikiParser;
import org.wikimodel.wem.jspwiki.JspWikiSerializer;
import org.wikimodel.wem.xwiki.XWikiParser;
import org.wikimodel.wem.xwiki.XWikiSerializer;
import org.xwiki.eclipse.ui.editors.XwikiEditorMessages;

/**
 * 
 * @author venkatesh
 * 
 */

public class ConvertAction extends TextEditorAction {

	// To Add more syntaxes conversions, add SYNTAX to the array below, add
	// corresponding entries to returnListener and returnParser
	private String[] SYNTAXES = { "XWIKI", "RAW", "JSP" };
	private String[] SYNTAX_PRETTY = { "XWiki Syntax", "Raw Text",
			"JspWiki Syntax" };
	private String from;
	private String to;

	public ConvertAction(String from, String to) {
		super(XwikiEditorMessages.getResourceBundle(), null, null);
		this.from = from;
		this.to = to;
		setEnabled(true);
		setText(SYNTAX_PRETTY[totype(to)]);
		setToolTipText("Convert From " + SYNTAX_PRETTY[totype(from)] + " to "
				+ SYNTAX_PRETTY[totype(to)]);
	}

	/*
	 * (non-Javadoc)action Method declared on IAction
	 */
	@Override
	public void run() {
		try {
			if (getTextEditor() == null)
				return;
			FileDialog dialog = new FileDialog(getTextEditor().getSite()
					.getWorkbenchWindow().getShell());
			dialog.setOverwrite(true);
			String path = dialog.open();
			if (path == null)
				return;
			if (path.length()!=0) {
				final FileOutputStream output = new FileOutputStream(path, true);
				IDocument document = getTextEditor().getDocumentProvider()
						.getDocument(getTextEditor().getEditorInput());
				Reader reader = new StringReader(document.get());
				IWikiParser parser = returnParser(from);
				IWikiPrinter printer = new IWikiPrinter() {
					public void print(String str) {
						try {
							output.write(str.getBytes());
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					public void println(String str) {
						try {
							output.write(str.getBytes());
							output.write("\n".getBytes());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				IWemListener serializer = returnListener(to, printer);
				parser.parse(reader, serializer);
				output.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected IWemListener returnListener(int type, IWikiPrinter printer) {
		if (type == 0)
			return new XWikiSerializer(printer);
		if (type == 1)
			return new PrintTextListener(printer);
		// JspWikiSerializer(IWikiPrinter) that I use here is modified,
		// modifications done to pass the printer to the serializer.
		if (type == 2)
			return new JspWikiSerializer(printer);
		return null;
	}

	protected IWikiParser returnParser(int type) {
		if (type == 0)
			return new XWikiParser();
		if (type == 1)
			return null;
		if (type == 2)
			return new JspWikiParser();
		return null;
	}

	protected IWikiParser returnParser(String syntax) {
		return returnParser(totype(syntax));
	}

	protected IWemListener returnListener(String syntax, IWikiPrinter printer) {
		return returnListener(totype(syntax), printer);
	}

	protected int totype(String syntax) {
		for (int i = 0; i < SYNTAXES.length; i++)
			if (SYNTAXES[i].toLowerCase().equals(syntax.toLowerCase()))
				return i;
		return 0;
	}
}