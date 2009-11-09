package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Widget;
import adnotatio.common.xml.IXmlElement;

/**
 * Hidden field wrapper.
 * 
 * @author kotelnikov
 */
public class HiddenFieldInfo extends FieldInfo {

    /**
     * @param panel TODO
     * @param e TODO
     */
    public HiddenFieldInfo(FieldPanel panel, IXmlElement e) {
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
        Hidden w = (Hidden) getWidget();
        return w.getValue();
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#newWidget()
     */
    protected Widget newWidget() {
        String name = getName();
        return new Hidden(name);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#setValue(Object)
     */
    protected boolean setValue(Object value) {
        String str = value != null ? value.toString() : "";
        Hidden hidden = (Hidden) getWidget();
        hidden.setValue(str);
        return true;
    }

}