/**
 * 
 */
package adnotatio.renderer.templates;

import java.util.Iterator;
import java.util.List;

/**
 * This is a default implementation of the {@link IFieldInfoVIsitor} interface.
 * By default it just visits all child nodes of the field.
 * 
 * @author kotelnikov
 */
public abstract class FieldInfoVisitor implements IFieldInfoVIsitor {

    /**
     * 
     */
    public FieldInfoVisitor() {
        super();
    }

    /**
     * This is an utility method used to visit all children of the specified
     * field.
     * 
     * @param info the field wrapper for which all children will be visited.
     */
    protected void visitChildren(FieldInfo info) {
        List children = info.getChildren();
        for (Iterator iterator = children.iterator(); iterator.hasNext();) {
            FieldInfo child = (FieldInfo) iterator.next();
            child.accept(this);
        }
    }

}
