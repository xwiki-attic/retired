package adnotatio.renderer.templates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import adnotatio.common.xml.IXmlElement;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 * A common superclass for all wrappers of form widgets. The goal of this
 * wrapper is to provide a common set of actions for various form widgets.
 * 
 * @author kotelnikov
 */
public abstract class FieldInfo {

    /**
     * The counter used to generate unique names. It is used only for fields
     * without explicit names.
     */
    private static int fNameCounter;

    /**
     * Compares the given values and returns <code>true</code> if they are
     * equal.
     * 
     * @param first the first value to compare
     * @param second the second value to compare
     * @return <code>true</code> if both values are equals
     */
    protected static boolean equals(Object first, Object second) {
        return first != null && second != null
            ? first.equals(second)
            : first == second;
    }

    /**
     * This list contains all children ({@link FieldInfo} instances)
     */
    private List fChildren;

    /**
     * The XML element defining the widget
     */
    protected final IXmlElement fElement;

    /**
     * The form widget owning this info
     */
    protected FieldPanel fFieldPanel;

    /**
     * The name of this widget.
     */
    private String fName;

    /**
     * The parent field
     */
    protected FieldInfo fParent;

    /**
     * The widget corresponding to this field
     */
    private Widget fWidget;

    /**
     * The default constructor
     * 
     * @param panel the parent panel
     * @param e the XML element describing the widget
     */
    public FieldInfo(FieldPanel panel, IXmlElement e) {
        fElement = e;
        fFieldPanel = panel;
        fFieldPanel.addFieldInfo(this);

        fWidget = newWidget();
        Set attributes = fElement.getAttributeNames();
        com.google.gwt.user.client.Element widgetElement = getWidgetElement(fWidget);
        for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            if (!isValidAttribute(key))
                continue;
            String value = fElement.getAttribute(key);
            DOM.setElementAttribute(widgetElement, key, value);
        }
    }

    /**
     * Accept the specified visitor. By default this method just calls the
     * {@link IFieldInfoVIsitor#visit(FieldInfo)} method.
     * 
     * @param visitor the visitor to accept
     */
    public void accept(IFieldInfoVIsitor visitor) {
        visitor.visit(this);
    }

    /**
     * Adds a new child field.
     * 
     * @param child a new child to add
     */
    protected void addChildFieldInfo(FieldInfo child) {
        if (child.fParent != null) {
            child.fParent.fChildren.remove(child);
        }
        child.fParent = this;
        if (fChildren == null)
            fChildren = new ArrayList();
        fChildren.add(child);
    }

    /**
     * Cleans up all internal fields
     */
    public abstract void clear();

    /**
     * Returns the attribute value corresponding to the specified name
     * 
     * @param name the name of the attribute to return
     * @return the attribute value corresponding to the specified name
     */
    public String getAttribute(String name) {
        return fElement.getAttribute(name);
    }

    /**
     * Returns the specified attribute as a boolean value
     * 
     * @param name the name of the attribute
     * @param defaultValue the default value of the attribute
     * @return a boolean value of the specified attributed
     */
    public boolean getAttributeAsBoolean(String name, boolean defaultValue) {
        String value = getAttribute(name);
        boolean result = defaultValue;
        if (value != null) {
            result = "true".equals(value)
                || "yes".equals(value)
                || "1".equals(value)
                || "checked".equals(value);
        }
        return result;
    }

    /**
     * Returns all child field wrappers
     * 
     * @return the children (all child field wrappers)
     */
    public List getChildren() {
        return fChildren != null ? fChildren : new ArrayList();
    }

    /**
     * Returns an interface providing access to internationalized messages and
     * labels
     * 
     * @return an interface providing access to internationalized messages and
     *         labels
     */
    public IFieldI18N getConstants() {
        return fFieldPanel.getConstants();
    }

    /**
     * This is an utility method returning a label associated with this widget.
     * This method tries to get a label key defined by the "labelKey" attribute
     * and if it is found then it loads an internationalized message with this
     * key. If the corresponding label was not found (or if the "labelKey"
     * attribute is not defined) then this method tries to load the value of the
     * "label" attribute.
     * 
     * @return a label associated with the current widget
     */
    protected String getLabelFromAttributes() {
        String labelKey = getAttribute("labelKey");
        String label = null;
        if (labelKey != null) {
            IFieldI18N constants = getConstants();
            label = constants.getString(labelKey);
        }
        if (label == null) {
            label = getAttribute("label");
        }
        if (label == null) {
            label = getAttribute("title");
        }
        return label;
    }

    /**
     * Returns the name of the form element
     * 
     * @return the name of the form element
     */
    public String getName() {
        if (fName == null) {
            fName = fElement.getAttribute("name");
        }
        if (fName == null) {
            fName = "field" + (fNameCounter++);
        }
        return fName;
    }

    /**
     * Returns the string value extracted from the widget
     * 
     * @return the string value extracted from the widget
     */
    public abstract Object getValue();

    /**
     * @return the widget associated with this wrapper
     */
    public final Widget getWidget() {
        return fWidget;
    }

    /**
     * Returns the browser DOM element to which all attributes of the widget
     * should be applied
     * 
     * @param widget the widget for which attributes should be applied
     * @return the browser DOM element to which all attributes of the widget
     *         should be applied
     */
    protected com.google.gwt.user.client.Element getWidgetElement(Widget widget) {
        return widget.getElement();
    }

    /**
     * Returns <code>true</code> if the specified attribute should be copied
     * from the original XML element to the target HTML widget
     * 
     * @param name the name of the attribute to check
     * @return <code>true</code> if the specified attribute should be copied
     *         from the original XML element to the target HTML widget
     */
    protected boolean isValidAttribute(String name) {
        return !"type".equals(name);
    }

    /**
     * Creates and returns a new widget corresponding to the given XML element
     * 
     * @return a newly created widget corresponding to the field
     */
    protected abstract Widget newWidget();

    /**
     * Enables or disables the controlled form element
     * 
     * @param enabled if this flag is <code>true</code> then the controlled
     *        element will be enabled; otherwise it will be disabled
     */
    protected void setEnabled(boolean enabled) {
        if (fChildren != null) {
            for (Iterator iterator = fChildren.iterator(); iterator.hasNext();) {
                FieldInfo info = (FieldInfo) iterator.next();
                info.setEnabled(enabled);
            }
        }
    }

    /**
     * Sets the specified value in the controlled field
     * 
     * @param value the value of the controlled field
     * @return <code>true</code> if the specified value was successfully
     *         setted
     */
    protected abstract boolean setValue(Object value);

    /**
     * Sets some values from the beginning of the given list and returns the
     * rest of the list
     * 
     * @param values the values to set
     * @return a list of not used values
     */
    protected List setValues(List values) {
        Object value = null;
        List result = new ArrayList();
        if (values != null && values.size() > 0) {
            result.addAll(values);
            value = result.remove(0);
        }
        setValue(value);
        return result;
    }

}