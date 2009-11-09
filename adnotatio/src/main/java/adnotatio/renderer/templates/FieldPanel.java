/**
 * 
 */
package adnotatio.renderer.templates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ClickListenerCollection;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * The form template is a widget which transform a given XML node into a form.
 * It recognizes almost all HTML form elements and replaces them by the
 * corresponding GWT widgets. These widgets are accessible by their names. The
 * values filled by the user in this form are accessible using the
 * {@link FieldPanel#getFieldValues()} method.
 * 
 * @author kotelnikov
 */
public class FieldPanel extends Composite {

    /**
     * The internal HTML panel used as a container for widgets. It can replace
     * 
     * @author kotelnikov
     */
    static class InternalHTMLPanel extends ComplexPanel {

        /**
         * The default constructor initializing the owner field
         */
        InternalHTMLPanel() {
            setElement(DOM.createDiv());
        }

        /**
         * Sets the internal HTML and replace all widget placeholders by the
         * corresponding widgets. The given HTML can contain HTML elements
         * marked by unique identifiers; If for such elements there are
         * corresponding widgets in the given map then these elements will be
         * replaced by widgets.
         * 
         * @param html the HTML to set; this HTML contains placeholders marked
         *        by unique identifiers; these placeholders will be replaced by
         *        the corresponding widgets
         * @param fieldInfoMap this map contains element identifiers and the
         *        corresponding to them {@link FieldInfo} objects; these field
         *        info objects are used to instantiate widgets which are
         *        inserted in the content instead placeholders
         */
        public void setPanelContent(String html, Map fieldInfoMap) {
            DOM.setInnerHTML(getElement(), html);
            setWidgets(getElement(), fieldInfoMap);
        }

        /**
         * Replace DOM elements by the corresponding widgets defined in the
         * specified field info map;
         * 
         * @param elem the top element
         * @param fieldInfoMap this map contains element ids and the
         *        corresponding {@link FieldInfo} objects used to get widgets to
         *        insert in the panel
         */
        private void setWidgets(Element elem, Map fieldInfoMap) {
            Element child = DOM.getFirstChild(elem);
            while (child != null) {
                String id = DOM.getElementProperty(child, "id");
                Element next = DOM.getNextSibling(child);
                FieldInfo info = id != null
                    ? (FieldInfo) fieldInfoMap.get(id)
                    : null;
                if (info != null) {
                    // The child element is a marker element; it will be
                    // replaced by the corresponding widget
                    Widget widget = info.getWidget();
                    // Detach new child.
                    widget.removeFromParent();
                    // Logical attach.
                    getChildren().add(widget);
                    // Physical attach.
                    DOM.insertBefore(elem, widget.getElement(), child);
                    // Adopt.
                    adopt(widget);
                    // Removes the marker element
                    DOM.removeChild(elem, child);
                } else {
                    setWidgets(child, fieldInfoMap);
                }
                child = next;
            }
        }

    }

    /**
     * Container of cancel listeners
     */
    private ClickListenerCollection fCancelListeners;

    /**
     * The interface providing access to internationalized messages and labels
     */
    private IFieldI18N fConstants;

    /**
     * This map contains field names (keys) and lists of corresponding
     * {@link FieldInfo} objects (values).
     */
    Map fFieldInfoMap = new HashMap();

    /**
     * The main panel containing the internal HTML and all form fields
     */
    InternalHTMLPanel fPanel;

    /**
     * Container of submit listeners
     */
    private ClickListenerCollection fSubmitListeners;

    /**
     * The default constructor.
     * 
     * @param constants the interface providing access to the internationalized
     *        labels
     */
    public FieldPanel(IFieldI18N constants) {
        super();
        fConstants = constants != null ? constants : IFieldI18N.NULL;
        fPanel = newTemplatePanel();
        initWidget(fPanel);
    }

    /**
     * Adds a new click listener for cancel buttons
     * 
     * @param listener the listener to add
     */
    public void addCancelListener(ClickListener listener) {
        if (fCancelListeners == null) {
            fCancelListeners = new ClickListenerCollection();
        }
        fCancelListeners.add(listener);
    }

    /**
     * Adds the given field info object to the internal map
     * 
     * @param info the field info to add
     */
    protected void addFieldInfo(FieldInfo info) {
        String name = info.getName();
        List list = getFieldInfo(name);
        if (list == null) {
            list = new ArrayList();
            fFieldInfoMap.put(name, list);
        }
        list.add(info);
    }

    /**
     * Adds a new click listener for submit buttons
     * 
     * @param listener the listener to add
     */
    public void addSubmitListener(ClickListener listener) {
        if (fSubmitListeners == null) {
            fSubmitListeners = new ClickListenerCollection();
        }
        fSubmitListeners.add(listener);
    }

    /**
     * Cleans up all internal fields
     */
    public void clear() {
        for (Iterator i = fFieldInfoMap.values().iterator(); i.hasNext();) {
            List list = (List) i.next();
            for (Iterator j = list.iterator(); j.hasNext();) {
                FieldInfo info = (FieldInfo) j.next();
                info.clear();
            }
        }
    }

    /**
     * Cleans up all fields with the specified names. If the given flag
     * <code>children</code> is <code>true</code> then all child fields are
     * cleaned up as well.
     * 
     * @param name the name of the field to clean up
     * @param child if this flag is <code>true</code> then all children of the
     *        field with the specified name will be cleaned up as well.
     */
    public void clear(String name, boolean children) {
        List fields = getFieldInfo(name);
        if (fields != null) {
            if (children) {
                IFieldInfoVIsitor visitor = new FieldInfoVisitor() {
                    public void visit(FieldInfo info) {
                        info.clear();
                        visitChildren(info);
                    }
                };
                for (Iterator i = fields.iterator(); i.hasNext();) {
                    FieldInfo info = (FieldInfo) i.next();
                    info.accept(visitor);
                }
            } else {
                for (Iterator i = fields.iterator(); i.hasNext();) {
                    FieldInfo info = (FieldInfo) i.next();
                    info.clear();
                }
            }
        }
    }

    /**
     * Returns an interface providing access to internationalized messages and
     * labels
     * 
     * @return an interface providing access to internationalized messages and
     *         labels
     */
    public IFieldI18N getConstants() {
        return fConstants;
    }

    /**
     * Returns a list of field info objects ({@link FieldInfo}) corresponding
     * to the specified name
     * 
     * @param fieldName the name of the field
     * @return a list of field info objects corresponding to the specified name
     */
    public List getFieldInfo(String fieldName) {
        return (List) fFieldInfoMap.get(fieldName);
    }

    /**
     * Returns the number of fields with the specified name
     * 
     * @param fieldName the name of the fields
     * @return the number of fields with the specified name
     */
    public int getFieldNumber(String fieldName) {
        List list = getFieldInfo(fieldName);
        return list != null ? list.size() : 0;
    }

    /**
     * Returns the value of the first field with the specified name; if there is
     * no such a field then this method returns <code>null</code>;
     * 
     * @param fieldName the name of the field
     * @return the value of the first field with the specified name
     */
    public Object getFieldValue(String fieldName) {
        List list = getFieldInfo(fieldName);
        if (list == null || list.size() == 0)
            return null;
        FieldInfo info = (FieldInfo) list.get(0);
        return info.getValue();
    }

    /**
     * Returns a list of values of fields with the specified name; if there is
     * no such a field then this method returns <code>null</code>;
     * 
     * @param fieldName the name of fields
     * @return the value of all fields with the specified name
     */
    public List getFieldValues(String fieldName) {
        List list = getFieldInfo(fieldName);
        if (list == null || list.size() == 0)
            return null;
        int len = list.size();
        List result = new ArrayList(len);
        for (int i = 0; i < len; i++) {
            FieldInfo info = (FieldInfo) list.get(i);
            Object value = info.getValue();
            if (value != null) {
                result.add(value);
            }
        }
        return result;
    }

    /**
     * Returns the first form widget corresponding to the specified name (if
     * any)
     * 
     * @param name the name of the field
     * @return the first form widget corresponding to the specified name (if
     *         any)
     */
    public Widget getFieldWidget(String name) {
        List list = getFieldInfo(name);
        if (list == null)
            return null;
        int len = list.size();
        if (len == 0)
            return null;
        FieldInfo info = (FieldInfo) list.get(0);
        return info.getWidget();
    }

    /**
     * Returns a list of all form widgets corresponding to the specified name
     * 
     * @param name the name of the field for which the corresponding widget
     *        should be returned
     * @return a list of all widgets corresponding to the specified name
     */
    public List getFieldWidgets(String name) {
        List list = getFieldInfo(name);
        if (list == null)
            return null;
        int len = list.size();
        List result = new ArrayList(len);
        for (int i = 0; i < len; i++) {
            FieldInfo info = (FieldInfo) list.get(i);
            Widget w = info.getWidget();
            result.add(w);
        }
        return result;
    }

    /**
     * Returns a set of all field names.
     * 
     * @return a set of all field names
     */
    public Set getFildNames() {
        return fFieldInfoMap.keySet();
    }

    /**
     * Returns the main HTML panel containing all widgets
     * 
     * @return the main HTML panel containing all widgets
     */
    public InternalHTMLPanel getPanel() {
        return fPanel;
    }

    /**
     * Creates and returns a new internal panel used as a container for the HTML
     * with embedded GWT widgets
     * 
     * @return a new internal panel used as a container for HTML with embedded
     *         widgets
     */
    InternalHTMLPanel newTemplatePanel() {
        return new InternalHTMLPanel();
    }

    /**
     * This method is called to notify that the operations in this form was
     * canceled.
     * 
     * @param sender the widget activating this operation; normally it is a
     *        button
     */
    public void onCancel(Widget sender) {
        if (fCancelListeners != null) {
            fCancelListeners.fireClick(sender);
        }
    }

    /**
     * This method is called to notify that the operations in this form was
     * submitted.
     * 
     * @param sender the widget activating this operation; normally it is a
     *        button
     */
    public void onSubmit(Widget sender) {
        if (fSubmitListeners != null) {
            fSubmitListeners.fireClick(sender);
        }
    }

    /**
     * Removes the specified click listener from the list of cancel listeners
     * 
     * @param listener the listener to remove
     */
    public void removeCancelListener(ClickListener listener) {
        if (fCancelListeners != null) {
            fCancelListeners.remove(listener);
        }
    }

    /**
     * Removes the specified click listener from the list of submit listeners
     * 
     * @param listener the listener to remove
     */
    public void removeSubmitListener(ClickListener listener) {
        if (fSubmitListeners != null) {
            fSubmitListeners.remove(listener);
        }
    }

    /**
     * Resets the values of all fields.
     */
    public void reset() {
        for (Iterator i = fFieldInfoMap.values().iterator(); i.hasNext();) {
            List list = (List) i.next();
            for (Iterator j = list.iterator(); j.hasNext();) {
                FieldInfo w = (FieldInfo) j.next();
                w.clear();
            }
        }
    }

    /**
     * Enables/disables all fields in this form
     * 
     * @param enabled this flag is used to define if fields should be enabled or
     *        to disabled
     */
    public void setEnabled(boolean enabled) {
        for (Iterator i = fFieldInfoMap.values().iterator(); i.hasNext();) {
            List list = (List) i.next();
            for (Iterator j = list.iterator(); j.hasNext();) {
                FieldInfo info = (FieldInfo) j.next();
                info.setEnabled(enabled);
            }
        }
    }

    /**
     * Sets the specified value of the field with the given name and returns
     * <code>true</code> if the the value was successfully stored
     * 
     * @param fieldName the name of the field for which the value should be
     *        stored
     * @param value the value to set
     * @return <code>true</code> if the specified value was successfully
     *         stored
     */
    public boolean setFieldValue(String fieldName, String value) {
        List list = getFieldInfo(fieldName);
        if (list == null)
            return false;
        boolean result = false;
        int len = list.size();
        for (int i = 0; !result && i < len; i++) {
            FieldInfo info = (FieldInfo) list.get(i);
            result = info.setValue(value);
        }
        return result;
    }

    /**
     * Sets the specified values in the fields with the specified name
     * 
     * @param fieldName the name of the field for which values should be stored
     * @param values an array of values to set
     */
    public void setFieldValues(String fieldName, List values) {
        List list = getFieldInfo(fieldName);
        if (list == null)
            return;
        int len = list.size();
        for (int i = 0; i < len; i++) {
            FieldInfo info = (FieldInfo) list.get(i);
            values = info.setValues(values);
        }
    }

}
