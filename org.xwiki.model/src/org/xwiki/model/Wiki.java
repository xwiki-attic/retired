package org.xwiki.model;

import java.util.HashSet;
import java.util.Set;

import org.xwiki.model.dao.IWikiDao;
import org.xwiki.model.dao.SpaceValue;
import org.xwiki.model.dao.WikiValue;

public class Wiki {

    private Session fSession;

    private WikiValue fValue;

    public Wiki(Session session, WikiValue wikiValue) {
        fSession = session;
        fValue = wikiValue;
    }

    private boolean checkAccess(SpaceValue spaceValue) {
        return spaceValue != null;
    }

    public Set<Space> getAllSpaces() {
        IWikiDao dao = getWikiDao();
        Set<Space> spaces = new HashSet<Space>();
        Set<String> spaceNames = dao.loadSpaceNames(fValue.name);
        for (String spaceName : spaceNames) {
            Space space = getSpace(spaceName);
            if (space != null)
                spaces.add(space);
        }
        return spaces;
    }

    public Document getDocument(String spaceName, String docName) {
        Space space = getSpace(spaceName);
        return space != null ? space.getDocument(docName) : null;
    }

    public String getName() {
        return fValue.name;
    }

    public Space getSpace(String spaceName) {
        IWikiDao dao = getWikiDao();
        SpaceValue spaceValue = dao.loadSpaceInfo(fValue.name, spaceName);
        return checkAccess(spaceValue) ? new Space(this, spaceValue) : null;
    }

    IWikiDao getWikiDao() {
        return fSession.getWikiDao();
    }

    public Space newSpace(String spaceName) throws XWikiException {
        if (spaceName == null)
            throw new XWikiException("Space name is not defined");
        IWikiDao dao = getWikiDao();
        SpaceValue spaceValue = dao.loadSpaceInfo(fValue.name, spaceName);
        if (spaceValue != null)
            throw new XWikiException(
                "A space with such a name already exists. Name: '"
                    + spaceName
                    + "'. ");
        spaceValue = new SpaceValue(spaceName);
        dao.storeSpaceInfo(fValue.name, spaceValue);
        return new Space(this, spaceValue);
    }

}
