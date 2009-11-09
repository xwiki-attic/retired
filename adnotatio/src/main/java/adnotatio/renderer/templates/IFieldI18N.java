/**
 * 
 */
package adnotatio.renderer.templates;

/**
 * Instances of this type are used to provide internationalized messages and
 * labels used by templates
 * 
 * @author kotelnikov
 */
public interface IFieldI18N {

    /**
     * "null-implementation" of this interface.
     */
    IFieldI18N NULL = new IFieldI18N() {
        public String getString(String name) {
            return null;
        }
    };

    /**
     * Returns a localized value corresponding to the specified name
     * 
     * @param name the name of the localized parameter
     * @return a localized value corresponding to the specified name
     */
    String getString(String name);

}
