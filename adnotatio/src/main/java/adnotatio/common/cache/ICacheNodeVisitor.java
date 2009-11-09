/**
 * 
 */
package adnotatio.common.cache;

public interface ICacheNodeVisitor {

    void visit(CompositeCacheNode node);

    void visit(TextCacheNode node);
}