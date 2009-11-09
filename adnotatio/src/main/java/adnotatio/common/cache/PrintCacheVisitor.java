package adnotatio.common.cache;

public abstract class PrintCacheVisitor implements ICacheNodeVisitor {

    protected abstract void print(String str);

    public void visit(CompositeCacheNode node) {
        int len = node.getChildrenCount();
        for (int i = 0; i < len; i++) {
            CacheNode child = node.getChild(i);
            child.accept(this);
        }
    }

    public void visit(TextCacheNode node) {
        String str = node.getText();
        print(str);
    }

}
