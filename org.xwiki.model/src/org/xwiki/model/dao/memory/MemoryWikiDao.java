package org.xwiki.model.dao.memory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xwiki.model.dao.DocumentValue;
import org.xwiki.model.dao.IWikiDao;
import org.xwiki.model.dao.SpaceValue;
import org.xwiki.model.dao.UserValue;
import org.xwiki.model.dao.WikiValue;

/**
 * @author MikhailKotelnikov
 */
public class MemoryWikiDao implements IWikiDao {

    private Map<String, DocumentValue> fDocuments = new HashMap<String, DocumentValue>();

    private Map<String, SpaceValue> fSpaces = new HashMap<String, SpaceValue>();

    private Map<String, UserValue> fUsers = new HashMap<String, UserValue>();

    private Map<String, WikiValue> fWikis = new HashMap<String, WikiValue>();

    /**
     * 
     */
    public MemoryWikiDao() {
    }

    private String getKey(String wikiName, String spaceName) {
        return wikiName + "." + spaceName;
    }

    /**
     * @see org.xwiki.model.dao.IWikiDao#load(java.lang.String)
     */
    public WikiValue load(String name) {
        return fWikis.get(name);
    }

    public DocumentValue loadDocumentValue(
        String wikiName,
        String spaceName,
        String docName) {
        String key = getKey(getKey(wikiName, spaceName), docName);
        return fDocuments.get(key);
    }

    public SpaceValue loadSpaceInfo(String wikiName, String spaceName) {
        String spaceKey = getKey(wikiName, spaceName);
        return fSpaces.get(spaceKey);
    }

    public Set<String> loadSpaceNames(String wikiName) {
        Set<String> result = new HashSet<String>();
        String prefix = wikiName + ".";
        for (String name : fSpaces.keySet()) {
            if (name.startsWith(prefix))
                result.add(name.substring(prefix.length()));
        }
        return result;
    }

    public UserValue loadUserInfo(String login) {
        return fUsers.get(login);
    }

    /**
     * @see org.xwiki.model.dao.IWikiDao#store(org.xwiki.model.dao.WikiValue)
     */
    public void store(WikiValue value) {
        String name = value.name;
        fWikis.put(name, value);
    }

    public void storeDocumentInfo(DocumentValue value) {
        String key = getKey(getKey(value.wikiName, value.spaceName), value.name);
        fDocuments.put(key, value);
    }

    /**
     * @see org.xwiki.model.dao.IWikiDao#storeSpaceInfo(java.lang.String,
     *      org.xwiki.model.dao.SpaceValue)
     */
    public void storeSpaceInfo(String wikiName, SpaceValue spaceValue) {
        String spaceKey = getKey(wikiName, spaceValue.name);
        fSpaces.put(spaceKey, spaceValue);
    }

    public void storeUserInfo(UserValue userValue) {
        fUsers.put(userValue.login, userValue);
    }

}
