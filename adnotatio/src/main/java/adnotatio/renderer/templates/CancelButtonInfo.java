/**
 * 
 */
package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.Widget;
import adnotatio.common.xml.IXmlElement;

/**
 * A GWT button calling the {@link FieldPanel#onCancel(Widget)} method on the
 * parent template.
 */
public class CancelButtonInfo extends ButtonInfo {

    /**
     * @param panel
     * @param e
     */
    public CancelButtonInfo(FieldPanel panel, IXmlElement e) {
        super(panel, e);
    }

    /**
     * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
     */
    public void onClick(Widget sender) {
        fFieldPanel.onCancel(sender);
    }
}