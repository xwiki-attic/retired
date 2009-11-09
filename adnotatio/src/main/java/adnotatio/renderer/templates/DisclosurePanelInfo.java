/**
 * 
 */
package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Widget;
import adnotatio.common.xml.IXmlElement;

/**
 * This is a wrapper for the {@link DisclosurePanel} widgets which are used to
 * create a disclosure panel with a clickable header and openable/closable
 * panels.
 * 
 * @author kotelnikov
 */
public class DisclosurePanelInfo extends FieldInfo implements IContainerField {

    /**
     * @param panel the parent panel
     * @param e the XML element defining the widget
     */
    public DisclosurePanelInfo(FieldPanel panel, IXmlElement e) {
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
        return new DisclosurePanel(label);
    }

    /**
     * @see adnotatio.renderer.templates.IContainerField#setContentWidget(com.google.gwt.user.client.ui.Widget)
     */
    public void setContentWidget(Widget widget) {
        DisclosurePanel panel = (DisclosurePanel) getWidget();
        panel.add(widget);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#setValue(Object)
     */
    protected boolean setValue(Object value) {
        return false;
    }

}
