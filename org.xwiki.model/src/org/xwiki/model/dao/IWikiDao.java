package org.xwiki.model.dao;

import java.util.Set;

/**
 * @author MikhailKotelnikov
 */
public interface IWikiDao {

    WikiValue load(String name);

    SpaceValue loadSpaceInfo(String wikiName, String spaceName);

    Set<String> loadSpaceNames(String wikiName);

    UserValue loadUserInfo(String login);

    void store(WikiValue value);

    void storeSpaceInfo(String wikiName, SpaceValue spaceValue);

    void storeUserInfo(UserValue userValue);

    DocumentValue loadDocumentValue(
        String wikiName,
        String spaceName,
        String docName);

    void storeDocumentInfo(DocumentValue value);

}
