package com.xpn.p2pxwiki.synchronization.diffmerge;

import java.text.BreakIterator;
import java.util.Locale;

import com.xpn.xwiki.doc.XWikiDocument;

public class XMLDiffMerge implements DiffMerge {
	public static final int LEVEL_NO_CHUNK = 0;
	public static final int LEVEL_LINE = 1;
	public static final int LEVEL_SENTENCE = 2;
	public static final int LEVEL_WORD = 3;
	public static final int DEFAULT_CHUNK_LEVEL = LEVEL_SENTENCE;
	protected int level = DEFAULT_CHUNK_LEVEL;

	public XWikiDocument[] mergeBranches(XWikiDocument[] localBranch, XWikiDocument[] remoteBranch) {
		// Locale locale = new Locale(lastLocalDoc.getRealLanguage());
		// TODO: chunkText
		// TODO: call tdm
		// TODO: dechunkText
		// TODO: interleave branches
		// TODO: return result
		return null;
	}

	protected String chunkText(String text, Locale locale) {
		String result = text.replace("&", "&amp;");
		result = result.replace("<", "&lt;").replace(">", "&gt;");
		result = chunkLines(result, locale);
		return "<text>" + result + "</text>";
	}

	protected String chunkLines(String text, Locale locale) {
		if (level < LEVEL_LINE) {
			return text;
		}
		StringBuilder result = new StringBuilder();
		int start = 0;
		int end = text.indexOf("\n");
		if (end == 0) {
			while (end < text.length() && text.charAt(end) == '\n') {
				end++;
			}
			result.append("<para>" + text.substring(start, end) + "</para>");
			start = end;
			end = text.indexOf("\n", start + 1);
		}
		while (end > 0) {
			result.append("<para>" + chunkSentences(text.substring(start, end), locale) + "</para>");
			start = end;
			while (end < text.length() && text.charAt(end) == '\n') {
				end++;
			}
			result.append("<para>" + text.substring(start, end) + "</para>");
			start = end;
			end = text.indexOf("\n", start + 1);
		}
		if (start < text.length()) {
			result.append("<para>" + chunkSentences(text.substring(start), locale) + "</para>");
		}
		return result.toString();
	}

	protected String chunkSentences(String text, Locale locale) {
		if (level < LEVEL_SENTENCE) {
			return text;
		}
		BreakIterator boundary = BreakIterator.getSentenceInstance(locale);
		boundary.setText(text);
		StringBuilder result = new StringBuilder();
		int start = boundary.first();
		int end = boundary.next();
		while (end != BreakIterator.DONE) {
			result.append("<sentence>" + chunkWords(text.substring(start, end), locale) + "</sentence>");
			start = end;
			end = boundary.next();
		}
		return result.toString();
	}

	protected String chunkWords(String text, Locale locale) {
		if (level < LEVEL_WORD) {
			return text;
		}
		BreakIterator boundary = BreakIterator.getLineInstance(locale);
		boundary.setText(text);
		StringBuilder result = new StringBuilder();
		int start = boundary.first();
		int end = boundary.next();
		while (end != BreakIterator.DONE) {
			result.append("<word>" + text.substring(start, end) + "</word>");
			start = end;
			end = boundary.next();
		}
		return result.toString();
	}

	protected String dechunkText(String text, Locale locale) {
		String result = text.replace("<text>", "").replace("</text>", "");
		result = result.replace("<para>", "").replace("</para>", "");
		result = result.replace("<sentence>", "").replace("</sentence>", "");
		result = result.replace("<word>", "").replace("</word>", "");
		result = result.replace("&lt;", "<").replace("&gt;", ">");
		result = result.replace("&amp;", "&");
		return result;
	}

	public static void main(String args[]) {
		//String text = "A   long text.\n\n\nAnother line.\n Some more \ntext. And more.\nLine.\n\n";
		String text = "A  <b> long text.\n\n\nAnother line.\n Some & &amp; more \ntext. And more.\nLine.\n\n";
		XMLDiffMerge m = new XMLDiffMerge();
		m.level = LEVEL_NO_CHUNK;
		//System.out.println(m.chunkText(text, Locale.ENGLISH));
		System.out.println(m.dechunkText(m.chunkText(text, Locale.ENGLISH), Locale.ENGLISH));
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}	
}
