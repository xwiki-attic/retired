package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import adnotatio.common.xml.IXmlElement;

/**
 * This is a common wrapper for buttons
 * 
 * @author kotelnikov
 */
public abstract class ButtonInfo extends FieldInfo implements ClickListener {

    /**
     * The internal counter used to give unique button name
     */
    private static int fCounter;

    /**
     * @param e the XML element defining the widget
     */
    public ButtonInfo(FieldPanel panel, IXmlElement e) {
        super(panel, e);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#clear()
     */
    public void clear() {
        //
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#getName()
     */
    public String getName() {
        String name = super.getName();
        return name != null ? name : "Button-" + (fCounter++);
    }

    /**
     * The button values are never included in the form results
     * 
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
        if (label == null) {
            label = getAttribute("value");
        }
        Button b = new Button(label);
        b.addClickListener(this);
        return b;
    }

    protected void setEnabled(boolean enabled) {
        Button b = (Button) getWidget();
        b.setEnabled(enabled);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#setValue(Object)
     */
    protected boolean setValue(Object value) {
        return false;
    }
}