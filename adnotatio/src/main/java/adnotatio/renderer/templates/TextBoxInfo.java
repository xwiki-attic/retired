package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import adnotatio.common.xml.IXmlElement;

/**
 * A field wrapper corresponding to a text field
 * 
 * @author kotelnikov
 */
public class TextBoxInfo extends TextFieldInfo {

    /**
     * @param panel TODO
     * @param e TODO
     */
    public TextBoxInfo(FieldPanel panel, IXmlElement e) {
        super(panel, e);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#newWidget()
     */
    protected Widget newWidget() {
        return new TextBox();
    }
}