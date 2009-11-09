/**
 * 
 */
package adnotatio.client.renderer.cache;

import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import adnotatio.common.cache.CacheNode;
import adnotatio.common.cache.CompositeCacheNode;
import adnotatio.common.cache.ICacheNodeVisitor;
import adnotatio.common.cache.TextCacheNode;

/**
 * @author kotelnikov
 */
public class CacheTest extends TestCase {

    static interface IExtension {
        void onExtension(TestVisitor visitor, CompositeCacheNode node);
    }

    static class TestVisitor implements ICacheNodeVisitor {

        private Map fExtensions = new HashMap();

        public void addExtension(String key, IExtension extension) {
            fExtensions.put(key, extension);
        }

        public IExtension getExtension(String key) {
            return (IExtension) fExtensions.get(key);
        }

        private String getExtensionKey(CompositeCacheNode node) {
            return (String) node.getData("name");
        }

        public Map getParams(CompositeCacheNode node) {
            Map params = (Map) node.getData("params");
            return params != null ? params : Collections.EMPTY_MAP;
        }

        protected void print(String str) {
            System.out.print(str);
        }

        public void println(String string) {
            System.out.println(string);
        }

        public IExtension removeExtension(String key) {
            return (IExtension) fExtensions.remove(key);
        }

        public void visit(CompositeCacheNode node) {
            String extensionKey = getExtensionKey(node);
            IExtension extension = getExtension(extensionKey);
            if (extension != null) {
                extension.onExtension(this, node);
            } else {
                visitChildren(node);
            }
        }

        public void visit(TextCacheNode node) {
            print(node.getText());
        }

        public void visitChildren(CompositeCacheNode node) {
            int len = node.getChildrenCount();
            for (int i = 0; i < len; i++) {
                CacheNode child = node.getChild(i);
                child.accept(this);
            }
        }

    }

    private String TEMPLATE = ""
        + "<x:template>\n"
        + "<style>\n"
        + "    .hightlight { background-color: yellow; margin: 0; border: 1px solid gray; }\n"
        + "</style>\n"
        + "<h1>Hello, world!</h1>\n"
        + "<x:toto count='5'>\n"
        + "<p class='hightlight'>abc</p>\n"
        + "</x:toto>\n"
        + "<p>This is a simple paragraph</p>\n"
        + "</x:template>";

    public void test() throws Exception {
        StringReader reader = new StringReader(TEMPLATE);
        InputSource is = new InputSource(reader);
        Document doc = DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(is);

        Node n = doc.getDocumentElement();

        DomCacheBuilder builder = new DomCacheBuilder();
        CompositeCacheNode cache = builder.build(n);

        TestVisitor visitor = new TestVisitor();
        visitor.addExtension("x:template", new IExtension() {
            public void onExtension(TestVisitor visitor, CompositeCacheNode node) {
                visitor
                    .print("<html><head><title>Hello, world!</title></head><body>");
                visitor.visitChildren(node);
                visitor.print("</body></html>");
            }
        });

        visitor.addExtension("x:toto", new IExtension() {
            private int getIntValue(Map params, String key, int defaultValue) {
                int result = defaultValue;
                try {
                    String str = (String) params.get(key);
                    result = Integer.parseInt(str);
                } catch (Exception e) {
                    //
                }
                return result;
            }

            public void onExtension(TestVisitor visitor, CompositeCacheNode node) {
                visitor.println("<table>");
                Map params = visitor.getParams(node);
                int counter = getIntValue(params, "count", 1);
                for (int i = 0; i < counter; i++) {
                    visitor.print("<tr><td>");
                    visitor.visitChildren(node);
                    visitor.println("</td></tr>");
                }
                visitor.println("</table>");
            }
        });

        cache.accept(visitor);

        // PrintStream out = System.out;
        // print(n, out);
    }
}
