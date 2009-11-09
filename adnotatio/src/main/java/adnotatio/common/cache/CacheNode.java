/**
 * 
 */
package adnotatio.common.cache;

public abstract class CacheNode {

    private CompositeCacheNode fParent;

    public CacheNode() {
        super();
    }

    public void accept(ICacheNodeVisitor visitor) {
        if (this instanceof CompositeCacheNode) {
            visitor.visit((CompositeCacheNode) this);
        } else {
            visitor.visit((TextCacheNode) this);
        }
    }

    public CompositeCacheNode getParent() {
        return fParent;
    }

    void setParent(CompositeCacheNode parent) {
        fParent = parent;
    }

}