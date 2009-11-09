/**
 * 
 */
package adnotatio.renderer.templates;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a default map-based implementation of the {@link IFieldI18N}
 * interface. It provides a simple way to register key/value pairs. The
 * {@link FieldI18N#setValues(String)} method can be used to set serialized
 * key/value pairs. The given string should represent a list of key/value pairs
 * where each pair is on its own line. Keys and values should be separated by
 * the "=" or space (" ") symbols.
 * 
 * @author kotelnikov
 */
public class FieldI18N implements IFieldI18N {

    /**
     * This map contains internationalized values for keys
     */
    private Map fMap = new HashMap();

    /**
     * 
     */
    public FieldI18N() {
        super();
    }

    /**
     * This constructor is used to set serialized key/value pairs
     * 
     * @param text the serialized key/value pairs
     */
    public FieldI18N(String text) {
        setValues(text);
    }

    /**
     * @see adnotatio.renderer.templates.IFieldI18N#getString(java.lang.String)
     */
    public String getString(String name) {
        return (String) fMap.get(name);
    }

    /**
     * @param key
     * @param value
     */
    public void setValue(String key, String value) {
        fMap.put(key, value);
    }

    /**
     * This method is used to set serialized key/value pairs. The given string
     * should represent a list of key/value pairs where each pair is on its own
     * line. Keys and values should be separated by one of the following
     * symbols: '=', '\t' (tabulation symbol) or ' ' (space).
     * 
     * @param text the serialized key/value pairs
     */
    public void setValues(String text) {
        fMap.clear();
        String[] lines = text.split("[\n\r]+");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line == null)
                continue;
            line = line.trim();
            if ("".equals(line) || line.startsWith("#"))
                continue;
            int idx = line.indexOf('=');
            if (idx < 0) {
                idx = line.indexOf(' ');
                if (idx < 0) {
                    idx = line.indexOf('\t');
                }
            }
            String key = line;
            String value = "";
            if (idx >= 0) {
                key = line.substring(0, idx);
                value = line.substring(idx + 1);
                key = key.trim();
                value = value.trim();
            }
            fMap.put(key, value);
        }
    }

}
