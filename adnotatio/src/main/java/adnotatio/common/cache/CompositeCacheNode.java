/**
 * 
 */
package adnotatio.common.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositeCacheNode extends CacheNode {

    private Map fDataMap = new HashMap();

    private List fList = new ArrayList();

    public CompositeCacheNode() {
        super();
    }

    void addNode(CacheNode node) {
        fList.add(node);
        node.setParent(this);
    }

    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof CompositeCacheNode))
            return false;
        CompositeCacheNode node = (CompositeCacheNode) obj;
        return fList.equals(node.fList);
    }

    public CacheNode getChild(int pos) {
        return (CacheNode) (pos >= 0 && pos <= fList.size()
            ? fList.get(pos)
            : null);
    }

    /**
     * @return the number of child elements
     */
    public int getChildrenCount() {
        return fList.size();
    }

    public Object getData(String key) {
        return fDataMap.get(key);
    }

    public int hashCode() {
        int l = fList.hashCode();
        return l;
    }

    public Object removeData(String key) {
        return fDataMap.remove(key);
    }

    public void setData(String key, Object value) {
        fDataMap.put(key, value);
    }

    public String toString() {
        return fList.toString();
    }

}