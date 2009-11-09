/**
 * 
 */
package adnotatio.renderer.templates;

/**
 * This is a visitor interface used to visit individual fields in templates.
 * 
 * @author kotelnikov
 */
public interface IFieldInfoVIsitor {

    /**
     * Visits the specified field.
     * 
     * @param info the wrapper for a field
     */
    void visit(FieldInfo info);

}
