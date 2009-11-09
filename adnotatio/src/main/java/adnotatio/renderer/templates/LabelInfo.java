/**
 * 
 */
package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import adnotatio.common.xml.IXmlElement;

/**
 * @author kotelnikov
 */
public class LabelInfo extends FieldInfo {

    /**
     * @param panel
     * @param e
     */
    public LabelInfo(FieldPanel panel, IXmlElement e) {
        super(panel, e);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#clear()
     */
    public void clear() {
        // 
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#getValue()
     */
    public Object getValue() {
        return null;
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#newWidget()
     */
    protected Widget newWidget() {
        String label = getLabelFromAttributes();
        return new Label(label);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#setValue(java.lang.Object)
     */
    protected boolean setValue(Object value) {
        return false;
    }

}
