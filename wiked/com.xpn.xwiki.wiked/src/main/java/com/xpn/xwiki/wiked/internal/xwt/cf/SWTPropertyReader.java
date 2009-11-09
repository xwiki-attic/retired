
package com.xpn.xwiki.wiked.internal.xwt.cf;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.w3c.dom.Element;

public class SWTPropertyReader {

    private static SWTPropertyReader defaultParser;
    
    private static final String DELIMITER = "|";
    private static final String ATTRIBUTE = "flags";

    private String attribute;
    private String delimiter;
    private Map valueMap;

    public static SWTPropertyReader getDefault() {
        if (defaultParser == null) {
            defaultParser = new SWTPropertyReader(ATTRIBUTE, DELIMITER);
        }
        return defaultParser;
    }
    
    public SWTPropertyReader(String attribute, String delimiter) {
        this.attribute = attribute;
        this.delimiter = delimiter;
        this.valueMap = new HashMap();
        this.valueMap.put("SWT.NONE", new Integer(SWT.NONE));
        this.valueMap.put("SWT.RESIZE", new Integer(SWT.RESIZE));
        this.valueMap.put("SWT.WRAP", new Integer(SWT.WRAP));
        this.valueMap.put("SWT.READ_ONLY", new Integer(SWT.READ_ONLY));
        this.valueMap.put("SWT.RESIZE", new Integer(SWT.RESIZE));
        this.valueMap.put("SWT.CHECK", new Integer(SWT.CHECK));
        this.valueMap.put("SWT.LEFT", new Integer(SWT.LEFT));
        this.valueMap.put("SWT.BORDER", new Integer(SWT.BORDER));
        this.valueMap.put("SWT.SINGLE", new Integer(SWT.SINGLE));
        this.valueMap.put("SWT.PASSWORD", new Integer(SWT.PASSWORD));
        this.valueMap.put("SWT.HORIZONTAL", new Integer(SWT.HORIZONTAL));
        this.valueMap.put("SWT.SEPARATOR", new Integer(SWT.SEPARATOR));
        this.valueMap.put("true", new Boolean(true));
        this.valueMap.put("false", new Boolean(false));
    }

    public int parse(Element element) {
        String text = element.getAttribute(this.attribute);
        if (text != null && text.length() > 0) {
	        return parse(text);
        }
        return SWT.NONE;
    }

	public int parse(String text) {
        int flags = SWT.NONE;
		StringTokenizer tokens = new StringTokenizer(text, this.delimiter);
		while (tokens.hasMoreTokens()) {
		    String key = tokens.nextToken().trim();
		    Integer value = (Integer)parseKey(key);
		    if (value != null) {
		        flags |= value.intValue();
		    }
		}
		return flags;
	}

	public Object parseKey(String key) {
        return (this.valueMap.containsKey(key)) ? 
            this.valueMap.get(key) : key;
    }

}
