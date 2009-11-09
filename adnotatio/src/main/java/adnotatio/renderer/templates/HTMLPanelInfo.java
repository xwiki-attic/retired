package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import adnotatio.common.xml.IXmlElement;

/**
 * A common wrapper for HTML panels.
 * 
 * @author kotelnikov
 */
public class HTMLPanelInfo extends FieldInfo {

    /**
     * @param panel TODO
     * @param e TODO
     */
    public HTMLPanelInfo(FieldPanel panel, IXmlElement e) {
        super(panel, e);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#clear()
     */
    public void clear() {
        HTML html = (HTML) getWidget();
        html.setHTML("");
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#getValue()
     */
    public Object getValue() {
        HTML html = (HTML) getWidget();
        return html.getHTML();
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#newWidget()
     */
    protected Widget newWidget() {
        return new HTML();
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#setValue(Object)
     */
    protected boolean setValue(Object value) {
        HTML html = (HTML) getWidget();
        String str = value != null ? value.toString() : "";
        html.setHTML(str);
        return true;
    }
}