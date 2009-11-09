package org.xwiki.model;

import org.xwiki.model.dao.DocumentValue;

public class Document {

    private Space fSpace;

    private DocumentValue fValue;

    public Document(Space space, DocumentValue value) {
        fSpace = space;
        fValue = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Document))
            return false;
        Document doc = (Document) obj;
        return fValue.equals(doc.fValue);
    }

    public String getName() {
        return fValue.name;
    }

    @Override
    public int hashCode() {
        return fValue.hashCode();
    }

    @Override
    public String toString() {
        return fValue.toString();
    }

}
