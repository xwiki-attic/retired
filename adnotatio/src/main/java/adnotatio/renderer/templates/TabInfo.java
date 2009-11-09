/**
 * 
 */
package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import adnotatio.common.xml.IXmlElement;

/**
 * @author kotelnikov
 */
public class TabInfo extends FieldInfo implements IContainerField {

    /**
     * @param panel
     * @param e
     */
    public TabInfo(FieldPanel panel, IXmlElement e) {
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
        return new SimplePanel();
    }

    /**
     * @see adnotatio.renderer.templates.IContainerField#setContentWidget(com.google.gwt.user.client.ui.Widget)
     */
    public void setContentWidget(Widget widget) {
        SimplePanel panel = (SimplePanel) getWidget();
        panel.setWidget(widget);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#setValue(java.lang.Object)
     */
    protected boolean setValue(Object value) {
        return false;
    }

}
