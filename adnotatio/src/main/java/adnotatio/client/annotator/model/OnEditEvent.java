/**
 * 
 */
package adnotatio.client.annotator.model;

/**
 * Events of this type are fired to notify about various stages of the
 * annotation editing process - they are fired when an annotation should be
 * edited (see {@link OnEditEvent#BEGIN}), when editing process was cancelled
 * (see {@link OnEditEvent#CANCEL}) or it should be saved (see
 * {@link OnEditEvent#SAVE}).
 * 
 * @author kotelnikov
 */
public class OnEditEvent extends AnnotationEvent {
    /**
     * The editing process is started
     */
    public final static int BEGIN = 0;

    /**
     * The editing process is canceled
     */
    public final static int CANCEL = 1;

    /**
     * The edited annotation should be saved
     */
    public final static int SAVE = 2;

    /**
     * The flag defining the editing phase. It can one of the following values:
     * {@link #BEGIN}, {@link #CANCEL} or {@link #SAVE}
     */
    public final int stage;

    /**
     * @param annotation the annotation used to edit
     * @param stage this is one of the following values: {@link #BEGIN},
     *        {@link #CANCEL} or {@link #SAVE}; if it is not one of these
     *        values then this method rises {@link IllegalArgumentException}
     */
    public OnEditEvent(Annotation annotation, int stage) {
        super(OnEditEvent.class, annotation);
        switch (stage) {
            case BEGIN:
            case CANCEL:
            case SAVE:
                break;
            default:
                throw new IllegalArgumentException();
        }
        this.stage = stage;
    }
}
