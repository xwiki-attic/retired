
package com.xpn.xwiki.wiked.internal.wikip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * Parser of wiki syntax
 * @author psenicka_ja
 */
public class WikiParser {

    private Pattern pattern;
    
    private static final String HEADING_RE = "^[\\p{Space}]*1(\\.1)*[\\p{Space}]+.*?$";

    public WikiParser() {
    	this.pattern = Pattern.compile(HEADING_RE, Pattern.MULTILINE); 
    }
    
    /**
     * Parse given file.
     * @param file the file to parse
     * @return parsed document
     * @throws IOException for general IO errors
     */
    public WikiDocument parse(IFile file) throws IOException {
        if (file == null) {
        	throw new IllegalArgumentException("no file");
        }
        WikiDocument document = new WikiDocument();
        try {
            InputStream is = file.getContents();
        	read(document, new BufferedReader(new InputStreamReader(is)));
        } catch (CoreException ex) {
        	IOException ioex = new IOException();
            ioex.initCause(ex);
            throw ioex;
        }

        return document;
    }

    /**
     * Parse given text.
     * @param wikiText the text to parse
     * @return parsed document
     * @throws IOException for general IO errors
     */
    public WikiDocument parse(String wikiText) throws IOException {
		WikiDocument document = new WikiDocument();
        read(document, new BufferedReader(new StringReader(wikiText)));
        return document;
    }

	private void read(WikiDocument document, BufferedReader reader) throws IOException {
		String line;
		for (int i = 1; (line = reader.readLine()) != null; i++) {
            Matcher matcher = pattern.matcher(line); 
            while (matcher.find()) { 
                String heading = matcher.group(0); 
                document.addTitle(heading, i); 
            }     
        }
	}

        
}
