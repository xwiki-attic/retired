/**
 * 
 */
package adnotatio.common.cache;

public class TextCacheNode extends CacheNode {

    private String fText;

    public TextCacheNode(String text) {
        super();
        fText = text;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof TextCacheNode))
            return false;
        TextCacheNode node = (TextCacheNode) obj;
        return fText.equals(node.fText);
    }

    public String getText() {
        return fText;
    }

    public int hashCode() {
        return fText != null ? fText.hashCode() : 0;
    }

    public String toString() {
        return fText;
    }

}