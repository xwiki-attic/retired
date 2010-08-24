
package com.xpn.xwiki.wiked.internal.xwt.cf;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

public class LayoutPropertyReader {

    private static LayoutPropertyReader defaultParser;
    
    private static final String DELIMITER = "|";

    private String delimiter;
    private Map valueMap;

    public static LayoutPropertyReader getDefault() {
        if (defaultParser == null) {
            defaultParser = new LayoutPropertyReader(DELIMITER);
        }
        return defaultParser;
    }
    
    public LayoutPropertyReader(String delimiter) {
        this.delimiter = delimiter;
        this.valueMap = new HashMap();
        this.valueMap.put("FILL", new Integer(GridData.FILL));
        this.valueMap.put("FILL_HORIZONTAL", new Integer(GridData.FILL_HORIZONTAL));
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
