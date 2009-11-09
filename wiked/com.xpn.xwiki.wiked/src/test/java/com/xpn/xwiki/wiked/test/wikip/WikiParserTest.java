
package com.xpn.xwiki.wiked.test.wikip;

import junit.framework.TestCase;
import com.xpn.xwiki.wiked.internal.wikip.IWikiStructureObject;
import com.xpn.xwiki.wiked.internal.wikip.WikiDocument;
import com.xpn.xwiki.wiked.internal.wikip.WikiParser;
import com.xpn.xwiki.wiked.internal.wikip.WikiTitle;

/**
 * 
 * @author psenicka_ja
 */
public class WikiParserTest extends TestCase {

    private WikiParser parser;
    
    /**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		this.parser = new WikiParser();
	}
    
	public void testLevel00() throws Exception {
		String content = "1 a\n1 b";
		WikiDocument doc = this.parser.parse(content);
        assertNotNull(doc);
        IWikiStructureObject[] titles = doc.getChildren();
        for (int i = 0; i < titles.length; i++) {
			IWikiStructureObject child = titles[i];
            assertTrue(child instanceof WikiTitle);
            assertEquals(0, ((WikiTitle)child).getLevel());
            assertEquals(String.valueOf(i+1), ((WikiTitle)child).getValue());
            assertEquals(i+1, ((WikiTitle)child).getLineNumber());
            switch (i) {
                case 0: 
                    assertEquals("a", ((WikiTitle)child).getTitle());
                    break;
                case 1: 
                    assertEquals("b", ((WikiTitle)child).getTitle());
                    break;
            } 
		}
    }
        
    public void testLevel01() throws Exception {
        String content = "1 a\n1.1 b";
        WikiDocument doc = this.parser.parse(content);
        assertNotNull(doc);
        IWikiStructureObject[] titles = doc.getChildren();
        assertEquals(1, titles.length);
        assertNotNull(titles[0]);
        assertTrue(titles[0] instanceof WikiTitle);
        assertEquals(0, ((WikiTitle)titles[0]).getLevel());
        assertEquals("1", ((WikiTitle)titles[0]).getValue());
        assertEquals("a", ((WikiTitle)titles[0]).getTitle());
        IWikiStructureObject[] subtitles = titles[0].getChildren();
        assertEquals(1, subtitles.length);
        assertNotNull(subtitles[0]);
        assertTrue(subtitles[0] instanceof WikiTitle);
        assertEquals(1, ((WikiTitle)subtitles[0]).getLevel());
        assertEquals("1.1", ((WikiTitle)subtitles[0]).getValue());
        assertEquals("b", ((WikiTitle)subtitles[0]).getTitle());
    }

    public void testLevel011() throws Exception {
        String content = "1 a\n1.1 b\n1.1 c";
        WikiDocument doc = this.parser.parse(content);
        assertNotNull(doc);
        IWikiStructureObject[] titles = doc.getChildren();
        assertEquals(1, titles.length);
        assertNotNull(titles[0]);
        assertTrue(titles[0] instanceof WikiTitle);
        assertEquals(0, ((WikiTitle)titles[0]).getLevel());
        assertEquals("1", ((WikiTitle)titles[0]).getValue());
        assertEquals("a", ((WikiTitle)titles[0]).getTitle());
        IWikiStructureObject[] subtitles = titles[0].getChildren();
        assertEquals(2, subtitles.length);
        assertNotNull(subtitles[0]);
        assertTrue(subtitles[0] instanceof WikiTitle);
        assertEquals(1, ((WikiTitle)subtitles[0]).getLevel());
        assertEquals("1.1", ((WikiTitle)subtitles[0]).getValue());
        assertEquals("b", ((WikiTitle)subtitles[0]).getTitle());
        assertNotNull(subtitles[1]);
        assertTrue(subtitles[1] instanceof WikiTitle);
        assertEquals(1, ((WikiTitle)subtitles[1]).getLevel());
        assertEquals("1.2", ((WikiTitle)subtitles[1]).getValue());
        assertEquals("c", ((WikiTitle)subtitles[1]).getTitle());
    }

    public void testLevel012() throws Exception {
        String content = "1 a\n1.1 b\n1.1.1 c";
        WikiDocument doc = this.parser.parse(content);
        assertNotNull(doc);
        IWikiStructureObject[] titles = doc.getChildren();
        assertEquals(1, titles.length);
        assertNotNull(titles[0]);
        assertTrue(titles[0] instanceof WikiTitle);
        assertEquals(0, ((WikiTitle)titles[0]).getLevel());
        assertEquals("1", ((WikiTitle)titles[0]).getValue());
        assertEquals("a", ((WikiTitle)titles[0]).getTitle());
        IWikiStructureObject[] subtitles = titles[0].getChildren();
        assertEquals(1, subtitles.length);
        assertNotNull(subtitles[0]);
        assertTrue(subtitles[0] instanceof WikiTitle);
        assertEquals(1, ((WikiTitle)subtitles[0]).getLevel());
        assertEquals("1.1", ((WikiTitle)subtitles[0]).getValue());
        assertEquals("b", ((WikiTitle)subtitles[0]).getTitle());
        subtitles = subtitles[0].getChildren();
        assertEquals(1, subtitles.length);
        assertNotNull(subtitles[0]);
        assertTrue(subtitles[0] instanceof WikiTitle);
        assertEquals(2, ((WikiTitle)subtitles[0]).getLevel());
        assertEquals("1.1.1", ((WikiTitle)subtitles[0]).getValue());
        assertEquals("c", ((WikiTitle)subtitles[0]).getTitle());
    }

    public void testLevel0121() throws Exception {
        String content = "1 a\n1.1 b\n1.1.1 c\n1.1 d";
        WikiDocument doc = this.parser.parse(content);
        assertNotNull(doc);
        IWikiStructureObject[] titles = doc.getChildren();
        assertEquals(1, titles.length);
        assertNotNull(titles[0]);
        assertTrue(titles[0] instanceof WikiTitle);
        assertEquals(0, ((WikiTitle)titles[0]).getLevel());
        assertEquals("1", ((WikiTitle)titles[0]).getValue());
        assertEquals("a", ((WikiTitle)titles[0]).getTitle());
        IWikiStructureObject[] subtitles = titles[0].getChildren();
        assertEquals(2, subtitles.length);
        assertNotNull(subtitles[0]);
        assertTrue(subtitles[0] instanceof WikiTitle);
        assertEquals(1, ((WikiTitle)subtitles[0]).getLevel());
        assertEquals("1.1", ((WikiTitle)subtitles[0]).getValue());
        assertEquals("b", ((WikiTitle)subtitles[0]).getTitle());
        assertNotNull(subtitles[1]);
        assertTrue(subtitles[1] instanceof WikiTitle);
        assertEquals(1, ((WikiTitle)subtitles[1]).getLevel());
        assertEquals("1.2", ((WikiTitle)subtitles[1]).getValue());
        assertEquals("d", ((WikiTitle)subtitles[1]).getTitle());
        subtitles = subtitles[0].getChildren();
        assertEquals(1, subtitles.length);
        assertNotNull(subtitles[0]);
        assertTrue(subtitles[0] instanceof WikiTitle);
        assertEquals(2, ((WikiTitle)subtitles[0]).getLevel());
        assertEquals("1.1.1", ((WikiTitle)subtitles[0]).getValue());
        assertEquals("c", ((WikiTitle)subtitles[0]).getTitle());
    }

    public void testLevel02() throws Exception {
        String content = "1 a\n1.1.1 b";
        WikiDocument doc = this.parser.parse(content);
        assertNotNull(doc);
        IWikiStructureObject[] titles = doc.getChildren();
        assertEquals(1, titles.length);
        assertNotNull(titles[0]);
        assertTrue(titles[0] instanceof WikiTitle);
        assertEquals(0, ((WikiTitle)titles[0]).getLevel());
        assertEquals("1", ((WikiTitle)titles[0]).getValue());
        assertEquals("a", ((WikiTitle)titles[0]).getTitle());
        IWikiStructureObject[] subtitles = titles[0].getChildren();
        assertEquals(1, subtitles.length);
        assertNotNull(subtitles[0]);
        assertTrue(subtitles[0] instanceof WikiTitle);
        assertEquals(2, ((WikiTitle)subtitles[0]).getLevel());
        assertEquals("1.1.1", ((WikiTitle)subtitles[0]).getValue());
        assertEquals("b", ((WikiTitle)subtitles[0]).getTitle());
    }

    public void testLevel022() throws Exception {
        String content = "1 a\n1.1.1 b\n1.1.1 c";
        WikiDocument doc = this.parser.parse(content);
        assertNotNull(doc);
        IWikiStructureObject[] titles = doc.getChildren();
        assertEquals(1, titles.length);
        assertNotNull(titles[0]);
        assertTrue(titles[0] instanceof WikiTitle);
        assertEquals(0, ((WikiTitle)titles[0]).getLevel());
        assertEquals("1", ((WikiTitle)titles[0]).getValue());
        assertEquals("a", ((WikiTitle)titles[0]).getTitle());
        IWikiStructureObject[] subtitles = titles[0].getChildren();
        assertEquals(2, subtitles.length);
        assertNotNull(subtitles[0]);
        assertTrue(subtitles[0] instanceof WikiTitle);
        assertEquals(2, ((WikiTitle)subtitles[0]).getLevel());
        assertEquals("1.1.1", ((WikiTitle)subtitles[0]).getValue());
        assertEquals("b", ((WikiTitle)subtitles[0]).getTitle());
        assertNotNull(subtitles[1]);
        assertTrue(subtitles[1] instanceof WikiTitle);
        assertEquals(2, ((WikiTitle)subtitles[1]).getLevel());
        assertEquals("1.1.2", ((WikiTitle)subtitles[1]).getValue());
        assertEquals("c", ((WikiTitle)subtitles[1]).getTitle());
    }

//    public void testLevel12() throws Exception {
//        String content = "1.1 a\n1.1.1 b";
//        WikiDocument doc = this.parser.parse(content);
//        assertNotNull(doc);
//        IWikiStructureObject[] titles = doc.getChildren();
//        assertEquals(1, titles.length);
//        assertNotNull(titles[0]);
//        assertTrue(titles[0] instanceof WikiTitle);
//        assertEquals(0, ((WikiTitle)titles[0]).getLevel());
//        assertEquals("1.1", ((WikiTitle)titles[0]).getValue());
//        assertEquals("a", ((WikiTitle)titles[0]).getTitle());
//        IWikiStructureObject[] subtitles = titles[0].getChildren();
//        assertEquals(1, subtitles.length);
//        assertNotNull(subtitles[0]);
//        assertTrue(subtitles[0] instanceof WikiTitle);
//        assertEquals(2, ((WikiTitle)subtitles[0]).getLevel());
//        assertEquals("1.1.1", ((WikiTitle)subtitles[0]).getValue());
//        assertEquals("b", ((WikiTitle)subtitles[0]).getTitle());
//    }
}