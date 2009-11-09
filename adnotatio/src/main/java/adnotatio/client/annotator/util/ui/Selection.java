/**
 * 
 */
package adnotatio.client.annotator.util.ui;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;

/**
 * This class provides an abstraction for selections in different browsers. It
 * contains "native" javascript code retrieving individual parameters of current
 * selections.
 * 
 * @author kotelnikov
 */
class Selection {

    /**
     * Returns the current selection of the browser
     * 
     * @return the current selection of the browser
     */
    public static Selection getSelection() {
        Selection selection = new Selection();
        return selection;
    }

    /**
     * The DOM element containing the end of the selected text
     */
    private Element fEndElement;

    /**
     * The DOM element containing the start position of the selected text
     */
    private Element fStartElement;

    /**
     * The selected text
     */
    private String fText;

    /**
     * This is a private constructor creating a new selection instance giving
     * access to the current browser selections
     */
    private Selection() {
        JavaScriptObject userSelection = loadSelection();
        fText = getTextFromSelection(userSelection);

        if (userSelection != null) {
            JavaScriptObject range = getRangeFromSelection(userSelection);
            if (range != null) {
                fStartElement = getStartElement(range);
                fEndElement = getEndElement(range);
            } else {
                fStartElement = null;
                fEndElement = null;
            }
        }
    }

    /**
     * Returns the DOM element containing the end of the selected text
     * 
     * @return the DOM element containing the end of the selected text
     */
    public Element getEndElement() {
        return fEndElement;
    }

    /**
     * This javascript method retrieves the end element containing selections
     * 
     * @param range the native range object corresponding to the selection
     * @return the element containing the end of the selection
     */
    protected native Element getEndElement(JavaScriptObject range) /*-{ 
       var e = range.endContainer;
       if (range.endContainer) {
           e  = range.endContainer;
           if (e.nodeType != 1) {
               e = e.parentNode;
           }
       } else if (range.parentElement) {
           e = range.parentElement();
       }
       return e; 
   }-*/;

    /**
     * Returns a native range object defining the browser's selection
     * 
     * @param selectionObject the selection object
     * @return a native range object defining the browser's selection
     */
    private native JavaScriptObject getRangeFromSelection(
        JavaScriptObject selectionObject)/*-{
        if (selectionObject.getRangeAt) {
            return (!selectionObject.isCollapsed || selectionObject.length > 0)
                ? selectionObject.getRangeAt(0)
                : null;
        } else { // Safari!
            var range = $doc.createRange();
            range.setStart(selectionObject.anchorNode,selectionObject.anchorOffset);
            range.setEnd(selectionObject.focusNode,selectionObject.focusOffset);
            return range;
        }
    }-*/;

    /**
     * Returns the DOM element containing start of the selected text
     * 
     * @return the DOM element containing start of the selected text
     */
    public Element getStartElement() {
        return fStartElement;
    }

    /**
     * This javascript method retrieves the start element containing selections
     * 
     * @param range the native range object corresponding to the selection
     * @return the element containing the start element containing selection
     */
    protected native Element getStartElement(JavaScriptObject range)/*-{ 
        var e = range.startContainer;
        if (e) {
            if (e.nodeType != 1) {
                e = e.parentNode;
            }
        } else if (range.parentElement) {
            e = range.parentElement();
        }
        return e; 
    }-*/;

    /**
     * Returns the selected text
     * 
     * @return the selected text
     */
    public String getText() {
        return fText;
    }

    /**
     * This javascript method retrieves the selected text
     * 
     * @param userSelection the native range object corresponding to the
     *        selection
     * @return the selected text
     */
    private native String getTextFromSelection(JavaScriptObject userSelection) /*-{
         var selectedText = userSelection;
         if (userSelection.text) selectedText = userSelection.text;
         return "" + selectedText;
     }-*/;

    /**
     * Loads and returns a native object corresponding to the text selection
     * 
     * @returns a native object corresponding to the text selection
     * 
     * <pre>
     * http://www.quirksmode.org/dom/range_intro.html
     * </pre>
     */
    private native JavaScriptObject loadSelection() /*-{
         var userSelection;
         if ($wnd.getSelection) { userSelection = $wnd.getSelection(); } 
         else if ($doc.getSelection) { userSelection = $doc.getSelection(); } 
         else if ($doc.selection) { userSelection = $doc.selection.createRange(); }
         return userSelection;
     }-*/;

}