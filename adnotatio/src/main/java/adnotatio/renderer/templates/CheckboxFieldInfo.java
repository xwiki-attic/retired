package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;
import adnotatio.common.xml.IXmlElement;

/**
 * A field wrapper for checkbox widgets
 * 
 * @author kotelnikov
 */
public class CheckboxFieldInfo extends FieldInfo {

    /**
     * The default checked state of the widget
     */
    boolean fChecked;

    /**
     * The value associated with this checkbox
     */
    String fValue;

    /**
     * @param panel TODO
     * @param e TODO
     */
    public CheckboxFieldInfo(FieldPanel panel, IXmlElement e) {
        super(panel, e);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#clear()
     */
    public void clear() {
        CheckBox w = (CheckBox) getWidget();
        w.setChecked(fChecked);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#getValue()
     */
    public Object getValue() {
        CheckBox w = (CheckBox) getWidget();
        return w.isChecked() ? fValue : null;
    }

    /**
     * Returns a new {@link CheckBox} widget. This method can be overloaded in
     * subclasses.
     * 
     * @param label the label of the checkbox
     * @param e the element
     * @return a new {@link CheckBox} widget
     */
    protected CheckBox newCheckBox(String label) {
        return new CheckBox(label, true);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#newWidget()
     */
    protected Widget newWidget() {
        String label = getLabelFromAttributes();
        fValue = getAttribute("value");
        if (fValue == null) {
            fValue = label;
        }
        fChecked = getAttributeAsBoolean("checked", false);
        CheckBox w = newCheckBox(label);
        String name = getName();
        w.setName(name);
        w.setChecked(fChecked);
        return w;
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#setEnabled(boolean)
     */
    protected void setEnabled(boolean enabled) {
        CheckBox checkBox = (CheckBox) getWidget();
        checkBox.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#setValue(Object)
     */
    protected boolean setValue(Object value) {
        if (equals(fValue, value)) {
            CheckBox checkBox = (CheckBox) getWidget();
            checkBox.setChecked(true);
            return true;
        }
        return false;
    }
}