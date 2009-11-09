package org.xwiki.model;

import org.xwiki.model.dao.DocumentValue;
import org.xwiki.model.dao.IWikiDao;
import org.xwiki.model.dao.SpaceValue;

/**
 * @author MikhailKotelnikov
 */
public class Space {

    private SpaceValue fValue;

    private Wiki fWiki;

    /**
     * @param session
     * @param value
     */
    public Space(Wiki wiki, SpaceValue value) {
        fWiki = wiki;
        fValue = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Space))
            return false;
        Space space = (Space) obj;
        return fValue.equals(space.fValue);
    }

    public Document getDocument(String docName) {
        IWikiDao dao = getWikiDao();
        DocumentValue value = dao.loadDocumentValue(
            fWiki.getName(),
            fValue.name,
            docName);
        return value != null ? new Document(this, value) : null;
    }

    public String getName() {
        return fValue.name;
    }

    private IWikiDao getWikiDao() {
        return fWiki.getWikiDao();
    }

    @Override
    public int hashCode() {
        return fValue.hashCode();
    }

    public Document newDocument(String docName) throws XWikiException {
        IWikiDao dao = getWikiDao();
        DocumentValue value = dao.loadDocumentValue(
            fWiki.getName(),
            fValue.name,
            docName);
        if (value != null)
            throw new XWikiException(
                "A document with such a name already exists. Name: '"
                    + docName
                    + "'.");
        value = new DocumentValue(fWiki.getName(), fValue.name, docName);
        dao.storeDocumentInfo(value);
        return new Document(this, value);
    }

    @Override
    public String toString() {
        return fValue.toString();
    }

}
