/**
 * 
 */
package adnotatio.server.xml.dom;

import org.w3c.dom.CharacterData;

import adnotatio.common.xml.IXmlText;

/**
 * @author kotelnikov
 */
public class DomXmlText extends DomXmlNode implements IXmlText {

    /**
     * 
     */
    public DomXmlText(CharacterData characterData) {
        super(characterData);
    }

    /**
     * @see adnotatio.common.xml.IXmlText#getText()
     */
    public String getText() {
        CharacterData text = (CharacterData) fNode;
        return text.getData();
    }

}
