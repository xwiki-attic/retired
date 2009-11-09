package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;
import adnotatio.common.xml.IXmlElement;

/**
 * GWT button calling the {@link FormPanel#onFormSubmit()} method.
 * 
 * @author kotelnikov
 */
public class SubmitButtonInfo extends ButtonInfo {

    /**
     * @param panel
     * @param e
     */
    public SubmitButtonInfo(FieldPanel panel, IXmlElement e) {
        super(panel, e);
    }

    /**
     * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
     */
    public void onClick(Widget sender) {
        fFieldPanel.onSubmit(sender);
    }
}