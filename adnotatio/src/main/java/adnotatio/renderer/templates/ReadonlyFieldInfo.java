package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Widget;
import adnotatio.common.xml.IXmlElement;

/**
 * A field wrapper corresponding to a "read only" (immutable) form element. It
 * contains an internal hidden field and an HTML panel showing the content of
 * this field.
 * 
 * @author kotelnikov
 */
public class ReadonlyFieldInfo extends FieldInfo {

    Hidden fHidden;

    FlowPanel fPanel;

    HTML fView;

    /**
     * @param panel TODO
     * @param e TODO
     */
    public ReadonlyFieldInfo(FieldPanel panel, IXmlElement e) {
        super(panel, e);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#clear()
     */
    public void clear() {
        setValue("");
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#getValue()
     */
    public Object getValue() {
        return fHidden.getValue();
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#newWidget()
     */
    protected Widget newWidget() {
        fPanel = new FlowPanel();
        fHidden = new Hidden();
        fView = new HTML();
        fPanel.add(fHidden);
        fPanel.add(fView);
        return fPanel;
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#setValue(Object)
     */
    protected boolean setValue(Object value) {
        String str = value != null ? value.toString() : "";
        fHidden.setValue(str);
        fView.setHTML(str);
        return true;
    }

}