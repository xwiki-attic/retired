/**
 * 
 */
package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.Widget;
import adnotatio.common.xml.IXmlElement;

/**
 * A GWT button widget used to clear the form
 * 
 * @author kotelnikov
 */
public class ClearButtonInfo extends ButtonInfo {

    /**
     * @param panel
     * @param e
     */
    public ClearButtonInfo(FieldPanel panel, IXmlElement e) {
        super(panel, e);
    }

    /**
     * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
     */
    public void onClick(Widget sender) {
        String name = getAttribute("for");
        if (name != null) {
            boolean children = getAttributeAsBoolean("children", true);
            fFieldPanel.clear(name, children);
        } else {
            fFieldPanel.clear();
        }
    }
}