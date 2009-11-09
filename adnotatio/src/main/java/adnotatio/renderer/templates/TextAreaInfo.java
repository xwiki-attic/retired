package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import adnotatio.common.xml.IXmlElement;

/**
 * A field wrapper corresponding to a text area
 * 
 * @author kotelnikov
 */
public class TextAreaInfo extends TextFieldInfo {

    /**
     * @param panel
     * @param e TODO
     */
    public TextAreaInfo(FieldPanel panel, IXmlElement e) {
        super(panel, e);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#newWidget()
     */
    protected Widget newWidget() {
        return new TextArea();
    }
}