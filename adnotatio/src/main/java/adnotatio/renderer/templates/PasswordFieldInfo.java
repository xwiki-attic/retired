/**
 * 
 */
package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;
import adnotatio.common.xml.IXmlElement;

/**
 * A field wrapper corresponding to a password form element
 * 
 * @author kotelnikov
 */
public class PasswordFieldInfo extends TextFieldInfo {

    /**
     * @param panel TODO
     * @param e TODO
     */
    public PasswordFieldInfo(FieldPanel panel, IXmlElement e) {
        super(panel, e);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#newWidget()
     */
    protected Widget newWidget() {
        return new PasswordTextBox();
    }
}