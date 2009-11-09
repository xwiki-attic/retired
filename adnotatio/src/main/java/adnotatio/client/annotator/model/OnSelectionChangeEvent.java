/**
 * 
 */
package adnotatio.client.annotator.model;

import adnotatio.common.event.Event;

/**
 * Events of this type are rised to notify that the text selection was changed.
 * 
 * @author kotelnikov
 */
public class OnSelectionChangeEvent extends Event {

    private boolean fEnabled;

    private int fSelectionLen;

    private int fSelectionPos;

    private String fSelectionText;

    /**
     * 
     */
    public OnSelectionChangeEvent(boolean enabled) {
        super(OnSelectionChangeEvent.class);
        fEnabled = enabled;
    }

    public int getSelectionLen() {
        return fSelectionLen;
    }

    public int getSelectionPos() {
        return fSelectionPos;
    }

    public String getSelectionText() {
        return fSelectionText;
    }

    public boolean isEnabled() {
        return fEnabled;
    }

    public void setSelectionLen(int len) {
        fSelectionLen = len;
    }

    public void setSelectionPos(int pos) {
        fSelectionPos = pos;
    }

    public void setSelectionText(String text) {
        fSelectionText = text;
    }

}
