/**
 * 
 */
package adnotatio.common.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is a default java.util.Map-based implementation of the
 * {@link IPropertiesContainer} interface.
 * 
 * @author kotelnikov
 */
public class PropertiesContainer implements IPropertiesContainer {

    /**
     * Serializes the specified container as a JSON.
     * 
     * @param container the container to serialize
     * @param buf the string buffer instance where the serialized value should
     *        be written
     */
    public static void printContainer(
        PropertiesContainer container,
        StringBuffer buf,
        String shift) {
        buf.append("{");
        for (Iterator iterator = container.getNames().iterator(); iterator
            .hasNext();) {
            String key = (String) iterator.next();
            List list = container.getValues(key);
            buf.append("\n");
            buf.append(shift);
            buf.append("    ");
            buf.append(key);
            buf.append(": ");
            if (list.size() == 1) {
                printValue(list.get(0), buf, shift);
            } else {
                buf.append("[");
                for (Iterator j = list.iterator(); j.hasNext();) {
                    Object value = j.next();
                    printValue(value, buf, shift);
                    if (j.hasNext()) {
                        buf.append(", ");
                    }
                }
                buf.append("]");
            }
            if (iterator.hasNext()) {
                buf.append(", ");
            }
        }
        buf.append("\n");
        buf.append(shift);
        buf.append("}");
    }

    /**
     * Serializes the specified object as a JSON. If the given value is an
     * instance of the {@link IPropertiesContainer} then this method calls the
     * {@link #printContainer(PropertiesContainer, StringBuffer, String)} method
     * 
     * @param value the object to serialize
     * @param buf the string buffer where the resulting value should be written
     * @param shift the shift value used to print values
     */
    private static void printValue(Object value, StringBuffer buf, String shift) {
        if (value instanceof PropertiesContainer) {
            printContainer((PropertiesContainer) value, buf, shift + "    ");
        } else {
            buf.append("'");
            buf.append(value);
            buf.append("'");
        }
    }

    /**
     * The map containing all properties
     */
    private Map fMap = new HashMap();

    /**
     * The default constructor.
     */
    public PropertiesContainer() {
        super();
    }

    /**
     * @see adnotatio.common.data.IPropertiesContainer#addValue(java.lang.String,
     *      java.lang.Object)
     */
    public void addValue(String name, Object value) {
        List list = getList(name, true);
        list.add(value);
    }

    /**
     * @see adnotatio.common.data.IPropertiesContainer#addValues(java.lang.String,
     *      java.util.List)
     */
    public void addValues(String name, List values) {
        List list = getList(name, true);
        list.addAll(values);
    }

    /**
     * @see adnotatio.common.data.IPropertiesContainer#clear()
     */
    public void clear() {
        fMap.clear();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof PropertiesContainer))
            return false;
        PropertiesContainer container = (PropertiesContainer) obj;
        return fMap.equals(container.fMap);
    }

    /**
     * Returns a list of values corresponding to the specified key. If the
     * internal map does not contain the corresponding list of values and the
     * given <code>create</code> flag is <code>true</code> then this method
     * will create a new value list and put it in the map.
     * 
     * @param name the name of the property for which the map should be returned
     * @param create if this flag is <code>true</code> and there is no data
     *        list in the map then a new list is created
     * @return a list of values corresponding to the specified property key
     */
    private List getList(String name, boolean create) {
        List list = (List) fMap.get(name);
        if (list == null && create) {
            list = new ArrayList();
            fMap.put(name, list);
        }
        return list;
    }

    /**
     * @see adnotatio.common.data.IPropertiesContainer#getNames()
     */
    public Set getNames() {
        return fMap.keySet();
    }

    /**
     * @see adnotatio.common.data.IPropertiesContainer#getValue(String)
     */
    public Object getValue(String name) {
        List list = getList(name, false);
        return (String) (list != null && list.size() > 0 ? list.get(0) : null);
    }

    /**
     * @see adnotatio.common.data.IPropertiesContainer#getValueAsInt(java.lang.String,
     *      int)
     */
    public int getValueAsInt(String name, int defaultValue) {
        String value = getValueAsString(name);
        int result = defaultValue;
        try {
            result = Integer.parseInt(value);
        } catch (Exception e) {
            result = defaultValue;
        }
        return result;
    }

    /**
     * @see adnotatio.common.data.IPropertiesContainer#getValueAsString(java.lang.String)
     */
    public String getValueAsString(String name) {
        Object value = getValue(name);
        return value != null ? value.toString() : null;
    }

    /**
     * @see adnotatio.common.data.IPropertiesContainer#getValues(java.lang.String)
     */
    public List getValues(String name) {
        List list = getList(name, false);
        return list != null ? new ArrayList(list) : null;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return fMap.hashCode();
    }

    /**
     * @see adnotatio.common.data.IPropertiesContainer#removeValues(java.lang.String)
     */
    public List removeValues(String name) {
        return (List) fMap.remove(name);
    }

    /**
     * @see adnotatio.common.data.IPropertiesContainer#setValue(java.lang.String,
     *      Object)
     */
    public void setValue(String name, Object value) {
        List list = new ArrayList();
        list.add(value);
        fMap.put(name, list);
    }

    /**
     * @see adnotatio.common.data.IPropertiesContainer#setValues(adnotatio.common.data.IPropertiesContainer)
     */
    public void setValues(IPropertiesContainer properties) {
        clear();
        updateValues(properties);
    }

    /**
     * @see adnotatio.common.data.IPropertiesContainer#setValues(java.lang.String,
     *      java.util.List)
     */
    public void setValues(String name, List values) {
        List list = new ArrayList(values);
        fMap.put(name, list);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        PropertiesContainer container = this;
        printContainer(container, buf, "");
        return buf.toString();
    }

    /**
     * @see adnotatio.common.data.IPropertiesContainer#updateValues(adnotatio.common.data.IPropertiesContainer)
     */
    public void updateValues(IPropertiesContainer properties) {
        for (Iterator iterator = properties.getNames().iterator(); iterator
            .hasNext();) {
            String name = (String) iterator.next();
            List values = properties.getValues(name);
            setValues(name, values);
        }

    }

}
