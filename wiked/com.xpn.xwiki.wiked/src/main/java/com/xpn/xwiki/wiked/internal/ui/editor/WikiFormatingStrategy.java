package com.xpn.xwiki.wiked.internal.ui.editor;

import java.util.LinkedList;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;

import com.xpn.xwiki.wiked.internal.WikedPlugin;

public class WikiFormatingStrategy extends ContextBasedFormattingStrategy {

	private final LinkedList documents = new LinkedList();
	private int maxLineLength = 80;

	public void format() {
		super.format();
		final IDocument document = (IDocument) documents.removeFirst();
		if (document != null) {
			int numberOfLines = document.getNumberOfLines();
			for (int lineNumber = 0; lineNumber < numberOfLines; ++lineNumber) {
				try {
					String line = document.get(document
							.getLineOffset(lineNumber), document
							.getLineLength(lineNumber));
					if (line.length() > maxLineLength) {

					}
				} catch (BadLocationException ex) {
					WikedPlugin.logError("Failed to format document", ex);
				}
			}
		}
	}

	public void formatterStarts(IFormattingContext context) {
		super.formatterStarts(context);
		documents.addLast(context.getProperty(
            FormattingContextProperties.CONTEXT_MEDIUM));
	}

	public void formatterStops() {
		documents.clear();
		super.formatterStops();
	}
}