/**
 * 
 */
package adnotatio.common.xml;

/**
 * @author kotelnikov
 */
public interface IXmlDocument {

    IXmlElement createElement(String name);

    IXmlText createTextNode(String content);

    IXmlElement getRootElement();

    void setRootElement(IXmlElement root);

}
