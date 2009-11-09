
package com.xpn.xwiki.wiked.internal.wikip;

import java.util.ArrayList;
import java.util.List;

/**
 * Wiki header (title) object
 * @author psenicka_ja
 */
public class WikiTitle implements IWikiStructureObject, INavigable {

	private IWikiStructureObject parent;

    /** The title */
	private String title;
    /** The level of title, 0 = root */
    private int level;
    /** The index at the same level */
    private int index;
    /** The line number */
    private int line;

    private List subtitles;
    
	public WikiTitle(IWikiStructureObject parent, int index) {
        this(parent, "", index);
	}

	public WikiTitle(IWikiStructureObject parent, String title, int index) {
        this.parent = parent;
        this.title = title;
        this.index = index;
        this.subtitles = new ArrayList();
	}

	public int getLevel() {
		return this.level;
	}

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTitle() {
		return this.title;
	}

    public void setTitle(String text) {
        this.title = text;
    }

    public String getValue() {
        if (this.parent instanceof WikiTitle) {
            WikiTitle parentTitle = (WikiTitle)this.parent;
            int parentLevel = parentTitle.getLevel();
        	StringBuffer buffer = new StringBuffer(parentTitle.getValue());
            for (int i=parentLevel+1; i<this.level; i++) {
            	buffer.append(".1");
            }
            buffer.append(".").append(String.valueOf(this.index));
            return buffer.toString();
        } 
        
        return String.valueOf(this.index);
	}

    public int getLineNumber() {
    	return line;
    }
    
    public void setLine(int line) {
    	this.line = line;
    }

    private WikiTitle[] getSubTitles() {
        return (WikiTitle[])this.subtitles.toArray(
            new WikiTitle[this.subtitles.size()]);
    }

    public void addSubTitle(WikiTitle title) {
        this.subtitles.add(title);
    }

    /**
	 * @see com.xpn.xwiki.wiked.internal.wikip.IWikiStructureObject#getChildren()
	 */
	public IWikiStructureObject[] getChildren() {
		return getSubTitles();
	}

	/**
	 * @see com.xpn.xwiki.wiked.internal.wikip.IWikiStructureObject#getParent()
	 */
	public IWikiStructureObject getParent() {
		return this.parent;
	}

	/**
	 * @see com.xpn.xwiki.wiked.internal.wikip.IWikiStructureObject#addChild(com.xpn.xwiki.wiked.internal.wikip.IWikiStructureObject)
	 */
	public void addChild(IWikiStructureObject child) {
        if (child instanceof WikiTitle) {
            addSubTitle((WikiTitle)child);
        } else {
        	throw new IllegalArgumentException("unknown child "+child);
        }
	}

    public String toString() {
    	return this.getValue()+" "+this.getTitle();
    }
    
}
