/**
 * 
 */
package adnotatio.client.util;

import adnotatio.client.annotator.util.PrintXMLParser;
import junit.framework.TestCase;

/**
 * @author kotelnikov
 */
public class SimpleXMLParserTest extends TestCase {

    public SimpleXMLParserTest(String name) {
        super(name);
    }

    public void test() {
        test("<cde 2=\"3\">", "<cde 2='3'>");
        test("<cde x='1' 2=\"3\">", "<cde x='1' 2='3'>");
        test("<cde x=y z x='1' 2=\"3\">", "<cde x='y' z='' x='1' 2='3'>");

        test("<cde c='d' d='e' >", "<cde c='d' d='e'>");
        test("<cde c=d >", "<cde c='d'>");
        test("<cde c=word >", "<cde c='word'>");
        test(
            "<cde c='this is a long parameter' d=e>",
            "<cde c='this is a long parameter' d='e'>");
        test(
            "<cde c=\"this is a long parameter\" d=e>",
            "<cde c='this is a long parameter' d='e'>");
        test("<cde c>", "<cde c=''>");
        test("<cde c d>", "<cde c='' d=''>");
        test("<cde c d e>", "<cde c='' d='' e=''>");
        test("<cde c d=e>", "<cde c='' d='e'>");
        test("<cde a=b c>", "<cde a='b' c=''>");

        test("abc", "abc");
        test("abc cde", "abc cde");
        test("abc <cde> efg", "abc <cde> efg");
        test("abc <cde x=y> efg", "abc <cde x='y'> efg");

        test("<cde x = y y = z>", "<cde x='y' y='z'>");
        test(
            "abc <cde   x=y   z   x='1'   2  =  \"3\"  >efg</cde> ijk",
            "abc <cde x='y' z='' x='1' 2='3'>efg</cde> ijk");
    }

    private void test(String str, String control) {
        final StringBuffer buf = new StringBuffer();
        PrintXMLParser splitter = new PrintXMLParser() {
            protected void print(String str) {
                buf.append(str);
            }
        };
        splitter.split(str);
        System.out.println(buf);
        assertEquals(control, buf.toString());
    }

    public void testEntity() {
        test("abc &nbsp; cde", "abc &nbsp; cde");
        testEscaping("abc &nbsp; cde", "[abc] &nbsp; [cde]");
        testEscaping("abc&nbsp;cde", "[abc]&nbsp;[cde]");
    };

    public void testEscaping() {
        testEscaping("abc", "[abc]");
        testEscaping(
            "<p>This is a phrase...</p>",
            "<p>[This] [is] [a] [phrase]...</p>");
    }

    private void testEscaping(String str, String control) {
        final StringBuffer buf = new StringBuffer();
        PrintXMLParser splitter = new PrintXMLParser() {
            public void onWord(String word) {
                print("[");
                super.onWord(word);
                print("]");
            }

            protected void print(String str) {
                buf.append(str);
            }
        };
        splitter.split(str);
        System.out.println(buf);
        assertEquals(control, buf.toString());
    }
}
