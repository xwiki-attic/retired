/**
 * 
 */
package adnotatio.client.annotator.util;

import java.util.List;

/**
 * @author kotelnikov
 */
public abstract class PrintXMLParser extends SimpleXMLParser {

    protected void onBeginTag(String beginTag, List params) {
        print("<");
        print(beginTag);
        for (int i = 0; i < params.size(); i++) {
            String[] param = (String[]) params.get(i);
            print(" ");
            print(param[0]);
            print("=\'");
            String value = param[1];
            int len = value.length();
            if (len >= 2) {
                char ch = value.charAt(0);
                if ((ch == '\'' || ch == '"') && value.charAt(len - 1) == ch) {
                    value = value.substring(1, len - 1);
                }
            }
            print(value);
            print("\'");
        }
        print(">");
    }

    protected void onEndTag(String endTag) {
        print("</");
        print(endTag);
        print(">");
    }

    protected void onEntity(String entity) {
        print(entity);
    }

    protected void onSpaces(String str) {
        print(str);
    }

    protected void onSpecialSymbols(String str) {
        print(str);
    }

    protected void onWord(String word) {
        print(word);
    }

    protected abstract void print(String str);
}
