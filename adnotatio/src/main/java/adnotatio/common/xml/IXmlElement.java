/**
 * 
 */
package adnotatio.common.xml;

import java.util.Set;

/**
 * @author kotelnikov
 */
public interface IXmlElement extends IXmlNode {

    void appendChild(IXmlNode child);

    String getAttribute(String name);

    Set getAttributeNames();

    IXmlNode getChild(int pos);

    int getChildNumber();

    String getNodeName();

}
