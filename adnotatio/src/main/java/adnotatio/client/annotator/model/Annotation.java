/**
 * 
 */
package adnotatio.client.annotator.model;

import adnotatio.common.data.IPropertiesContainer;
import adnotatio.common.data.PropertiesContainer;

/**
 * Annotations are wrappers for {@link IPropertiesContainer} objects giving
 * simplified access to common properties like annotation length or annotation
 * start position.
 * 
 * @author kotelnikov
 */
public class Annotation {

    /**
     * The name of the property containing the length of the annotation
     */
    public final static String ANNOTATION_LEN = "annotationLen";

    /**
     * The selected text associated with the annotation
     */
    public final static String ANNOTATION_SELECTION = "selection";

    /**
     * The name of the property containing the position of the first word in the
     * annotation
     */
    public final static String ANNOTATION_START = "annotationStart";

    /**
     * Properties container
     */
    private IPropertiesContainer fProperties;

    /**
     * This constructor is used to initialize common annotation properties like
     * annotation start and annotation length
     * 
     * @param start the first word of the annotation
     * @param len the number of words covered by the annotation
     * @param text the annotation text
     */
    public Annotation(int start, int len, String text) {
        fProperties = new PropertiesContainer();
        setAnnotationStart(start);
        setAnnotationLen(len);
        setSelection(text);
    }

    /**
     * This constructor initializes the internal properties using the given
     * properties container
     * 
     * @param container properties container used to initialize internal fields
     */
    public Annotation(IPropertiesContainer container) {
        super();
        fProperties = container;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Annotation))
            return false;
        Annotation a = (Annotation) obj;
        return fProperties.equals(a.fProperties);
    }

    /**
     * Returns the position of the last word in the annotation. This method
     * returns the same value as the call
     * {@link IPropertiesContainer#getValueAsInt(String) getValueAsInt(ANNOTATION_LEN)}
     * 
     * @return the position of the last word in the annotation
     */
    public int getLength() {
        return fProperties.getValueAsInt(ANNOTATION_LEN, 0);
    }

    public IPropertiesContainer getProperties() {
        return fProperties;
    }

    public String getSelection() {
        return fProperties.getValueAsString(ANNOTATION_SELECTION);
    }

    /**
     * Returns the position of the first word of the annotation. This method
     * returns the same value as the call
     * {@link IPropertiesContainer#getValueAsInt(String) getValueAsInt(ANNOTATION_START)}
     * 
     * @return the position of the first word of the annotation
     */
    public int getStartPosition() {
        return fProperties.getValueAsInt(ANNOTATION_START, -1);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return fProperties.hashCode();
    }

    /**
     * Sets a new annotation length. The annotation length is the number of
     * words covered by this annotation.
     * 
     * @param len the number of words covered by the annotation
     */
    private void setAnnotationLen(int len) {
        fProperties.setValue(ANNOTATION_LEN, "" + len);
    }

    /**
     * Sets the first word of this annotation
     * 
     * @param start the first word covered by the annotation
     */
    public void setAnnotationStart(int start) {
        fProperties.setValue(ANNOTATION_START, "" + start);
    }

    /**
     * Replaces all internal values by the given values
     * 
     * @param properties properties to set
     */
    public void setProperties(IPropertiesContainer properties) {
        fProperties.setValues(properties);
    }

    /**
     * Sets the selected text
     * 
     * @param text the selected text
     */
    public void setSelection(String text) {
        fProperties.setValue(ANNOTATION_SELECTION, text);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return fProperties.toString();
    }

    /**
     * Updates all internal properties using values from the given annotation
     * 
     * @param annotation the annotation object
     */
    public void updateValues(Annotation annotation) {
        fProperties.updateValues(annotation.fProperties);
    }
}
