/**
 * 
 */
package adnotatio.common.data;

import java.util.List;
import java.util.Set;

/**
 * A simple Map-like interface providing possibilities to store multiple values
 * for each key. It contains some additional utility methods simplifying work
 * with values.
 * 
 * @author kotelnikov
 */
public interface IPropertiesContainer {

    /**
     * Adds the specified value to the list of existing values with the given
     * name. If there is no values exist then this method just creates a new
     * list of values with this value and associate it with the name.
     * 
     * @param name the name of the property
     * @param value the value to add
     */
    void addValue(String name, Object value);

    /**
     * Adds the specified values to the existing values (if any) associated with
     * the given name. If this annotation does not contain any values then this
     * method just set the given values.
     * 
     * @param name the name of the properties to add
     * @param values the values to add
     */
    void addValues(String name, List values);

    /**
     * Removes all properties
     */
    void clear();

    /**
     * Returns a set of all property names
     * 
     * @return a set of all property names
     */
    Set getNames();

    /**
     * Returns the first value from the list of values associated with the given
     * name. This method can return <code>null</code> if there is no values
     * corresponding to the given name.
     * 
     * @param name the name of the property to return
     * @return the first value from the list of values associated with the
     *         specified name; it can be <code>null</code> if there is no
     *         values corresponding to the given name.
     */
    Object getValue(String name);

    /**
     * Returns the integer representation of the first value corresponding to
     * the given name
     * 
     * @param name the name of the property to return
     * @param defaultValue the default value; this value is returned if there is
     *        no values with the specified name
     * @return the integer representation of the first value corresponding to
     *         the given name
     */
    int getValueAsInt(String name, int defaultValue);

    /**
     * Returns the string representation of the value with the specified name
     * 
     * @param name the name of the value to return
     * @return the string representation of the value with the specified name
     */
    String getValueAsString(String name);

    /**
     * Returns a list of all values associated with the specified name. This
     * method returns <code>null</code> if there is no values corresponding to
     * the given name.
     * 
     * @param name the name of the property
     * @return a list of all values associated with the specified name; it can
     *         be <code>null</code> if there is no values corresponding to the
     *         given name.
     */
    List getValues(String name);

    /**
     * This method removes all values associated with the given name and returns
     * the removed list. This method returns <code>null</code> if there is no
     * values corresponding to the given name.
     * 
     * @param name the name of the property to remove
     * @return the removed list of values associated with the specified name; it
     *         can be <code>null</code> if there is no values associated with
     *         the name
     */
    List removeValues(String name);

    /**
     * Replaces all existing values associated with the specified name by the
     * given value.
     * 
     * @param name the name of the property
     * @param value the value to set
     */
    void setValue(String name, Object value);

    /**
     * Cleans up this object and copies all values from the given properties
     * object.
     * 
     * @param properties the values to copy
     */
    void setValues(IPropertiesContainer properties);

    /**
     * Sets a new array of values associated with the specified name. This
     * method replaces all existing values by new ones
     * 
     * @param name the name of the properties to set
     * @param values the values to set
     */
    void setValues(String name, List values);

    /**
     * Replaces all properties by the values from the specified properties
     * object. All existing values are not changed.
     * 
     * @param properties the values to copy
     */
    void updateValues(IPropertiesContainer properties);

}
