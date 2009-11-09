/**
 * 
 */
package adnotatio.renderer.templates;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import adnotatio.common.xml.IXmlElement;

/**
 * A wrapper for simple HTML panels
 * 
 * @author kotelnikov
 */
public class InlineHTMLPanelInfo extends HTMLPanelInfo {

    /**
     * @param panel TODO
     * @param e TODO
     */
    public InlineHTMLPanelInfo(FieldPanel panel, IXmlElement e) {
        super(panel, e);
    }

    /**
     * @see adnotatio.renderer.templates.HTMLPanelInfo#newWidget()
     */
    protected Widget newWidget() {
        return new HTML() {
            {
                setElement(DOM.createSpan());
            }
        };
    }
}