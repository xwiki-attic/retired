package org.xwiki.model.dao;

/**
 * @author MikhailKotelnikov
 */
public class DocumentValue {

    public String name;

    public String spaceName;

    public String wikiName;

    /**
     * @param wikiName
     * @param spaceName
     * @param docName
     */
    public DocumentValue(String wikiName, String spaceName, String docName) {
        this.wikiName = wikiName;
        this.spaceName = spaceName;
        this.name = docName;
    }

}
