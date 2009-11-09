/**
 * 
 */
package adnotatio.renderer.templates;

import adnotatio.common.xml.IXmlElement;

/**
 * Field info factories are used to create new {@link FieldInfo} objects. To
 * extend the functionalities of templates it is possible to register instances
 * of this type in the {@link FieldInfoFactoryRegistry} using the
 * {@link FieldInfoFactoryRegistry#addFieldInfoFactory(String, String, IFieldInfoFactory)}
 * method.
 * 
 * @author kotelnikov
 */
public interface IFieldInfoFactory {

    /**
     * Returns a new field wrapper corresponding to the given template node
     * 
     * @param panel the parent widget where the resulting field will be inserted
     * @param e the XML element defining the widget
     * @return a new field wrapper corresponding to the given template node
     */
    FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e);
}