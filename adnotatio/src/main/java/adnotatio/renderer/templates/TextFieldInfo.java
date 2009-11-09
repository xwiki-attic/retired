package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.TextBoxBase;
import adnotatio.common.xml.IXmlElement;

/**
 * A common wrapper for text widgets.
 * 
 * @author kotelnikov
 */
public abstract class TextFieldInfo extends FieldInfo {

    /**
     * @param panel
     * @param e TODO
     */
    public TextFieldInfo(FieldPanel panel, IXmlElement e) {
        super(panel, e);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#clear()
     */
    public void clear() {
        getTextBoxBase().setText("");
    }

    /**
     * An utility method casting the internal widget to the {@link TextBoxBase}
     * type
     * 
     * @return a {@link TextBoxBase} widget
     */
    protected final TextBoxBase getTextBoxBase() {
        return (TextBoxBase) getWidget();
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#getValue()
     */
    public Object getValue() {
        return getTextBoxBase().getText();
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#setEnabled(boolean)
     */
    protected void setEnabled(boolean enabled) {
        getTextBoxBase().setEnabled(enabled);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#setValue(Object)
     */
    protected boolean setValue(Object value) {
        String str = value != null ? value.toString() : "";
        getTextBoxBase().setText(str);
        return true;
    }

}