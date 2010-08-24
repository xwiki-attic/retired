package org.xwiki.model;

/**
 * @author MikhailKotelnikov
 */
public class XWikiException extends Exception {

    /**
     * 
     */
    public XWikiException() {
    }

    /**
     * @param message
     */
    public XWikiException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public XWikiException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public XWikiException(String message, Throwable cause) {
        super(message, cause);
    }

}
