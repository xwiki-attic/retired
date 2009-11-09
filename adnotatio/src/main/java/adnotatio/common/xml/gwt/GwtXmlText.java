/**
 * 
 */
package adnotatio.common.xml.gwt;

import adnotatio.common.xml.IXmlDocument;
import adnotatio.common.xml.IXmlText;

import com.google.gwt.xml.client.CharacterData;

/**
 * @author kotelnikov
 */
public class GwtXmlText extends GwtXmlNode implements IXmlText {

    /**
     * 
     */
    public GwtXmlText(CharacterData characterData) {
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
