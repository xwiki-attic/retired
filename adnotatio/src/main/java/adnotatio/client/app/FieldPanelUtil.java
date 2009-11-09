/**
 * 
 */
package adnotatio.client.app;

import java.util.Iterator;
import java.util.List;

import adnotatio.common.data.IPropertiesContainer;
import adnotatio.common.data.PropertiesContainer;
import adnotatio.renderer.templates.FieldPanel;

/**
 * This is an utility class used to transform the data from the
 * {@link IPropertiesContainer} format to the form panel and vice versa.
 * 
 * @author kotelnikov
 */
public class FieldPanelUtil {

    /**
     * Returns a map of all field values; the keys in this map are field names
     * and values are lists of field values. For each name it is possible to
     * have multiple fields/widgets so the signature of the returned map is
     * Map&lt;String, List&lt;String&gt;&gt;.
     * 
     * @return a map of field values
     */
    public static IPropertiesContainer getFieldValues(FieldPanel w) {
        IPropertiesContainer result = new PropertiesContainer();
        for (Iterator i = w.getFildNames().iterator(); i.hasNext();) {
            String key = (String) i.next();
            List values = w.getFieldValues(key);
            if (!values.isEmpty()) {
                result.setValues(key, values);
            }
        }
        return result;
    }

    /**
     * This method sets the specified values for each field name; all
     * non-specified fields will be cleaned up.
     * 
     * @param form the form to update
     * @param properties map of field names to the corresponding lists of values
     */
    public static void setFieldValues(
        FieldPanel form,
        IPropertiesContainer properties) {
        form.clear();
        updateFieldValues(form, properties);
    }

    /**
     * This method updates all values for each field name. Non-specified fields
     * are stay non-changed.
     * 
     * @param form the form to update
     * @param properties map of field names to the corresponding lists of values
     */
    public static void updateFieldValues(
        FieldPanel form,
        IPropertiesContainer properties) {
        for (Iterator iterator = properties.getNames().iterator(); iterator
            .hasNext();) {
            String name = (String) iterator.next();
            List values = properties.getValues(name);
            form.setFieldValues(name, values);
        }
    }

    /**
     * 
     */
    public FieldPanelUtil() {
        // TODO Auto-generated constructor stub
    }

}
