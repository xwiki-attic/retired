package adnotatio.common.cache;

import java.util.Iterator;

public abstract class CacheBuilder {

    private StringBuffer fHtml = new StringBuffer();

    private CompositeCacheNode fTop;

    /**
     * @param object the object
     * @return the dynamic cache node
     */
    public CompositeCacheNode build(Object object) {
        fTop = newTopNode(object);
        doBuild(object);
        flushText();
        CompositeCacheNode top = fTop;
        fTop = null;
        return top;
    }

    private void doBuild(Object object) {
        CompositeCacheNode top = fTop;
        CompositeCacheNode compositeNode = getCompositeNode(object);
        String str = getTextPrefix(object, compositeNode);
        if (str != null) {
            fHtml.append(str);
        }
        if (compositeNode != null) {
            flushText();
            fTop.addNode(compositeNode);
            fTop = compositeNode;
        }
        Iterator iterator = getChildren(object, compositeNode);
        while (iterator != null && iterator.hasNext()) {
            Object n = iterator.next();
            doBuild(n);
        }
        str = getTextSuffix(object, compositeNode);
        if (str != null) {
            fHtml.append(str);
        }
        if (compositeNode != null) {
            flushText();
        }
        fTop = top;
    }

    private void flushText() {
        if (fHtml != null && fHtml.length() > 0) {
            TextCacheNode node = new TextCacheNode(fHtml.toString());
            fTop.addNode(node);
        }
        fHtml.delete(0, fHtml.length());
    }

    /**
     * @param object the object for which a list of children should be returned
     * @param compositeNode the composite node corresponding to the given object
     *        returned by the {@link #getCompositeNode(Object)} method; it can
     *        be <code>null</code>
     * @return an iterator over all child nodes of the specified object
     */
    protected abstract Iterator getChildren(
        Object object,
        CompositeCacheNode compositeNode);

    /**
     * Returns a dynamic node corresponding to the specified object. This method
     * returns <code>null</code> if this object does not correspond to a
     * dynamic node.
     * 
     * @param object for this object the corresponding dynamic node should be
     *        created and returned
     * @return a dynamic node corresponding to the specified object
     */
    protected abstract CompositeCacheNode getCompositeNode(Object object);

    /**
     * Returns the text prefix corresponding to the specified object; this
     * method can return <code>null</code>
     * 
     * @param object for this object the corresponding text prefix should be
     *        returned
     * @param compositeNode the composite node corresponding to the given object
     *        returned by the {@link #getCompositeNode(Object)} method; it can
     *        be <code>null</code>
     * @return the text prefix corresponding to the specified object; this
     *         method can return <code>null</code>
     */
    protected abstract String getTextPrefix(
        Object object,
        CompositeCacheNode compositeNode);

    /**
     * Returns the text suffix corresponding to the specified object; this
     * method can return <code>null</code>
     * 
     * @param object for this object the corresponding text suffix should be
     *        returned
     * @param compositeNode the composite node corresponding to the given object
     *        returned by the {@link #getCompositeNode(Object)} method; it can
     *        be <code>null</code>
     * @return the text suffix corresponding to the specified object; this
     *         method can return <code>null</code>
     */
    protected abstract String getTextSuffix(
        Object object,
        CompositeCacheNode compositeNode);

    /**
     * Creates and returns the top composite cache node corresponding to the
     * specified object
     * 
     * @param object for this object the corresponding cache node will be
     *        returned
     * @return the top composite cache node corresponding to the specified
     *         object
     */
    protected abstract CompositeCacheNode newTopNode(Object object);
}
