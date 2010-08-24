
package com.xpn.xwiki.wiked.internal.wikip;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Wiki parsed document
 * @author psenicka_ja
 */
public class WikiDocument implements IWikiStructureObject {

    private Pattern pattern;
    private List children;
    
    private static final String TITLE_RE = "^[\\p{Space}]*1(\\.1)*[\\p{Space}]";
    
    public WikiDocument() {
        this.pattern = Pattern.compile(TITLE_RE); 
    	this.children = new ArrayList();
    }
    
    public IWikiStructureObject[] getChildren() {
    	return (IWikiStructureObject[])this.children.toArray(
            new IWikiStructureObject[this.children.size()]);
    }
    
    public void addTitle(WikiTitle title) {
    	this.children.add(title);
    }

    /**
     * @param line
     * @param heading
     */
    public void addTitle(String title, int line) {
        Matcher matcher = pattern.matcher(title); 
        if (matcher.find()) { 
            String number = matcher.group(0); 
            StringTokenizer tokens = new StringTokenizer(number, ".");
            int level = tokens.countTokens() - 1;
            IWikiStructureObject object = find(level-1);
            if (object != null) {
                int index = object.getChildren().length+1;
                String text = title.substring(number.length());
                WikiTitle wikititle = new WikiTitle(object, text, index);
                wikititle.setLevel(level);
                wikititle.setLine(line);
            	object.addChild(wikititle);
                return;
            }
        }     
        
        throw new IllegalArgumentException("unknown title "+title);
    }

	/**
	 * @see com.xpn.xwiki.wiked.internal.wikip.IWikiStructureObject#getTitle()
	 */
	public String getTitle() {
		return "ROOT";
	}

	/**
	 * @see com.xpn.xwiki.wiked.internal.wikip.IWikiStructureObject#getParent()
	 */
	public IWikiStructureObject getParent() {
		return null;
	}

	/**
	 * @see com.xpn.xwiki.wiked.internal.wikip.IWikiStructureObject#addChild(com.xpn.xwiki.wiked.internal.wikip.IWikiStructureObject)
	 */
	public void addChild(IWikiStructureObject child) {
		if (child instanceof WikiTitle) {
			addTitle((WikiTitle)child);
        } else {
        	throw new IllegalArgumentException("unknown child "+child);
        }
	}

    /**
     * @param level
     * @return
     */
    private IWikiStructureObject find(int level) {
    	IWikiStructureObject object = this;
        for (int currentlevel = 0; currentlevel <= level; currentlevel++) {
        	IWikiStructureObject[] children = object.getChildren();
            if (getLevel(children) - currentlevel > 0) {
            	break;
            } else if (children.length > 0) {
            	object = children[children.length-1];
            }
        }
        
        return object;
    }

	private int getLevel(IWikiStructureObject[] children) {
		for (int i = 0; i < children.length; i++) {
			IWikiStructureObject object = children[i];
			if (object instanceof WikiTitle) {
				return ((WikiTitle)object).getLevel();
            }
		}
        return 0;
	}

}

