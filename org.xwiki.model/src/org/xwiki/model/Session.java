package org.xwiki.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xwiki.model.dao.IWikiDao;
import org.xwiki.model.dao.WikiValue;

/**
 * @author MikhailKotelnikov
 */
public class Session {

    private Set<String> fWikiNames = new HashSet<String>();

    private Farm fFarm;

    private User fUser;

    /**
     * @param farm
     * @param user
     */
    public Session(Farm farm, User user) {
        fFarm = farm;
        fUser = user;
    }

    /**
     * Returns the user.
     * 
     * @return the user.
     */
    public User getUser() {
        return fUser;
    }

    public Set<Wiki> getAvailableWikis() {
        IWikiDao dao = getWikiDao();
        Set<Wiki> result = new HashSet<Wiki>();
        for (String name : fWikiNames) {
            WikiValue wikiValue = dao.load(name);
            Wiki wiki = new Wiki(this, wikiValue);
            result.add(wiki);
        }
        return result;
    }

    IWikiDao getWikiDao() {
        return fFarm.getWikiDao();
    }

    public Wiki getWiki(String name) {
        IWikiDao dao = getWikiDao();
        WikiValue value = dao.load(name);
        return value != null ? new Wiki(this, value) : null;
    }

    public Wiki newWiki(String name) throws XWikiException {
        IWikiDao dao = getWikiDao();
        WikiValue value = dao.load(name);
        if (value != null)
            throw new XWikiException(
                "A wiki with such a name already exists. Name: '" + name + "'.");
        value = new WikiValue(name);
        dao.store(value);
        return value != null ? new Wiki(this, value) : null;
    }

}
