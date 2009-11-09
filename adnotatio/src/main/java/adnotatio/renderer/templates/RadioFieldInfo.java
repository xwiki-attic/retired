package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.RadioButton;
import adnotatio.common.xml.IXmlElement;

/**
 * A field wrapper corresponding to a radiobutton
 * 
 * @author kotelnikov
 */
public class RadioFieldInfo extends CheckboxFieldInfo {

    /**
     * @param panel TODO
     * @param e TODO
     */
    public RadioFieldInfo(FieldPanel panel, IXmlElement e) {
        super(panel, e);
    }

    /**
     * @see adnotatio.renderer.templates.CheckboxFieldInfo#newCheckBox(java.lang.String)
     */
    protected CheckBox newCheckBox(String label) {
        String name = getName();
        final RadioButton w = new RadioButton(name, label, true);
        return w;
    }
}